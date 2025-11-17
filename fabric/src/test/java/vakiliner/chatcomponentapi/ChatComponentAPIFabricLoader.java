package vakiliner.chatcomponentapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader implements ModInitializer {
	public static final FabricParser PARSER = new FabricParser();
	public static final Logger LOGGER = LogManager.getLogger("chatcomponentapi");
	private static final List<Throwable> ERRORS = new ArrayList<>();
	private static int failCount = 0;

	public void onInitialize() {
		LOGGER.info("Tests started");
		test("Parse text component", () -> {
			Component input = new TextComponent("123");
			ChatComponent test = FabricParser.fabric(input);
			Component output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse translatable component", () -> {
			Component input = new TranslatableComponent("123");
			ChatComponent test = FabricParser.fabric(input);
			Component output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse selector component", () -> {
			Component input = new SelectorComponent("123");
			ChatComponent test = FabricParser.fabric(input);
			Component output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse click event", () -> {
			for (Action action : Action.values()) {
				ClickEvent input = new ClickEvent(action, "/test");
				ChatClickEvent test = FabricParser.fabric(input);
				ClickEvent output = FabricParser.fabric(test);
				if (!input.equals(output)) {
					return false;
				}
			}
			return true;
		});
		test("Parse hover event", () -> {
			HoverEvent[] input = { new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("123")), new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityTooltipInfo(EntityType.CREEPER, UUID.randomUUID(), new TextComponent("Hello"))), new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackInfo(new ItemStack(Items.DIRT, 15))) };
			HoverEvent[] output = new HoverEvent[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatHoverEvent<?> test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse resource location", () -> {
			ResourceLocation input = new ResourceLocation("chatcomponentapi", "id");
			ChatId test = FabricParser.fabric(input);
			ResourceLocation output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse chat type", () -> {
			ChatType[] input = { ChatType.CHAT, ChatType.SYSTEM };
			ChatType[] output = new ChatType[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatMessageType test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("ChatComponentWithLegacyText", () -> {
			String legacyText = "legacy";
			ChatComponent chatComponent = new ChatTextComponent("123");
			ChatComponentWithLegacyText chatComponentWithLegacyText = chatComponent.withLegacyText(legacyText);
			return chatComponentWithLegacyText.toLegacyText().equals(legacyText) && chatComponentWithLegacyText.getComponent() == chatComponent;
		});
		LOGGER.info("Fails " + failCount);
		ERRORS.forEach(Throwable::printStackTrace);
	}

	@SuppressWarnings("unused")
	private static boolean test(String name, Runnable runnable) {
		return test(name, () -> {
			runnable.run();
			return true;
		});
	}

	private static boolean test(String name, BooleanSupplier runnable) {
		final boolean success;
		final Throwable error;
		{
			boolean output = false;
			Throwable fail = null;
			try {
				output = runnable.getAsBoolean();
			} catch (Throwable err) {
				fail = err;
			}
			success = output;
			error = fail;
		}
		if (!success) {
			failCount++;
			ERRORS.add(new RuntimeException("Test failed: " + name, error));
		}
		return success;
	}
}