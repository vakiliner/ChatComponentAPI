package vakiliner.chatcomponentapi.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

@Mixin(Style.class)
public interface StyleMixin {
	@Invoker("<init>")
	static Style newStyle(TextColor textColor, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion, ResourceLocation font) {
		throw new AssertionError();
	}

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
}