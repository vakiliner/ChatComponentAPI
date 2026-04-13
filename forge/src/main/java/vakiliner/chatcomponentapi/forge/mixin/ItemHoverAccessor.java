package vakiliner.chatcomponentapi.forge.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.event.HoverEvent.ItemHover;

@Mixin(ItemHover.class)
public interface ItemHoverAccessor {
	@Accessor("item")
	Item getItem();

	@Accessor("count")
	int getCount();

	@Accessor("tag")
	CompoundNBT getTag();
}