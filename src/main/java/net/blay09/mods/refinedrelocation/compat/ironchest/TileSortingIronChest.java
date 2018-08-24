package net.blay09.mods.refinedrelocation.compat.ironchest;

import cpw.mods.ironchest.common.blocks.chest.IronChestType;
import cpw.mods.ironchest.common.tileentity.chest.TileEntityIronChest;
import net.blay09.mods.refinedrelocation.api.Capabilities;
import net.blay09.mods.refinedrelocation.api.filter.IRootFilter;
import net.blay09.mods.refinedrelocation.api.grid.ISortingInventory;
import net.blay09.mods.refinedrelocation.capability.CapabilityRootFilter;
import net.blay09.mods.refinedrelocation.capability.CapabilitySimpleFilter;
import net.blay09.mods.refinedrelocation.capability.CapabilitySortingGridMember;
import net.blay09.mods.refinedrelocation.capability.CapabilitySortingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.Locale;

public class TileSortingIronChest extends TileEntityIronChest implements ITickable {

    private final InvWrapper invWrapper = new InvWrapper(this);
    private final ISortingInventory sortingInventory = Capabilities.getDefaultInstance(Capabilities.SORTING_INVENTORY);
    private final IRootFilter rootFilter = Capabilities.getDefaultInstance(Capabilities.ROOT_FILTER);

    public TileSortingIronChest() {
    }

    public TileSortingIronChest(IronChestType type) {
        super(type);
    }

    public void onContentsChanged(int slot) {
        markDirty();
        sortingInventory.onSlotChanged(slot);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        sortingInventory.onLoad(this);
    }

    @Override
    public void update() {
        super.update();
        sortingInventory.onUpdate(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        sortingInventory.onInvalidate(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        sortingInventory.onInvalidate(this);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("SortingInventory", sortingInventory.serializeNBT());
        compound.setTag("RootFilter", rootFilter.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        sortingInventory.deserializeNBT(compound.getCompoundTag("SortingInventory"));

        // vvv Backwards Compatibility
        if (compound.getTagId("RootFilter") == Constants.NBT.TAG_LIST) {
            NBTTagList tagList = compound.getTagList("RootFilter", Constants.NBT.TAG_COMPOUND);
            compound.removeTag("RootFilter");
            NBTTagCompound rootFilter = new NBTTagCompound();
            rootFilter.setTag("FilterList", tagList);
            compound.setTag("RootFilter", rootFilter);
        }
        // ^^^ Backwards Compatibility

        rootFilter.deserializeNBT(compound.getCompoundTag("RootFilter"));
    }

    @Override
    public String getName() {
        return hasCustomName() ? super.getName() : "container.refinedrelocation:ironchest.sorting_chest_" + getType().name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return super.getItems();
    }

    @Override
    public IronChestType getType() {
        if (hasWorld()) {
            return IronChestType.VALUES[getBlockMetadata()];
        } else {
            return IronChestType.IRON;
        }
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        super.setInventorySlotContents(index, stack);
        onContentsChanged(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemStack = super.decrStackSize(index, count);
        onContentsChanged(index);
        return itemStack;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability == CapabilitySortingInventory.CAPABILITY || capability == CapabilitySortingGridMember.CAPABILITY
                || capability == CapabilityRootFilter.CAPABILITY || capability == CapabilitySimpleFilter.CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) invWrapper;
        } else if (capability == CapabilitySortingInventory.CAPABILITY || capability == CapabilitySortingGridMember.CAPABILITY) {
            return (T) sortingInventory;
        } else if (capability == CapabilityRootFilter.CAPABILITY || capability == CapabilitySimpleFilter.CAPABILITY) {
            return (T) rootFilter;
        }
        return super.getCapability(capability, facing);
    }

    public static class Dirt extends TileSortingIronChest {
        public Dirt() {
            super(IronChestType.DIRTCHEST9000);
        }
    }

    public static class Obsidian extends TileSortingIronChest {
        public Obsidian() {
            super(IronChestType.OBSIDIAN);
        }
    }

    public static class Crystal extends TileSortingIronChest {
        public Crystal() {
            super(IronChestType.CRYSTAL);
        }
    }

    public static class Diamond extends TileSortingIronChest {
        public Diamond() {
            super(IronChestType.DIAMOND);
        }
    }

    public static class Copper extends TileSortingIronChest {
        public Copper() {
            super(IronChestType.COPPER);
        }
    }

    public static class Gold extends TileSortingIronChest {
        public Gold() {
            super(IronChestType.GOLD);
        }
    }

    public static class Silver extends TileSortingIronChest {
        public Silver() {
            super(IronChestType.SILVER);
        }
    }

}
