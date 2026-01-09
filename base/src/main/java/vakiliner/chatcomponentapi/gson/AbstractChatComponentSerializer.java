package vakiliner.chatcomponentapi.gson;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

public abstract class AbstractChatComponentSerializer<Component extends ChatComponent> implements JsonSerializer<Component>, JsonDeserializer<Component> {
	protected void serialize(JsonObject object, Component component, JsonSerializationContext context) {
		ChatStyle style = component.getStyle();
		ChatTextColor color = style.getColor();
		Boolean bold = style.getBold();
		Boolean italic = style.getItalic();
		Boolean underlined = style.getUnderlined();
		Boolean strikethrough = style.getStrikethrough();
		Boolean obfuscated = style.getObfuscated();
		String insertion = style.getInsertion();
		ChatClickEvent clickEvent = style.getClickEvent();
		ChatHoverEvent<?> hoverEvent = style.getHoverEvent();
		List<ChatComponent> extra = component.getExtra();
		if (color != null) object.addProperty("color", color.toString());
		if (bold != null) object.addProperty("bold", bold);
		if (italic != null) object.addProperty("italic", italic);
		if (underlined != null) object.addProperty("underlined", underlined);
		if (strikethrough != null) object.addProperty("strikethrough", strikethrough);
		if (obfuscated != null) object.addProperty("obfuscated", obfuscated);
		if (insertion != null) object.addProperty("insertion", insertion);
		if (clickEvent != null) object.add("clickEvent", context.serialize(clickEvent));
		if (hoverEvent != null) object.add("hoverEvent", context.serialize(hoverEvent));
		if (extra != null) object.add("extra", context.serialize(extra));
	}

	public <T extends Component> T deserialize(Function<ChatStyle, T> function, JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		if (object.has("color")) builder = builder.withColor(ChatTextColor.of(object.get("color").getAsString()));
		if (object.has("bold")) builder = builder.withBold(object.get("bold").getAsBoolean());
		if (object.has("italic")) builder = builder.withItalic(object.get("italic").getAsBoolean());
		if (object.has("underlined")) builder = builder.withUnderlined(object.get("underlined").getAsBoolean());
		if (object.has("strikethrough")) builder = builder.withStrikethrough(object.get("strikethrough").getAsBoolean());
		if (object.has("obfuscated")) builder = builder.withObfuscated(object.get("obfuscated").getAsBoolean());
		if (object.has("insertion")) builder = builder.withInsertion(object.get("insertion").getAsString());
		if (object.has("clickEvent")) builder = builder.withClickEvent(context.deserialize(object.get("clickEvent"), ChatClickEvent.class));
		if (object.has("hoverEvent")) builder = builder.withHoverEvent(context.deserialize(object.get("hoverEvent"), ChatHoverEvent.class));
		T component = function.apply(builder.build());
		if (object.has("extra")) component.setExtra(Arrays.asList(context.deserialize(object.get("extra"), ChatComponent[].class)));
		return component;
	}
}