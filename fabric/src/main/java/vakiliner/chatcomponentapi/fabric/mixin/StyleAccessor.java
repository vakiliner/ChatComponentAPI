package vakiliner.chatcomponentapi.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import vakiliner.chatcomponentapi.component.ChatStyle;
import vakiliner.chatcomponentapi.fabric.FabricParser;

@Mixin(Style.class)
public interface StyleAccessor {
	@Invoker("<init>")
	static Style newStyle(TextColor textColor, Boolean bold, Boolean italic, Boolean underlined, Boolean strikethrough, Boolean obfuscated, ClickEvent clickEvent, HoverEvent hoverEvent, String insertion, ResourceLocation font) {
		throw new AssertionError();
	}

	default ChatStyle toChatComponentAPI() {
		ChatStyle.Builder builder = ChatStyle.newBuilder();
		builder.withColor(FabricParser.fabric(this.getColor()));
		builder.withBold(this.getBold());
		builder.withItalic(this.getItalic());
		builder.withUnderlined(this.getUnderlined());
		builder.withStrikethrough(this.getStrikethrough());
		builder.withObfuscated(this.getObfuscated());
		builder.withClickEvent(FabricParser.fabric(this.getClickEvent()));
		builder.withHoverEvent(FabricParser.fabric(this.getHoverEvent()));
		builder.withInsertion(this.getInsertion());
		builder.withFont(FabricParser.fabric(this.$getFont()));
		return builder.build();
	}

	@Accessor("color")
	TextColor getColor();

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