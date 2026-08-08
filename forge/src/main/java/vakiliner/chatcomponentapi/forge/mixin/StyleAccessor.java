package vakiliner.chatcomponentapi.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.forge.ForgeParser;

@Mixin(Style.class)
public interface StyleAccessor {
	@Invoker("<init>")
	static Style newStyle(Color сolor, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion, ResourceLocation font) {
		throw new AssertionError();
	}

	default ChatStyle toChatComponentAPI() {
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(ForgeParser.forge(this.getColor()));
		builder.withBold(this.getBold());
		builder.withItalic(this.getItalic());
		builder.withUnderlined(this.getUnderlined());
		builder.withStrikethrough(this.getStrikethrough());
		builder.withObfuscated(this.getObfuscated());
		builder.withClickEvent(ForgeParser.forge(this.getClickEvent()));
		builder.withHoverEvent(ForgeParser.forge(this.getHoverEvent()));
		builder.withInsertion(this.getInsertion());
		builder.withFont(ForgeParser.forge(this.$getFont()));
		return builder.build();
	}

	@Accessor("color")
	Color getColor();

	@Accessor("bold")
	Boolean getBold();

	@Accessor("italic")
	Boolean getItalic();

	@Accessor("underlined")
	Boolean getUnderlined();

	@Accessor("strikethrough")
	Boolean getStrikethrough();

	@Accessor("obfuscated")
	Boolean getObfuscated();

	@Accessor("clickEvent")
	ClickEvent getClickEvent();

	@Accessor("hoverEvent")
	HoverEvent getHoverEvent();

	@Accessor("insertion")
	String getInsertion();

	@Accessor("font")
	ResourceLocation $getFont();
}