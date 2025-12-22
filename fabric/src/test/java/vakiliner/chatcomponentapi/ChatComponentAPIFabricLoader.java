package vakiliner.chatcomponentapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.SelectorComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.fabric.FabricParser;

public class ChatComponentAPIFabricLoader implements ModInitializer, CommandRegistrationCallback {
	public static final FabricParser PARSER = new FabricParser();
	public static final Logger LOGGER = LogManager.getLogger("chatcomponentapi");
	private static final List<Throwable> ERRORS = new ArrayList<>();
	private static int testCount = 0;
	private static int failCount = 0;

	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(this);
		startTests(ChatComponentAPIFabricLoader::startTests);
	}

	public void register(CommandDispatcher<CommandSourceStack> dispatcher, boolean dedicated) {
		dispatcher.register(testCommand());
	}

	private static LiteralArgumentBuilder<CommandSourceStack> testCommand() {
		LiteralArgumentBuilder<CommandSourceStack> chatcomponentapi = LiteralArgumentBuilder.literal("chatcomponentapi");
		LiteralArgumentBuilder<CommandSourceStack> test = LiteralArgumentBuilder.literal("test");
		return chatcomponentapi.requires((stack) -> {
			return stack.hasPermission(2);
		}).then(test.executes((context) -> startTests(() -> {
			CommandSourceStack commandSourceStack = context.getSource();
			CommandSource commandSource = commandSourceStack.getEntity();
			if (commandSource == null) {
				commandSource = commandSourceStack.getServer();
			}
			startTests(commandSource);
		})));
	}

	public static void startTests() {
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
		test("Parse text component with style", () -> {
			Component input = new TextComponent("Hey").withStyle(Style.EMPTY.withBold(true).withItalic(false).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/chatcomponentapi test")));
			ChatComponent test = FabricParser.fabric(input);
			Component output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse text component with style hover event", () -> {
			Component input = new TextComponent("Hello").withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent("world"))));
			ChatComponent test = FabricParser.fabric(input);
			Component output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse click event", () -> {
			for (ClickEvent.Action action : ClickEvent.Action.values()) {
				ClickEvent input = new ClickEvent(action, "/chatcomponentapi test");
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
		test("Parse chat formatting", () -> {
			ChatFormatting[] input = ChatFormatting.values();
			ChatFormatting[] output = new ChatFormatting[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatTextFormat test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse chat colors", () -> {
			List<TextColor> rawInput = Arrays.asList(ChatFormatting.values()).stream().filter((f) -> !f.isFormat()).map(TextColor::fromLegacyFormat).collect(Collectors.toList());
			TextColor[] input = rawInput.toArray(new TextColor[rawInput.size()]);
			TextColor[] output = new TextColor[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatNamedColor test = (ChatNamedColor) FabricParser.fabric(input[i]);
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
	}

	public static void startTests(CommandSource commandSource) {
		ChatCommandSender chatCommandSender = PARSER.toChatCommandSender(commandSource);
		test("Send message", () -> {
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"));
		});
		test("Send message with message type", () -> {
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.CHAT, null);
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.SYSTEM, null);
		});
		test("Send message with uuid", () -> {
			ChatPlayer chatPlayer = chatCommandSender instanceof ChatPlayer ? (ChatPlayer) chatCommandSender : null;
			UUID uuid = chatPlayer != null ? chatPlayer.getUniqueId() : null;
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.CHAT, uuid);
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.SYSTEM, uuid);
		});
	}

	private static int startTests(Runnable runnable) {
		LOGGER.info("Tests started");
		runnable.run();
		int tests = testCount;
		int fails = failCount;
		LOGGER.info("Fails " + fails + '/' + testCount);
		ERRORS.forEach(Throwable::printStackTrace);
		clearTests();
		return fails > 0 ? -1 : tests;
	}

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

	private static void clearTests() {
		testCount = 0;
		failCount = 0;
		ERRORS.clear();
	}
}