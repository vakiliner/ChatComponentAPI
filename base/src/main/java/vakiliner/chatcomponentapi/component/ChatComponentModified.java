package vakiliner.chatcomponentapi.component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import com.google.gson.JsonObject;
import vakiliner.chatcomponentapi.common.ChatTextColor;

public abstract class ChatComponentModified extends ChatComponent {
	protected final ChatComponent component;

	public ChatComponentModified(ChatComponent component) {
		if (component instanceof ChatComponentModified) {
			throw new IllegalArgumentException(component.getClass().getSimpleName() + " cannot be used as a component");
		}
		this.component = Objects.requireNonNull(component);
	}

	protected ChatComponentModified(ChatComponentModified component) {
		this.component = component.component;
	}

	public final ChatComponent getComponent() {
		return this.component;
	}

	public ChatComponent getComponent(boolean isConsole) {
		return this.getComponent();
	}

	@Override
	public String toLegacyText() {
		return this.component.toLegacyText();
	}

	@Override
	protected String getLegacyText(ChatTextColor parentColor, Set<ChatComponentFormat> parentFormats) {
		return this.component.getLegacyText(parentColor, parentFormats);
	}

	@Override
	public ChatStyle getStyle() {
		return this.component.getStyle();
	}

	@Override
	@Deprecated
	public ChatTextColor getColorRaw() {
		return this.component.getColorRaw();
	}

	@Override
	@Deprecated
	public Boolean isBoldRaw() {
		return this.component.isBoldRaw();
	}

	@Override
	@Deprecated
	public Boolean isItalicRaw() {
		return this.component.isItalicRaw();
	}

	@Override
	@Deprecated
	public Boolean isUnderlinedRaw() {
		return this.component.isUnderlinedRaw();
	}

	@Override
	@Deprecated
	public Boolean isStrikethroughRaw() {
		return this.component.isStrikethroughRaw();
	}

	@Override
	@Deprecated
	public Boolean isObfuscatedRaw() {
		return this.component.isObfuscatedRaw();
	}

	@Override
	@Deprecated
	public String getInsertion() {
		return this.component.getInsertion();
	}

	@Override
	@Deprecated
	public ChatClickEvent getClickEvent() {
		return this.component.getClickEvent();
	}

	@Override
	@Deprecated
	public ChatHoverEvent<?> getHoverEvent() {
		return this.component.getHoverEvent();
	}

	@Override
	public List<ChatComponent> getExtra() {
		return this.component.getExtra();
	}

	@Override
	@Deprecated
	public Boolean getFormatRaw(ChatComponentFormat format) {
		return this.component.getFormatRaw(format);
	}

	@Override
	@Deprecated
	public Map<ChatComponentFormat, Boolean> getFormatsRaw() {
		return this.component.getFormatsRaw();
	}

	@Override
	public void setStyle(ChatStyle style) {
		this.component.setStyle(style);
	}

	@Override
	@Deprecated
	public void setColor(ChatTextColor color) {
		this.component.setColor(color);
	}

	@Override
	@Deprecated
	public void setBold(Boolean bold) {
		this.component.setBold(bold);
	}

	@Override
	@Deprecated
	public void setItalic(Boolean italic) {
		this.component.setItalic(italic);
	}

	@Override
	@Deprecated
	public void setUnderlined(Boolean underlined) {
		this.component.setUnderlined(underlined);
	}

	@Override
	@Deprecated
	public void setStrikethrough(Boolean strikethrough) {
		this.component.setStrikethrough(strikethrough);
	}

	@Override
	@Deprecated
	public void setObfuscated(Boolean obfuscated) {
		this.component.setObfuscated(obfuscated);
	}

	@Override
	@Deprecated
	public void setInsertion(String insertion) {
		this.component.setInsertion(insertion);
	}

	@Override
	@Deprecated
	public void setClickEvent(ChatClickEvent clickEvent) {
		this.component.setClickEvent(clickEvent);
	}

	@Override
	@Deprecated
	public void setHoverEvent(ChatHoverEvent<?> hoverEvent) {
		this.component.setHoverEvent(hoverEvent);
	}

	@Override
	@Deprecated
	public void setExtra(Collection<ChatComponent> children) {
		this.component.setExtra(children);
	}

	@Override
	@Deprecated
	public void setFormat(ChatComponentFormat format, Boolean isSet) {
		this.component.setFormat(format, isSet);
	}

	@Override
	@Deprecated
	public void setFormats(Map<ChatComponentFormat, Boolean> map) {
		this.component.setFormats(map);
	}

	@Override
	public void append(ChatComponent component) {
		this.component.append(component);
	}

	@Override
	protected void unsafeAppend(ChatComponent component) {
		this.component.unsafeAppend(component);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyComponent(Supplier<ChatComponent> getLegacyComponent) {
		return this.component.withLegacyComponent(getLegacyComponent);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyComponent(ChatComponent legacyComponent) {
		return this.component.withLegacyComponent(legacyComponent);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyText(Supplier<String> getLegacyText) {
		return this.component.withLegacyText(getLegacyText);
	}

	@Override
	public ChatComponentWithLegacyText withLegacyText(String legacyText) {
		return this.component.withLegacyText(legacyText);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof ChatComponentModified)) {
			return false;
		} else {
			ChatComponentModified other = (ChatComponentModified) obj;
			return this.component.equals(other.component);
		}
	}

	@Override
	protected void serialize(JsonObject object) {
		this.component.serialize(object);
	}
}