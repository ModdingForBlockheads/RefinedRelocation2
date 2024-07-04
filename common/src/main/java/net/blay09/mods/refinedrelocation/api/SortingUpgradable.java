package net.blay09.mods.refinedrelocation.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class SortingUpgradable implements ISortingUpgradable {
    @Override
    public boolean applySortingUpgrade(BlockEntity blockEntity, ItemStack itemStack, Player player, Level level, BlockPos pos, Direction side, double hitX, double hitY, double hitZ, InteractionHand hand) {
        return false;
    }
}
