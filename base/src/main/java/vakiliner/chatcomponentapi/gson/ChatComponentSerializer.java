package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatComponentModified;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class ChatComponentSerializer implements JsonSerializer<ChatComponent>, JsonDeserializer<ChatComponent> {
	public JsonElement serialize(ChatComponent src, Type typeOfSrc, JsonSerializationContext context) {
		if (src instanceof ChatComponentModified) {
			src = ((ChatComponentModified) src).getComponent();
		}
		if (src instanceof ChatTextComponent) {
			return context.serialize(src, ChatTextComponent.class);
		} else if (src instanceof ChatTranslateComponent) {
			return context.serialize(src, ChatTranslateComponent.class);
		} else if (src instanceof ChatSelectorComponent) {
			return context.serialize(src, ChatSelectorComponent.class);
		} else {
			throw new IllegalArgumentException();
		}
	}

	public ChatComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		if (element.isJsonPrimitive()) {
			return context.deserialize(element, ChatTextComponent.class);
		}
		JsonObject object = element.getAsJsonObject();
		if (object.has("text")) {
			return context.deserialize(object, ChatTextComponent.class);
		} else if (object.has("translate")) {
			return context.deserialize(object, ChatTranslateComponent.class);
		} else if (object.has("selector")) {
			return context.deserialize(object, ChatSelectorComponent.class);
		} else {
			throw new IllegalArgumentException();
		}
	}
}