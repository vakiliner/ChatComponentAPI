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
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import vakiliner.chatcomponentapi.base.ChatCommandSender;
import vakiliner.chatcomponentapi.base.ChatPlayer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentWithLegacyText;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
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
			ChatComponent input = new ChatTextComponent("123");
			Component test = FabricParser.fabric(input);
			ChatComponent output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse translatable component", () -> {
			ChatComponent input = new ChatTranslateComponent(null, "123");
			Component test = FabricParser.fabric(input);
			ChatComponent output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse selector component", () -> {
			ChatComponent input = new ChatSelectorComponent("123");
			Component test = FabricParser.fabric(input);
			ChatComponent output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse text component with style", () -> {
			ChatTextComponent input = new ChatTextComponent("Hey");
			input.setBold(true);
			input.setItalic(false);
			input.setUnderlined(true);
			input.setStrikethrough(false);
			input.setObfuscated(true);
			Component test = FabricParser.fabric(input);
			ChatComponent output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse click event", () -> {
			for (ChatClickEvent.Action action : ChatClickEvent.Action.values()) {
				ChatClickEvent input = new ChatClickEvent(action, "/chatcomponentapi test");
				ClickEvent test = FabricParser.fabric(input);
				ChatClickEvent output = FabricParser.fabric(test);
				if (!input.equals(output)) return false;
			}
			return true;
		});
		test("Parse hover event", () -> {
			ChatHoverEvent<?>[] input = { new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("123")), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, new ChatHoverEvent.ShowEntity(ChatId.parse("creeper"), UUID.randomUUID(), new ChatTextComponent("Hello"))), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, new ChatHoverEvent.ShowItem(ChatId.parse("dirt"), 15)) };
			ChatHoverEvent<?>[] output = new ChatHoverEvent[input.length];
			for (int i = 0; i < input.length; i++) {
				HoverEvent test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse resource location", () -> {
			ChatId input = new ChatId("chatcomponentapi", "id");
			ResourceLocation test = FabricParser.fabric(input);
			ChatId output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse chat type", () -> {
			ChatMessageType[] input = { ChatMessageType.CHAT, ChatMessageType.SYSTEM };
			ChatMessageType[] output = new ChatMessageType[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatType test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse text component with click & hover events", () -> {
			ChatComponent input = new ChatTextComponent("Hello");
			input.setClickEvent(new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, "/chatcomponentapi test"));
			input.setHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("world")));
			Component test = FabricParser.fabric(input);
			ChatComponent output = FabricParser.fabric(test);
			return input.equals(output);
		});
		test("Parse chat formatting", () -> {
			ChatTextFormat[] input = ChatTextFormat.values();
			ChatTextFormat[] output = new ChatTextFormat[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatFormatting test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse chat colors", () -> {
			List<ChatNamedColor> rawInput = Arrays.asList(ChatTextFormat.values()).stream().filter((f) -> !f.isFormat()).map(ChatNamedColor::getByFormat).collect(Collectors.toList());
			ChatTextColor[] input = rawInput.toArray(new ChatTextColor[rawInput.size()]);
			ChatTextColor[] output = new ChatTextColor[input.length];
			for (int i = 0; i < input.length; i++) {
				TextColor test = FabricParser.fabric(input[i]);
				output[i] = FabricParser.fabric(test);
			}
			return Arrays.equals(input, output);
		});
		test("ChatComponentWithLegacyText", () -> {
			ChatTextComponent legacyComponent = new ChatTextComponent("legacy");
			ChatComponent chatComponent = new ChatTextComponent("123");
			ChatComponentWithLegacyText chatComponentWithLegacyText = chatComponent.withLegacyComponent(legacyComponent);
			return chatComponentWithLegacyText.toLegacyText().equals(legacyComponent.toLegacyText()) && chatComponentWithLegacyText.getComponent() == chatComponent;
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

	private static void clearTests() {
		testCount = 0;
		failCount = 0;
		ERRORS.clear();
	}
}