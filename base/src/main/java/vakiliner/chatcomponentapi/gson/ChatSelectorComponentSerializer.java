package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatSelectorComponent;

public class ChatSelectorComponentSerializer extends AbstractChatComponentSerializer<ChatSelectorComponent> {
	public JsonElement serialize(ChatSelectorComponent component, Type type, JsonSerializationContext context) {
		JsonObject object = super.serialize(component, context);
		object.addProperty("selector", component.getSelector());
		object.add("separator", context.serialize(component.getSeparator(), ChatComponent.class));
		return object;
	}

	public ChatSelectorComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		String selector = object.get("selector").getAsString();
		JsonElement rawSeparator = object.get("separator");
		ChatComponent separator = rawSeparator != null ? context.deserialize(rawSeparator, ChatComponent.class) : null;
		return super.deserialize((style) -> new ChatSelectorComponent(selector, separator, style), object, context);
	}
}