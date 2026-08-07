package vakiliner.chatcomponentapi.forge;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vakiliner.chatcomponentapi.base.DevTester;
import vakiliner.chatcomponentapi.common.ChatId;
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
			var input = new ChatTextComponent("123");
			var test = ForgeParser.forge(input);
			ChatComponent output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatTranslateComponent", () -> {
			var input = new ChatTranslateComponent(null, "123");
			var test = ForgeParser.forge(input);
			var output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatSelectorComponent", () -> {
			ChatSelectorComponent input = new ChatSelectorComponent("123");
			if (this.parser.supportsSeparatorInSelector()) input.setSeparator(new ChatTextComponent(" | "));
			var test = ForgeParser.forge(input);
			var output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatClickEvent", () -> {
			for (var action : ChatClickEvent.Action.values()) {
				var input = new ChatClickEvent(action, "/chatcomponentapi test");
				var test = ForgeParser.forge(input);
				var output = ForgeParser.forge(test);
				if (!input.equals(output)) return false;
			}
			return true;
		});
		test("Parse ChatHoverEvent", () -> {
			ChatHoverEvent<?>[] input = { new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_TEXT, new ChatTextComponent("123")), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ENTITY, new ChatHoverEvent.ShowEntity(ChatId.of("creeper"), UUID.randomUUID(), new ChatTextComponent("Hello"))), new ChatHoverEvent<>(ChatHoverEvent.Action.SHOW_ITEM, new ChatHoverEvent.ShowItem(ChatId.of("dirt"), 15)) };
			var output = new ChatHoverEvent[input.length];
			for (int i = 0; i < input.length; i++) {
				var test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatId", () -> {
			var input = new ChatId("chatcomponentapi", "id");
			var test = ForgeParser.forge(input);
			var output = ForgeParser.forge(test);
			return input.equals(output);
		});
		test("Parse ChatTextFormat", () -> {
			var input = ChatTextFormat.values();
			var output = new ChatTextFormat[input.length];
			for (int i = 0; i < input.length; i++) {
				var test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatTextColor", () -> {
			var rawInput = Arrays.asList(ChatTextFormat.values()).stream().filter(ChatTextFormat::isColor).map(ChatNamedColor::getByFormat).collect(Collectors.toList());
			var input = rawInput.toArray(new ChatTextColor[rawInput.size()]);
			var output = new ChatTextColor[input.length];
			for (int i = 0; i < input.length; i++) {
				var test = ForgeParser.forge(input[i]);
				output[i] = ForgeParser.forge(test);
			}
			return Arrays.equals(input, output);
		});
		test("Parse ChatStyle", () -> {
			var builder = ChatStyle.newBuilder();
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
			var input = builder.build();
			var test = ForgeParser.forge(input);
			var output = ForgeParser.forge(test);
			return input.equals(output);
		});
	}
}