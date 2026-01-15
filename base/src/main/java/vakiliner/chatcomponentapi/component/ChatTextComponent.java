package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatTextComponent extends ChatComponent {
	private String text;

	public ChatTextComponent() {
		this("");
	}

	public ChatTextComponent(String text) {
		this.text = Objects.requireNonNull(text);
	}

	public ChatTextComponent(String text, ChatStyle style) {
		super(style);
		this.text = Objects.requireNonNull(text);
	}

	public ChatTextComponent(String text, ChatTextColor color) {
		super(color);
		this.text = Objects.requireNonNull(text);
	}

	public ChatTextComponent(ChatTextComponent component, boolean cloneExtra) {
		super(component, cloneExtra);
		this.text = component.text;
	}

	public ChatTextComponent clone(boolean cloneExtra) {
		return new ChatTextComponent(this, cloneExtra);
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = Objects.requireNonNull(text);
	}

	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.text;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatTextComponent)) {
			return false;
		} else  {
			ChatTextComponent other = (ChatTextComponent) obj;
			return super.equals(other) && this.text.equals(other.text);
		}
	}

	protected void serialize(JsonObject object) {
		object.addProperty("text", this.text);
	}

	public static ChatTextComponent deserialize(JsonElement element) {
		if (element.isJsonPrimitive()) {
			return new ChatTextComponent(element.getAsString());
		}
		JsonObject object = element.getAsJsonObject();
		String text = object.get("text").getAsString();
		return ChatComponent.deserialize((style) -> new ChatTextComponent(text, style), object);
	}
}