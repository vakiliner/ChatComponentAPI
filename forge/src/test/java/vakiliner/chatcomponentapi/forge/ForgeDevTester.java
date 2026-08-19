package vakiliner.chatcomponentapi.forge;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import vakiliner.chatcomponentapi.base.DevTester;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatMessageType;
import vakiliner.chatcomponentapi.common.ChatNamedColor;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.common.ChatTextFormat;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class ForgeDevTester extends DevTester {
	public static final Logger LOGGER = LogManager.getLogger("chatcomponentapi");
	private final ForgeParser parser;

	public ForgeDevTester(ForgeParser parser) {
		this.parser = parser;
	}

	@Override
	protected void log(String message) {
		LOGGER.info(message);
	}

	@Override
	public void startTests() {
		super.startTests();
		this.startTestsForge();
	}

	public final void startTestsForge() {
		test("Parse ChatTextComponent", () -> {
			ChatTextComponent input = new ChatTextComponent("123");
			Component test = ForgeParser.forge(input);
			ChatComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatTranslateComponent", () -> {
			ChatTranslateComponent input = new ChatTranslateComponent(null, "123");
			Component test = ForgeParser.forge(input);
			ChatComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatSelectorComponent", () -> {
			ChatSelectorComponent input = new ChatSelectorComponent("123");
			if (this.parser.supportsSeparatorInSelector()) input.setSeparator(new ChatTextComponent(" | "));
			Component test = ForgeParser.forge(input);
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
			ChatHoverEvent<?>[] input = { new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("123")), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, new ChatHoverEvent.ShowEntity(ChatId.of("creeper"), UUID.randomUUID(), new ChatTextComponent("Hello"))), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, new ChatHoverEvent.ShowItem(ChatId.of("dirt"), 15)) };
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
				ChatFormatting test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatTextColor", () -> {
			List<ChatNamedColor> rawInput = Arrays.asList(ChatTextFormat.values()).stream().filter(ChatTextFormat::isColor).map(ChatNamedColor::getByFormat).collect(Collectors.toList());
			ChatTextColor[] input = rawInput.toArray(new ChatTextColor[rawInput.size()]);
			ChatTextColor[] output = new ChatTextColor[input.length];
			for (int i = 0; i < input.length; i++) {
				TextColor test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatStyle", () -> {
			ChatStyle.Builder builder = ChatStyle.newBuilder();
			builder.withColor(ChatNamedColor.GRAY);
			builder.withBold(true);
			builder.withItalic(false);
			builder.withUnderlined(null);
			builder.withStrikethrough(false);
			builder.withObfuscated(true);
			builder.withClickEvent(new ChatClickEvent(ChatClickEvent.Action.RUN_COMMAND, "/chatcomponentapi test"));
			builder.withHoverEvent(new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("world")));
			builder.withInsertion("Test");
			if (this.parser.supportsFontInStyle()) builder.withFont(ChatId.of("default"));
			ChatStyle input = builder.build();
			Style test = ForgeParser.forge(input);
			ChatStyle output = ForgeParser.forge(test);
			return input.equals(output);
		});
	}
}