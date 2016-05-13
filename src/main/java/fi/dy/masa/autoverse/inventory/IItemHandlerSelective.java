package fi.dy.masa.autoverse.inventory;

import net.minecraft.item.ItemStack;

public interface IItemHandlerSelective
{
    public boolean isItemValidForSlot(int slot, ItemStack stack);

    public boolean canExtractFromSlot(int slot);
}
