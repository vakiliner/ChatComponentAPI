package vakiliner.chatcomponentapi.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

@Mixin(Style.class)
public interface StyleAccessor {
	@Invoker("<init>")
	static Style newStyle(Color сolor, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion, ResourceLocation font) {
		throw new AssertionError();
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
	ResourceLocation getFont();
}