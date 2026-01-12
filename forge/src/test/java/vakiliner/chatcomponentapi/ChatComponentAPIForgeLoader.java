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
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
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
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;
import vakiliner.chatcomponentapi.forge.ForgeParser;

@Mod("chatcomponentapi")
@EventBusSubscriber(modid = "chatcomponentapi", bus = Bus.FORGE)
public class ChatComponentAPIForgeLoader {
	public static final ForgeParser PARSER = new ForgeParser();
	public static final Logger LOGGER = LogManager.getLogger("chatcomponentapi");
	private static final List<Throwable> ERRORS = new ArrayList<>();
	private static int testCount = 0;
	private static int failCount = 0;

	public ChatComponentAPIForgeLoader() {
		MinecraftForge.EVENT_BUS.register(this);
		startTests(ChatComponentAPIForgeLoader::startTests);
	}

	@SubscribeEvent
	public void onRegisterCommands(FMLServerStartingEvent event) {
		CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommands().getDispatcher();
		dispatcher.register(testCommand());
	}

	private static LiteralArgumentBuilder<CommandSource> testCommand() {
		LiteralArgumentBuilder<CommandSource> chatcomponentapi = LiteralArgumentBuilder.literal("chatcomponentapi");
		LiteralArgumentBuilder<CommandSource> test = LiteralArgumentBuilder.literal("test");
		return chatcomponentapi.requires((stack) -> {
			return stack.hasPermission(2);
		}).then(test.executes((context) -> startTests(() -> {
			CommandSource stack = context.getSource();
			ICommandSource commandSource = stack.getEntity();
			if (commandSource == null) {
				commandSource = stack.getServer();
			}
			startTests(commandSource);
		})));
	}

	public static void startTests() {
		test("Parse ChatTextComponent", () -> {
			ChatComponent input = new ChatTextComponent("123");
			ITextComponent test = ForgeParser.forge(input);
			ChatComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatTranslateComponent", () -> {
			ChatComponent input = new ChatTranslateComponent(null, "123");
			ITextComponent test = ForgeParser.forge(input);
			ChatComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatSelectorComponent", () -> {
			ChatComponent input = new ChatSelectorComponent("123");
			ITextComponent test = ForgeParser.forge(input);
			ChatComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatClickEvent", () -> {
			for (ChatClickEvent.Action action : ChatClickEvent.Action.values()) {
				ChatClickEvent input = new ChatClickEvent(action, "/chatcomponentapi test");
				ClickEvent test = ForgeParser.forge(input);
				ChatClickEvent output = ForgeParser.forge(test);
				if (!input.equals(output)) return false;
			}
			return true;
		});
		test("Parse ChatHoverEvent", () -> {
			ChatHoverEvent<?>[] input = { new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("123")), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, new ChatHoverEvent.ShowEntity(ChatId.parse("creeper"), UUID.randomUUID(), new ChatTextComponent("Hello"))), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, new ChatHoverEvent.ShowItem(ChatId.parse("dirt"), 15)) };
			ChatHoverEvent<?>[] output = new ChatHoverEvent[input.length];
			for (int i = 0; i < input.length; i++) {
				HoverEvent test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatId", () -> {
			ChatId input = new ChatId("chatcomponentapi", "id");
			ResourceLocation test = ForgeParser.forge(input);
			ChatId output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatMessageType", () -> {
			ChatMessageType[] input = { ChatMessageType.CHAT, ChatMessageType.SYSTEM };
			ChatMessageType[] output = new ChatMessageType[input.length];
			for (int i = 0; i < input.length; i++) {
				ChatType test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatTextFormat", () -> {
			ChatTextFormat[] input = ChatTextFormat.values();
			ChatTextFormat[] output = new ChatTextFormat[input.length];
			for (int i = 0; i < input.length; i++) {
				TextFormatting test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatTextColor", () -> {
			List<ChatNamedColor> rawInput = Arrays.asList(ChatTextFormat.values()).stream().filter(ChatTextFormat::isColor).map(ChatNamedColor::getByFormat).collect(Collectors.toList());
			ChatTextColor[] input = rawInput.toArray(new ChatTextColor[rawInput.size()]);
			ChatTextColor[] output = new ChatTextColor[input.length];
			for (int i = 0; i < input.length; i++) {
				Color test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatStyle", () -> {
			ChatStyle input = ChatStyle.newBuilder().withColor(ChatNamedColor.GRAY).withBold(true).withItalic(false).withUnderlined(null).withStrikethrough(false).withObfuscated(true).withInsertion("Test").withFont(ChatId.parse("default")).withClickEvent(new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, "/chatcomponentapi test")).withHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("world"))).build();
			Style test = ForgeParser.forge(input);
			ChatStyle output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("ChatComponentWithLegacyText", () -> {
			ChatTextComponent legacyComponent = new ChatTextComponent("legacy");
			ChatComponent chatComponent = new ChatTextComponent("123");
			ChatComponentWithLegacyText chatComponentWithLegacyText = chatComponent.withLegacyComponent(legacyComponent);
			return chatComponentWithLegacyText.toLegacyText().equals(legacyComponent.toLegacyText()) && chatComponentWithLegacyText.getComponent() == chatComponent;
		});
		test("Looping ChatComponent extra", () -> {
			ChatComponent component1 = new ChatTextComponent("123");
			ChatComponent component2 = new ChatTextComponent("456");
			component1.append(component2);
			try {
				component2.append(component1);
			} catch (Throwable err) {
				component2.append(component1.clone());
			}
			component1.clone();
		});
	}

	public static void startTests(ICommandSource commandSource) {
		ChatCommandSender chatCommandSender = PARSER.toChatCommandSender(commandSource);
		test("Send message", () -> {
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"));
		});
		test("Send message with ChatMessageType", () -> {
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.CHAT, null);
			chatCommandSender.sendMessage(new ChatTextComponent("Hello world"), ChatMessageType.SYSTEM, null);
		});
		test("Send message with ChatMessageType & UUID", () -> {
			ChatPlayer chatPlayer = chatCommandSender instanceof ChatPlayer ? (ChatPlayer) chatCommandSender : null;
			UUID uuid = chatPlayer != null ? chatPlayer.getUniqueId() : Util.NIL_UUID;
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