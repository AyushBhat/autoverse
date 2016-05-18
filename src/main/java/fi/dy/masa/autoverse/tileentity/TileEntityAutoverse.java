package fi.dy.masa.autoverse.tileentity;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import fi.dy.masa.autoverse.reference.Reference;

public class TileEntityAutoverse extends TileEntity
{
    protected String tileEntityName;
    protected EnumFacing facing;
    protected EnumFacing facingOpposite;
    protected BlockPos posFront;
    //protected BlockPos posBack;
    protected boolean redstoneState;
    protected boolean tickScheduled;

    public TileEntityAutoverse(String name)
    {
        this.tileEntityName = name;
        this.facing = EnumFacing.UP;
    }

    public String getTEName()
    {
        return this.tileEntityName;
    }

    public void setFacing(EnumFacing facing)
    {
        this.facing = facing;
        this.facingOpposite = this.facing.getOpposite();
        this.posFront = this.getPos().offset(this.facing);
        //this.posBack = this.getPos().offset(this.facingOpposite);
    }

    public EnumFacing getFacing()
    {
        return this.facing;
    }

    protected Vec3d getSpawnedItemPosition()
    {
        return this.getSpawnedItemPosition(this.facing);
    }

    protected Vec3d getSpawnedItemPosition(EnumFacing side)
    {
        double x = this.getPos().getX() + 0.5 + side.getFrontOffsetX() * 0.625;
        double y = this.getPos().getY() + 0.5 + side.getFrontOffsetY() * 0.5;
        double z = this.getPos().getZ() + 0.5 + side.getFrontOffsetZ() * 0.625;

        if (side == EnumFacing.DOWN)
        {
            y -= 0.25;
        }

        return new Vec3d(x, y, z);
    }

    public void onLeftClickBlock(EntityPlayer player) { }

    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        boolean redstone = this.worldObj.isBlockPowered(this.getPos());

        if (redstone != this.redstoneState)
        {
            this.onRedstoneChange(redstone);
        }

        this.redstoneState = redstone;
    }

    protected void onRedstoneChange(boolean state)
    {
    }

    public void onBlockTick(IBlockState state, Random rand)
    {
        this.tickScheduled = false;
    }

    public void scheduleBlockTick(int delay, boolean force)
    {
        if (this.tickScheduled == false || force == true)
        {
            this.getWorld().scheduleUpdate(this.getPos(), this.getBlockType(), delay);
            this.tickScheduled = true;
        }
    }

    public void readFromNBTCustom(NBTTagCompound nbt)
    {
        this.redstoneState = nbt.getBoolean("Redstone");

        // Update the opposite and the front and back BlockPos
        this.setFacing(EnumFacing.getFront(nbt.getByte("Facing")));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.readFromNBTCustom(nbt); // This call needs to be at the super-most custom TE class
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);

        tag.setString("Version", Reference.MOD_VERSION);
        tag.setByte("Facing", (byte)this.facing.getIndex());
        tag.setBoolean("Redstone", this.redstoneState);
    }

    protected NBTTagCompound getBlockEntityTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);

        nbt.removeTag("id");
        nbt.removeTag("x");
        nbt.removeTag("y");
        nbt.removeTag("z");

        return nbt;
    }

    /**
     * Adds the BlockEntityTag to the provided ItemStack, if one exists
     */
    public ItemStack addBlockEntityTag(ItemStack stack)
    {
        NBTTagCompound nbt = this.getBlockEntityTag();
        if (nbt.hasNoTags() == false)
        {
            stack.setTagInfo("BlockEntityTag", nbt);
        }

        return stack;
    }

    public NBTTagCompound getDescriptionPacketTag(NBTTagCompound tag)
    {
        tag.setByte("f", (byte)(this.getFacing().getIndex() & 0x07));
        return tag;
    }

    @Override
    public Packet<INetHandlerPlayClient> getDescriptionPacket()
    {
        if (this.worldObj != null)
        {
            return new SPacketUpdateTileEntity(this.getPos(), 0, this.getDescriptionPacketTag(new NBTTagCompound()));
        }

        return null;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        NBTTagCompound nbt = packet.getNbtCompound();

        if (nbt.hasKey("f") == true)
        {
            this.setFacing(EnumFacing.getFront((byte)(nbt.getByte("f") & 0x07)));
        }

        IBlockState state = this.worldObj.getBlockState(this.getPos());
        this.worldObj.notifyBlockUpdate(this.getPos(), state, state, 3);
    }

    @Override
    public String toString()
    {
        return this.getClass().getSimpleName() + "(" + this.getPos() + ")@" + System.identityHashCode(this);
    }
}
