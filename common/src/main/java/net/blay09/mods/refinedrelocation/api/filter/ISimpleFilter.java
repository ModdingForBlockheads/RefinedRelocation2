package net.blay09.mods.refinedrelocation.api.filter;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;

public interface ISimpleFilter {
    @Deprecated
    default boolean passes(BlockEntity blockEntity, ItemStack itemStack) {
        return passes(blockEntity, itemStack, itemStack);
    }

    boolean passes(BlockEntity blockEntity, ItemStack itemStack, ItemStack originalStack);

    @Nullable
    default MenuProvider getConfiguration(Player player, BlockEntity blockEntity, int rootFilterIndex, int filterIndex) {
        return null;
    }

    default boolean hasConfiguration() {
        return false;
    }

    CompoundTag serializeNBT();

    void deserializeNBT(CompoundTag nbt);
}
