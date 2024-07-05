package net.blay09.mods.refinedrelocation.fabric.compat.ironchest;


import java.lang.reflect.Field;

import javax.annotation.Nonnull;

import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.Nullable;

import net.blay09.mods.refinedrelocation.block.ModBlocks;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.SortingChestType;
import net.blay09.mods.refinedrelocation.api.Capabilities;
import net.blay09.mods.refinedrelocation.api.ISortingUpgradable;
import net.blay09.mods.refinedrelocation.block.SortingChestBlock;
import net.blay09.mods.refinedrelocation.block.entity.SortingChestBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class IronChestAddon {

    private static final Logger logger = LogManager.getLogger();

    private Class<?> ironChestTileEntity;
    private Class<?> chestUpgradeItem;
    private Field lidAngleField;
    private Field chestUpgradeType;

    public IronChestAddon() {
        MinecraftForge.EVENT_BUS.register(this);

        try {
            ironChestTileEntity = Class.forName("com.progwml6.ironchest.common.block.tileentity.GenericIronChestTileEntity");
            lidAngleField = ironChestTileEntity.getDeclaredField("lidAngle");
            lidAngleField.setAccessible(true);

            chestUpgradeItem = Class.forName("com.progwml6.ironchest.common.item.ChestUpgradeItem");
            chestUpgradeType = chestUpgradeItem.getDeclaredField("type");
            chestUpgradeType.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            logger.error("Could not setup IronChests compat - some features may not work as expected!", e);
        }
    }

    @SubscribeEvent
    public void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack itemStack = event.getItemStack();
        final var registryName = BuiltInRegistries.ITEM.getKey(itemStack.getItem());
        if (!registryName.getNamespace().equals("ironchest") || !registryName.getPath().contains("upgrade")) { // TODO use a tag here
            return;
        }

        if (chestUpgradeType == null || chestUpgradeItem == null) {
            logger.error("Could not upgrade sorting chest because IronChest compat did not setup correctly");
            return;
        }

        if (event.getLevel().isClientSide || itemStack.isEmpty() || !(chestUpgradeItem.isAssignableFrom(itemStack.getItem().getClass()))) {
            return;
        }

        String upgradeName;
        try {
            upgradeName = ((Enum<?>) chestUpgradeType.get(itemStack.getItem())).name();
        } catch (IllegalAccessException e) {
            logger.error("Could not upgrade sorting chest because IronChest compat did not setup correctly", e);
            return;
        }

        SortingChestType sourceType;
        SortingChestType targetType;
        switch (upgradeName) {
            case "WOOD_TO_IRON":
                sourceType = SortingChestType.WOOD;
                targetType = SortingChestType.IRON;
                break;
            case "IRON_TO_GOLD":
                sourceType = SortingChestType.IRON;
                targetType = SortingChestType.GOLD;
                break;
            case "GOLD_TO_DIAMOND":
                sourceType = SortingChestType.GOLD;
                targetType = SortingChestType.DIAMOND;
                break;
            default:
                return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof SortingChestBlock)) {
            return;
        }

        if (((SortingChestBlock) state.getBlock()).getChestType() != sourceType) {
            return;
        }

        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (!(tileEntity instanceof SortingChestBlockEntity sortingChest)) {
            return;
        }

        if (sortingChest.getNumPlayersUsing() > 0) {
            return;
        }

        Direction facing = state.getValue(SortingChestBlock.FACING);
        CompoundTag serialized = tileEntity.saveWithoutMetadata(level.registryAccess());
        Clearable.tryClear(tileEntity);
        BlockState newState = ModBlocks.sortingChests[targetType.ordinal()].defaultBlockState().setValue(SortingChestBlock.FACING, facing);
        level.setBlockAndUpdate(pos, newState);

        BlockEntity newTileEntity = level.getBlockEntity(pos);
        if (newTileEntity instanceof SortingChestBlockEntity) {
            newTileEntity.loadWithComponents(serialized, level.registryAccess());
        }

        if (!event.getEntity().getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        event.setCanceled(true);
    }

    private class IronChestCapabilityProvider implements ICapabilityProvider,
            ISortingUpgradable {

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return Capabilities.SORTING_UPGRADABLE.orEmpty(cap, LazyOptional.of(() -> this));
        }

        @Override
        public boolean applySortingUpgrade(BlockEntity tileEntity,
                                           ItemStack itemStack,
                                           Player player,
                                           Level level,
                                           BlockPos pos,
                                           Direction side,
                                           double hitX,
                                           double hitY,
                                           double hitZ,
                                           InteractionHand hand) {
            try {
                if (lidAngleField == null || lidAngleField.getFloat(tileEntity) > 0) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                logger.error("Failed to upgrade chest due to incompatibility", e);
                return false;
            }

            BlockState state = level.getBlockState(pos);
            final var registryName = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            SortingChestType chestType;
            switch (registryName.getPath()) {
                case "iron_chest":
                    chestType = SortingChestType.IRON;
                    break;
                case "gold_chest":
                    chestType = SortingChestType.GOLD;
                    break;
                case "diamond_chest":
                    chestType = SortingChestType.DIAMOND;
                    break;
                default:
                    return false;
            }
            CompoundTag storedData = tileEntity.saveWithoutMetadata(level.registryAccess());
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            SortingChestBlock sortingChestBlock = ModBlocks.sortingChests[chestType.ordinal()];
            Clearable.tryClear(tileEntity);
            level.setBlockAndUpdate(pos, sortingChestBlock.defaultBlockState().setValue(SortingChestBlock.FACING, facing));
            SortingChestBlockEntity sortingChest = (SortingChestBlockEntity) level.getBlockEntity(pos);
            if (sortingChest != null) {
                sortingChest.restoreItems(storedData.getList("Items", CompoundTag.TAG_COMPOUND), level.registryAccess());
            }
            return true;
        }
    }

    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        if (ironChestTileEntity != null && ironChestTileEntity.isAssignableFrom(event.getObject().getClass())) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID, "sorting_upgradable"), new IronChestCapabilityProvider());
        }
    }

}
