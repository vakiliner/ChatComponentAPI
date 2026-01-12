package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatTranslateComponent;

public class ChatTranslateComponentSerializer extends AbstractChatComponentSerializer<ChatTranslateComponent> {
	public JsonElement serialize(ChatTranslateComponent component, Type type, JsonSerializationContext context) {
		JsonObject object = super.serialize(component, context);
		object.addProperty("translate", component.getKey());
		return object;
	}

	public ChatTranslateComponent deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		String translate = object.get("translate").getAsString();
		JsonElement rawWith = object.get("with");
		List<ChatComponent> with = rawWith != null ? Arrays.asList(context.deserialize(rawWith, ChatComponent[].class)) : Collections.emptyList();
		return super.deserialize((style) -> new ChatTranslateComponent(null, translate, style, with), object, context);
	}
}