package vakiliner.chatcomponentapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.SelectorTextComponent;
import net.minecraftforge.fml.common.Mod;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;

@Mod("chatcomponentapi")
public class ChatComponentAPIForgeLoader {
	public static final ForgeParser PARSER = new ForgeParser();
	public static final Logger LOGGER = LogManager.getLogger("chatcomponentapi");
	private static final List<Throwable> ERRORS = new ArrayList<>();
	private static int testCount = 0;
	private static int failCount = 0;

	public ChatComponentAPIForgeLoader() {
		LOGGER.info("Tests started");
		test("Parse text component", () -> {
			ITextComponent input = new StringTextComponent("123");
			ChatComponent test = ForgeParser.forge(input);
			ITextComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse translatable component", () -> {
			ITextComponent input = new TranslationTextComponent("123");
			ChatComponent test = ForgeParser.forge(input);
			ITextComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse selector component", () -> {
			ITextComponent input = new SelectorTextComponent("123");
			ChatComponent test = ForgeParser.forge(input);
			ITextComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse click event", () -> {
			for (ClickEvent.Action action : ClickEvent.Action.values()) {
				ClickEvent input = new ClickEvent(action, "/test");
				ChatClickEvent test = ForgeParser.forge(input);
				ClickEvent output = ForgeParser.forge(test);
				if (!input.equals(output)) {
					return false;
				}
			}
			return true;
		});
		test("Parse hover event", () -> {
			HoverEvent[] input = { new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("123")), new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityHover(EntityType.CREEPER, UUID.randomUUID(), new StringTextComponent("Hello"))), new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemHover(new ItemStack(Items.DIRT, 15))) };
			HoverEvent[] output = new HoverEvent[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatHoverEvent<?> test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse resource location", () -> {
			ResourceLocation input = new ResourceLocation("chatcomponentapi", "id");
			ChatId test = ForgeParser.forge(input);
			ResourceLocation output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse chat type", () -> {
			ChatType[] input = { ChatType.CHAT, ChatType.SYSTEM };
			ChatType[] output = new ChatType[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatMessageType test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("ChatComponentWithLegacyText", () -> {
			String legacyText = "legacy";
			ChatComponent chatComponent = new ChatTextComponent("123");
			ChatComponentWithLegacyText chatComponentWithLegacyText = chatComponent.withLegacyText(legacyText);
			return chatComponentWithLegacyText.toLegacyText().equals(legacyText) && chatComponentWithLegacyText.getComponent() == chatComponent;
		});
		LOGGER.info("Fails " + failCount + '/' + testCount);
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
		testCount++;
		if (!success) {
			failCount++;
			ERRORS.add(new RuntimeException("Test failed: " + name, error));
		}
		return success;
	}
}