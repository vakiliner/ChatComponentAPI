package vakiliner.chatcomponentapi.gson;

import java.lang.reflect.Type;
import java.util.UUID;
import com.google.gson.Gson;
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

public class ChatHoverEventSerializer implements JsonSerializer<ChatHoverEvent<?>>, JsonDeserializer<ChatHoverEvent<?>> {
	public JsonElement serialize(ChatHoverEvent<?> hoverEvent, Type type, JsonSerializationContext context) {
		JsonObject object = new JsonObject();
		object.addProperty("action", hoverEvent.getAction().getName());
		object.add("contents", context.serialize(hoverEvent.getContents()));
		return object;
	}

	public ChatHoverEvent<?> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
		JsonObject object = element.getAsJsonObject();
		final Action<?> action = Action.getByName(object.get("action").getAsString());
		final JsonElement contents;
		if (object.has("contents")) {
			contents = object.get("contents");
		} else if (object.has("value")) {
			JsonElement value = object.get("value");
			if (action == Action.SHOW_TEXT) {
				contents = value;
			} else {
				contents = new Gson().fromJson(((ChatTextComponent) context.deserialize(value, ChatTextComponent.class)).getText(), JsonElement.class);
			}
		} else {
			throw new JsonParseException("No content");
		}
		if (action == Action.SHOW_TEXT) {
			return new ChatHoverEvent<>(Action.SHOW_TEXT, context.deserialize(contents, ChatComponent.class));
		} else if (action == Action.SHOW_ENTITY) {
			return new ChatHoverEvent<>(Action.SHOW_ENTITY, context.deserialize(contents, ChatHoverEvent.ShowEntity.class));
		} else if (action == Action.SHOW_ITEM) {
			return new ChatHoverEvent<>(Action.SHOW_ITEM, context.deserialize(contents, ChatHoverEvent.ShowItem.class));
		} else {
			throw new JsonParseException("Unknown action");
		}
	}

	public static class ShowEntity implements JsonSerializer<ChatHoverEvent.ShowEntity>, JsonDeserializer<ChatHoverEvent.ShowEntity> {
		public JsonElement serialize(ChatHoverEvent.ShowEntity showEntity, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			ChatId type = showEntity.getType();
			UUID id = showEntity.getUniqueId();
			ChatComponent name = showEntity.getName();
			object.addProperty("type", type.toString());
			object.addProperty("id", id.toString());
			if (name != null) object.add("name", context.serialize(name, ChatComponent.class));
			return object;
		}

		public ChatHoverEvent.ShowEntity deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			JsonElement rawName = object.get("name");
			ChatId type = ChatId.parse(object.get("type").getAsString());
			UUID id = UUID.fromString(object.get("id").getAsString());
			ChatComponent name = rawName != null ? context.deserialize(rawName, ChatComponent.class) : null;
			return new ChatHoverEvent.ShowEntity(type, id, name);
		}
	}

	public static class ShowItem implements JsonSerializer<ChatHoverEvent.ShowItem>, JsonDeserializer<ChatHoverEvent.ShowItem> {
		public JsonElement serialize(ChatHoverEvent.ShowItem showItem, Type type, JsonSerializationContext context) {
			JsonObject object = new JsonObject();
			object.addProperty("id", showItem.getId().toString());
			int count = showItem.getCount();
			if (count != 1) object.addProperty("count", count);
			return object;
		}

		public ChatHoverEvent.ShowItem deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
			JsonObject object = element.getAsJsonObject();
			JsonElement rawCount = object.get("count");
			final ChatId id = ChatId.parse(object.get("id").getAsString());
			final int count = rawCount != null ? rawCount.getAsInt() : 1;
			return new ChatHoverEvent.ShowItem(id, count);
		}
	}
}