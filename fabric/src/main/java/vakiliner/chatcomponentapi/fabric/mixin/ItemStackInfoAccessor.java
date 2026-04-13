package vakiliner.chatcomponentapi.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.world.item.Item;

@Mixin(ItemStackInfo.class)
public interface ItemStackInfoAccessor {
	@Accessor("item")
	Item getItem();

	@Accessor("count")
	int getCount();

	@Accessor("tag")
	CompoundTag getTag();
}