package vakiliner.chatcomponentapi.component;

import java.util.Objects;
import java.util.Set;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatSelectorComponent extends ChatComponent {
	private String selector;
	private ChatComponent separator;

	public ChatSelectorComponent(String selector) {
		this.selector = Objects.requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatComponent separator) {
		this(selector);
		this.separator = separator;
	}

	public ChatSelectorComponent(String selector, ChatStyle style) {
		super(style);
		this.selector = Objects.requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatTextColor color) {
		super(color);
		this.selector = Objects.requireNonNull(selector);
	}

	public ChatSelectorComponent(String selector, ChatComponent separator, ChatStyle style) {
		this(selector, style);
		this.separator = separator;
	}

	public ChatSelectorComponent(String selector, ChatComponent separator, ChatTextColor color) {
		this(selector, color);
		this.separator = separator;
	}

	public ChatSelectorComponent(ChatSelectorComponent component) {
		super(component);
		this.selector = component.selector;
		this.separator = component.separator;
	}

	public ChatSelectorComponent clone() {
		return new ChatSelectorComponent(this);
	}

	public String getSelector() {
		return this.selector;
	}

	public ChatComponent getSeparator() {
		return this.separator;
	}

	public void setSelector(String selector) {
		this.selector = Objects.requireNonNull(selector);
	}

	public void setSeparator(ChatComponent separator) {
		this.separator = separator;
	}

	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.selector;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatSelectorComponent)) {
			return false;
		} else  {
			ChatSelectorComponent other = (ChatSelectorComponent) obj;
			return super.equals(other) && this.selector.equals(other.selector) && Objects.equals(this.separator, other.separator);
		}
	}
}