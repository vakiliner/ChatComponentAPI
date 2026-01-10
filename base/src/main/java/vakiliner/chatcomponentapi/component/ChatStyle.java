package vakiliner.chatcomponentapi.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import vakiliner.chatcomponentapi.common.ChatId;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public class ChatStyle {
	public static final ChatStyle EMPTY = new ChatStyle(null, null, null, null, null, null, null, null, null, null);
	private final ChatTextColor color;
	private final Boolean bold;
	private final Boolean italic;
	private final Boolean underlined;
	private final Boolean strikethrough;
	private final Boolean obfuscated;
	private final String insertion;
	private final ChatClickEvent clickEvent;
	private final ChatHoverEvent<?> hoverEvent;
	private final ChatId font;

	private ChatStyle(ChatTextColor color, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, String insertion, ChatClickEvent clickEvent, ChatHoverEvent<?> hoverEvent, ChatId font) {
		this.color = color;
		this.bold = bold;
		this.italic = italic;
		this.underlined = underlined;
		this.strikethrough = strikethrough;
		this.obfuscated = obfuscated;
		this.insertion = insertion;
		this.clickEvent = clickEvent;
		this.hoverEvent = hoverEvent;
		this.font = font;
	}

	private ChatStyle(ChatStyle style) {
		this.color = style.color;
		this.bold = style.bold;
		this.italic = style.italic;
		this.underlined = style.underlined;
		this.strikethrough = style.strikethrough;
		this.obfuscated = style.obfuscated;
		this.insertion = style.insertion;
		this.clickEvent = style.clickEvent;
		this.hoverEvent = style.hoverEvent;
		this.font = style.font;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static Builder newBuilder(ChatStyle style) {
		return new Builder(style);
	}

	public Builder toBuilder() {
		return newBuilder(this);
	}

	public ChatStyle clone() {
		return new ChatStyle(this);
	}

	public ChatTextColor getColor() {
		return this.color;
	}

	public Boolean getBold() {
		return this.bold;
	}

	public Boolean getItalic() {
		return this.italic;
	}

	public Boolean getUnderlined() {
		return this.underlined;
	}

	public Boolean getStrikethrough() {
		return this.strikethrough;
	}

	public Boolean getObfuscated() {
		return this.obfuscated;
	}

	public String getInsertion() {
		return this.insertion;
	}

	public ChatClickEvent getClickEvent() {
		return this.clickEvent;
	}

	public ChatHoverEvent<?> getHoverEvent() {
		return this.hoverEvent;
	}

	public ChatId getFont() {
		return this.font;
	}

	public ChatStyle withColor(ChatTextColor color) {
		if (Objects.equals(this.color, color)) return this;
		return new ChatStyle(color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withBold(Boolean bold) {
		if (Objects.equals(this.bold, bold)) return this;
		return new ChatStyle(this.color, bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withItalic(Boolean italic) {
		if (Objects.equals(this.italic, italic)) return this;
		return new ChatStyle(this.color, this.bold, italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withUnderlined(Boolean underlined) {
		if (Objects.equals(this.underlined, underlined)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withStrikethrough(Boolean strikethrough) {
		if (Objects.equals(this.strikethrough, strikethrough)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, this.underlined, strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withObfuscated(Boolean obfuscated) {
		if (Objects.equals(this.obfuscated, obfuscated)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withInsertion(String insertion) {
		if (Objects.equals(this.insertion, insertion)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, insertion, this.clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withClickEvent(ChatClickEvent clickEvent) {
		if (Objects.equals(this.clickEvent, clickEvent)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, clickEvent, this.hoverEvent, this.font);
	}

	public ChatStyle withHoverEvent(ChatHoverEvent<?> hoverEvent) {
		if (Objects.equals(this.hoverEvent, hoverEvent)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, hoverEvent, this.font);
	}

	public ChatStyle withFont(ChatId font) {
		if (Objects.equals(this.font, font)) return this;
		return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, font);
	}

	public Boolean getFormat(ChatComponentFormat format) {
		switch (format) {
			case BOLD: return this.getBold();
			case ITALIC: return this.getItalic();
			case UNDERLINED: return this.getUnderlined();
			case STRIKETHROUGH: return this.getStrikethrough();
			case OBFUSCATED: return this.getObfuscated();
			default: throw new IllegalArgumentException("Invalid ChatComponentFormat");
		}
	}

	public Map<ChatComponentFormat, Boolean> getFormats() {
		Map<ChatComponentFormat, Boolean> map = new HashMap<>();
		for (ChatComponentFormat format : ChatComponentFormat.values()) {
			map.put(format, this.getFormat(format));
		}
		return map;
	}

	public ChatStyle withFormat(ChatComponentFormat format, Boolean value) {
		switch (format) {
			case BOLD: return this.withBold(value);
			case ITALIC: return this.withItalic(value);
			case UNDERLINED: return this.withUnderlined(value);
			case STRIKETHROUGH: return this.withStrikethrough(value);
			case OBFUSCATED: return this.withObfuscated(value);
			default: throw new IllegalArgumentException("Invalid ChatComponentFormat");
		}
	}

	public ChatStyle withFormats(Map<ChatComponentFormat, Boolean> map) {
		Builder builder = this.toBuilder();
		for (Map.Entry<ChatComponentFormat, Boolean> entry : map.entrySet()) {
			builder = builder.withFormat(entry.getKey(), entry.getValue());
		}
		return builder.build();
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof ChatStyle) {
			ChatStyle other = (ChatStyle) obj;
			return (Objects.equals(this.color,			other.color)
				 && Objects.equals(this.bold,			other.bold)
				 && Objects.equals(this.italic,			other.italic)
				 && Objects.equals(this.underlined,		other.underlined)
				 && Objects.equals(this.strikethrough,	other.strikethrough)
				 && Objects.equals(this.obfuscated,		other.obfuscated)
				 && Objects.equals(this.insertion,		other.insertion)
				 && Objects.equals(this.clickEvent,		other.clickEvent)
				 && Objects.equals(this.hoverEvent,		other.hoverEvent)
				 && Objects.equals(this.font,			other.font)
			);
		} else {
			return false;
		}
	}

	public static class Builder {
		private ChatTextColor color;
		private Boolean bold;
		private Boolean italic;
		private Boolean underlined;
		private Boolean strikethrough;
		private Boolean obfuscated;
		private String insertion;
		private ChatClickEvent clickEvent;
		private ChatHoverEvent<?> hoverEvent;
		private ChatId font;

		private Builder() {
		}

		private Builder(ChatStyle style) {
			this.color = style.color;
			this.bold = style.bold;
			this.italic = style.italic;
			this.underlined = style.underlined;
			this.strikethrough = style.strikethrough;
			this.obfuscated = style.obfuscated;
			this.insertion = style.insertion;
			this.clickEvent = style.clickEvent;
			this.hoverEvent = style.hoverEvent;
			this.font = style.font;
		}

		public Builder withColor(ChatTextColor color) {
			this.color = color;
			return this;
		}

		public Builder withBold(Boolean bold) {
			this.bold = bold;
			return this;
		}

		public Builder withItalic(Boolean italic) {
			this.italic = italic;
			return this;
		}

		public Builder withUnderlined(Boolean underlined) {
			this.underlined = underlined;
			return this;
		}

		public Builder withStrikethrough(Boolean strikethrough) {
			this.strikethrough = strikethrough;
			return this;
		}

		public Builder withObfuscated(Boolean obfuscated) {
			this.obfuscated = obfuscated;
			return this;
		}

		public Builder withInsertion(String insertion) {
			this.insertion = insertion;
			return this;
		}

		public Builder withClickEvent(ChatClickEvent clickEvent) {
			this.clickEvent = clickEvent;
			return this;
		}

		public Builder withHoverEvent(ChatHoverEvent<?> hoverEvent) {
			this.hoverEvent = hoverEvent;
			return this;
		}

		public Builder withFont(ChatId font) {
			this.font = font;
			return this;
		}

		public Builder withFormat(ChatComponentFormat format, Boolean value) {
			switch (format) {
				case BOLD: return this.withBold(value);
				case ITALIC: return this.withItalic(value);
				case UNDERLINED: return this.withUnderlined(value);
				case STRIKETHROUGH: return this.withStrikethrough(value);
				case OBFUSCATED: return this.withObfuscated(value);
				default: throw new IllegalArgumentException("Invalid ChatComponentFormat");
			}
		}

		public ChatStyle build() {
			return new ChatStyle(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.insertion, this.clickEvent, this.hoverEvent, this.font);
		}
	}
}