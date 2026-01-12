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
import vakiliner.chatcomponentapi.component.ChatComponent;
import vakiliner.chatcomponentapi.component.ChatStyle;

public abstract class AbstractChatComponentSerializer<Component extends ChatComponent> implements JsonSerializer<Component>, JsonDeserializer<Component> {
	protected JsonObject serialize(Component component, JsonSerializationContext context) {
		JsonObject object = context.serialize(component.getStyle(), ChatStyle.class).getAsJsonObject();
		List<ChatComponent> extra = component.getExtra();
		if (extra != null && !extra.isEmpty()) object.add("extra", context.serialize(extra, ChatComponent[].class));
		return object;
	}

	protected <T extends Component> T deserialize(Function<ChatStyle, T> function, JsonObject object, JsonDeserializationContext context) throws JsonParseException {
		ChatStyle style = context.deserialize(object, ChatStyle.class);
		T component = function.apply(style);
		if (object.has("extra")) component.setExtra(Arrays.asList(context.deserialize(object.get("extra"), ChatComponent[].class)));
		return component;
	}
}