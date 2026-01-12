package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import java.util.UUID;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent;
import vakiliner.chatcomponentapi.component.ChatTextComponent;
import vakiliner.chatcomponentapi.component.ChatHoverEvent.Action;

public class ChatHoverEventSerializerWithValue extends ChatHoverEventSerializer {
	public JsonElement serialize(ChatHoverEvent<?> hoverEvent, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		Action<?> action = hoverEvent.getAction();
		final JsonElement value;
		if (action == Action.SHOW_TEXT) {
			value = context.serialize(hoverEvent.getContents(), ChatComponent.class);
		} else {
			value = context.serialize(new ChatTextComponent(context.serialize(hoverEvent.getContents()).toString()), ChatTextComponent.class);
		}
		object.addProperty("action", action.getName());
		object.add("value", value);
		return object;
	}

	public static class ShowEntity implements JsonSerializer<ChatHoverEvent.ShowEntity>, JsonDeserializer<ChatHoverEvent.ShowEntity> {
		public JsonElement serialize(ChatHoverEvent.ShowEntity showEntity, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			ChatId type = showEntity.getType();
			UUID id = showEntity.getUniqueId();
			ChatComponent name = showEntity.getName();
			object.addProperty("type", type.toString());
			object.addProperty("id", id.toString());
			if (name != null) object.add("name", context.serialize(new ChatTextComponent(context.serialize(name, ChatComponent.class).toString())));
			return object;
		}

		public ChatHoverEvent.ShowEntity deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			JsonElement rawName = object.get("name");
			ChatId type = ChatId.parse(object.get("type").getAsString());
			UUID id = UUID.fromString(object.get("id").getAsString());
			ChatComponent name = rawName != null ? context.deserialize(rawName, ChatTextComponent.class) : null;
			return new ChatHoverEvent.ShowEntity(type, id, name);
		}
	}
}