package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatTextComponent extends ChatComponent {
	private String text;

	public ChatTextComponent() {
		this("");
	}

	public ChatTextComponent(String text) {
		this.text = Objects.requireNonNull(text);
	}

	public ChatTextComponent(String text, ChatTextColor color) {
		super(color);
		this.text = Objects.requireNonNull(text);
	}

	public ChatTextComponent(ChatTextComponent component) {
		super(component);
		this.text = component.text;
	}

	public ChatTextComponent clone() {
		return new ChatTextComponent(this);
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
}