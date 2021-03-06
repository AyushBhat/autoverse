package fi.dy.masa.autoverse.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import fi.dy.masa.autoverse.gui.client.GuiAutoverse;
import fi.dy.masa.autoverse.gui.client.GuiFilterSequential;
import fi.dy.masa.autoverse.inventory.ItemHandlerWrapperFilterSequential;
import fi.dy.masa.autoverse.inventory.container.ContainerFilterSequential;
import fi.dy.masa.autoverse.reference.ReferenceNames;

public class TileEntityFilterSequential extends TileEntityFilter
{
    protected ItemHandlerWrapperFilterSequential inventoryInputSequential;

    public TileEntityFilterSequential()
    {
        this(ReferenceNames.NAME_TILE_ENTITY_FILTER_SEQUENTIAL);
    }

    public TileEntityFilterSequential(String name)
    {
        super(name);
    }

    @Override
    protected void initFilterInventory()
    {
        this.inventoryInputSequential = new ItemHandlerWrapperFilterSequential(this.inventoryReset, this.inventoryFilterItems,
                                             this.inventoryFilterered, this.inventoryOtherOut, this);
        this.inventoryInput = this.inventoryInputSequential;
    }

    @Override
    public void setFilterTier(int tier)
    {
        this.filterTier = MathHelper.clamp_int(tier, 0, 2);

        this.initInventories();
        this.initFilterInventory();
    }

    public int getFilterPosition()
    {
        return this.inventoryInputSequential.getFilterPosition();
    }

    @Override
    protected int getFilterBufferSize()
    {
        return this.getNumFilterSlots();
    }

    @Override
    public int getNumResetSlots()
    {
        int tier = this.getFilterTier();

        switch (tier)
        {
            case 0: return 2;
            case 1: return 3;
            case 2: return 4;
            default: return 2;
        }
    }

    @Override
    public int getNumFilterSlots()
    {
        int tier = this.getFilterTier();

        switch (tier)
        {
            case 0: return 4;
            case 1: return 9;
            case 2: return 18;
            default: return 4;
        }
    }

    @Override
    public ContainerFilterSequential getContainer(EntityPlayer player)
    {
        return new ContainerFilterSequential(player, this);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiAutoverse getGui(EntityPlayer player)
    {
        return new GuiFilterSequential(this.getContainer(player), this, false);
    }
}
