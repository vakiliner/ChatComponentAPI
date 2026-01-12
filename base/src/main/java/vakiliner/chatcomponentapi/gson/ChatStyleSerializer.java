package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.component.ChatClickEvent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;

public class ChatStyleSerializer implements JsonSerializer<ChatStyle>, JsonDeserializer<ChatStyle> {
	public JsonElement serialize(ChatStyle style, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		ChatTextColor color = style.getColor();
		Boolean bold = style.getBold();
		Boolean italic = style.getItalic();
		Boolean underlined = style.getUnderlined();
		Boolean strikethrough = style.getStrikethrough();
		Boolean obfuscated = style.getObfuscated();
		ChatClickEvent clickEvent = style.getClickEvent();
		ChatHoverEvent<?> hoverEvent = style.getHoverEvent();
		String insertion = style.getInsertion();
		ChatId font = style.getFont();
		if (color != null) object.addProperty("color", color.toString());
		if (bold != null) object.addProperty("bold", bold);
		if (italic != null) object.addProperty("italic", italic);
		if (underlined != null) object.addProperty("underlined", underlined);
		if (strikethrough != null) object.addProperty("strikethrough", strikethrough);
		if (obfuscated != null) object.addProperty("obfuscated", obfuscated);
		if (clickEvent != null) object.add("clickEvent", context.serialize(clickEvent, ChatClickEvent.class));
		if (hoverEvent != null) object.add("hoverEvent", context.serialize(hoverEvent, ChatHoverEvent.class));
		if (insertion != null) object.addProperty("insertion", insertion);
		if (font != null) object.addProperty("font", font.toString());
		return object;
	}

	public ChatStyle deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		if (object.has("color")) builder.withColor(ChatTextColor.of(object.get("color").getAsString()));
		if (object.has("bold")) builder.withBold(object.get("bold").getAsBoolean());
		if (object.has("italic")) builder.withItalic(object.get("italic").getAsBoolean());
		if (object.has("underlined")) builder.withUnderlined(object.get("underlined").getAsBoolean());
		if (object.has("strikethrough")) builder.withStrikethrough(object.get("strikethrough").getAsBoolean());
		if (object.has("obfuscated")) builder.withObfuscated(object.get("obfuscated").getAsBoolean());
		if (object.has("clickEvent")) builder.withClickEvent(context.deserialize(object.get("clickEvent"), ChatClickEvent.class));
		if (object.has("hoverEvent")) builder.withHoverEvent(context.deserialize(object.get("hoverEvent"), ChatHoverEvent.class));
		if (object.has("insertion")) builder.withInsertion(object.get("insertion").getAsString());
		if (object.has("font")) builder.withFont(ChatId.parse(object.get("font").getAsString()));
		return builder.build();
	}
}