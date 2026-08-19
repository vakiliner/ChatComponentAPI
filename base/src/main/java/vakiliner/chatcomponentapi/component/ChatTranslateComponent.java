package vakiliner.chatcomponentapi.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.common.ChatTextColor;
import vakiliner.chatcomponentapi.util.Utils;

public class ChatTranslateComponent extends ChatComponent {
	private String key;
	private final List<ChatComponent> with = new ArrayList<>();
	private String fallback;
	private boolean useFallback;

	public ChatTranslateComponent(String fallback, String key, ChatComponent... with) {
		this(fallback, key, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, ChatStyle style, ChatComponent... with) {
		this(fallback, key, style, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, ChatTextColor color, ChatComponent... with) {
		this(fallback, key, color, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, ChatComponentFormat format, ChatComponent... with) {
		this(fallback, key, format, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, Collection<ChatComponent> with) {
		this(fallback, key, false, with);
	}

	public ChatTranslateComponent(String fallback, String key, ChatStyle style, Collection<ChatComponent> with) {
		this(fallback, key, false, style, with);
	}

	public ChatTranslateComponent(String fallback, String key, ChatTextColor color, Collection<ChatComponent> with) {
		this(fallback, key, false, color, with);
	}

	public ChatTranslateComponent(String fallback, String key, ChatComponentFormat format, Collection<ChatComponent> with) {
		this(fallback, key, false, format, with);
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatComponent... with) {
		this(fallback, key, useFallback, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatStyle style, ChatComponent... with) {
		this(fallback, key, useFallback, style, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatTextColor color, ChatComponent... with) {
		this(fallback, key, useFallback, color, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatComponentFormat format, ChatComponent... with) {
		this(fallback, key, useFallback, format, Arrays.asList(with));
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, Collection<ChatComponent> with) {
		this.key = Objects.requireNonNull(key);
		this.with.addAll(with);
		this.fallback = fallback;
		this.useFallback = useFallback;
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatStyle style, Collection<ChatComponent> with) {
		super(style);
		this.key = Objects.requireNonNull(key);
		this.with.addAll(with);
		this.fallback = fallback;
		this.useFallback = useFallback;
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatTextColor color, Collection<ChatComponent> with) {
		super(color);
		this.key = Objects.requireNonNull(key);
		this.with.addAll(with);
		this.fallback = fallback;
		this.useFallback = useFallback;
	}

	public ChatTranslateComponent(String fallback, String key, boolean useFallback, ChatComponentFormat format, Collection<ChatComponent> with) {
		super(format);
		this.key = Objects.requireNonNull(key);
		this.with.addAll(with);
		this.fallback = fallback;
		this.useFallback = useFallback;
	}

	public ChatTranslateComponent(ChatTranslateComponent component, boolean cloneExtra) {
		super(component, cloneExtra);
		this.key = component.key;
		this.with.addAll(component.with);
		this.fallback = component.fallback;
		this.useFallback = component.useFallback;
	}

	@Override
	public ChatTranslateComponent clone(boolean cloneExtra) {
		return new ChatTranslateComponent(this, cloneExtra);
	}

	public String getKey() {
		return this.key;
	}

	public List<ChatComponent> getWith() {
		return Collections.unmodifiableList(this.with);
	}

	public String getFallback() {
		if (!this.useFallback) return null;
		return this.fallback;
	}

	public void setKey(String key) {
		this.key = Objects.requireNonNull(key);
	}

	public void setWith(Collection<ChatComponent> with) {
		this.with.clear();
		this.with.addAll(with);
	}

	public void setFallback(String fallback) {
		this.setFallback(fallback, fallback != null);
	}

	public void setLegacyText(String fallback) {
		this.setFallback(fallback, false);
	}

	private void setFallback(String fallback, boolean useFallback) {
		this.fallback = fallback;
		this.useFallback = useFallback;
	}

	public void addWith(ChatComponent with) {
		this.with.add(with);
	}

	@Override
	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		List<ChatComponent> with = this.with;
		if (with.isEmpty()) {
			return this.fallback;
		} else {
			return Utils.stringFormat(this.fallback, with.stream().map((component) -> component.toLegacyText(parentColor, parentFormats)).toArray());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatTranslateComponent)) {
			return false;
		} else  {
			ChatTranslateComponent other = (ChatTranslateComponent) obj;
			return super.equals(other) && this.key.equals(other.key) && Objects.equals(this.fallback, other.fallback) && this.with.equals(other.with);
		}
	}

	@Override
	protected void serialize(JsonObject object) {
		object.addProperty("translate", this.key);
		List<ChatComponent> with = this.with;
		if (!with.isEmpty()) {
			JsonArray array = new JsonArray();
			with.forEach((c) -> array.add(ChatComponent.serialize(c)));
			object.add("with", array);
		}
	}

	public static ChatTranslateComponent deserialize(JsonElement element) {
		JsonObject object = element.getAsJsonObject();
		JsonElement rawWith = object.get("with");
		String translate = object.get("translate").getAsString();
		List<ChatComponent> with = new ArrayList<>();
		if (rawWith != null) {
			JsonArray array = rawWith.getAsJsonArray();
			array.forEach((c) -> with.add(ChatComponent.deserialize(c)));
		}
		return ChatComponent.deserialize((style) -> new ChatTranslateComponent(null, translate, style, with), object);
	}
}