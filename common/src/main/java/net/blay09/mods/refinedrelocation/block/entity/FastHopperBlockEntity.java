package net.blay09.mods.refinedrelocation.block.entity;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.BalmContainerProvider;
import net.blay09.mods.balm.api.container.ContainerUtils;
import net.blay09.mods.balm.api.container.DefaultContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.balm.common.BalmBlockEntity;
import net.blay09.mods.refinedrelocation.block.FastHopperBlock;
import net.blay09.mods.refinedrelocation.menu.FastHopperMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class FastHopperBlockEntity extends BalmBlockEntity implements BalmMenuProvider, BalmContainerProvider, Nameable {

    private static final Predicate<? super Entity> HAS_ITEM_HANDLER = (entity) -> Balm.getProviders().getProvider(entity, Container.class) != null;

    private final DefaultContainer container = createContainer();

    private Component customName;
    private int cooldown;

    public FastHopperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.fastHopper.get(), pos, state);
    }

    public FastHopperBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected DefaultContainer createContainer() {
        return new DefaultContainer(5) {
            @Override
            public void setChanged() {
                FastHopperBlockEntity.this.setChanged();
            }
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FastHopperBlockEntity blockEntity) {
        blockEntity.serverTick(level, pos, state);
    }

    public void serverTick(Level level, BlockPos pos, BlockState state) {
        if (level != null) {
            cooldown--;
            if (cooldown <= 0) {
                boolean isEnabled = state.getValue(FastHopperBlock.ENABLED);
                if (!isEnabled) {
                    return;
                }

                Direction facing = state.getValue(FastHopperBlock.FACING);
                Direction opposite = facing.getOpposite();
                BlockEntity facingTile = level.getBlockEntity(worldPosition.relative(facing));
                Container targetContainer = facingTile != null ? Balm.getProviders().getProvider(facingTile, Container.class) : null;
                boolean hasSpace = false;
                if (targetContainer != null) {
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack itemStack = container.getItem(i);
                        if (!itemStack.isEmpty()) {
                            pushItem(i, targetContainer);
                        }
                        if (!hasSpace && (itemStack.isEmpty() || itemStack.getCount() < itemStack.getMaxStackSize())) {
                            hasSpace = true;
                        }
                    }
                } else {
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        if (container.getItem(i).isEmpty()) {
                            hasSpace = true;
                            break;
                        }
                    }
                }

                if (hasSpace) {
                    Container container = getItemHandlerAt(worldPosition.relative(Direction.UP));
                    if (container != null) {
                        pullItem(container);
                    }
                }

                cooldown = 20;
            }

            for (ItemEntity itemEntity : findItemsAbove()) {
                pullItem(itemEntity);
            }
        }
    }

    private Container getItemHandlerAt(BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity != null) {
            return Balm.getProviders().getProvider(blockEntity, Direction.DOWN, Container.class);
        }

        List<Entity> list = level.getEntities((Entity) null, new AABB(pos.getX() - 0.5, pos.getY() - 0.5, pos.getZ() - 0.5, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), EntitySelector.CONTAINER_ENTITY_SELECTOR);
        if (!list.isEmpty()) {
            Entity entity = list.get(level.random.nextInt(list.size()));
            return (Container) entity;
        }

        return null;
    }

    private List<ItemEntity> findItemsAbove() {
        return level.getEntitiesOfClass(ItemEntity.class, Hopper.SUCK_AABB.move(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()), EntitySelector.ENTITY_STILL_ALIVE);
    }

    private void pushItem(int sourceSlot, Container targetContainer) {
        ItemStack sourceStack = ContainerUtils.extractItem(container, sourceSlot, 64, true);
        ItemStack restStack = ContainerUtils.insertItem(targetContainer, sourceStack, false);
        ContainerUtils.extractItem(container, sourceSlot, restStack.isEmpty() ? sourceStack.getCount() : sourceStack.getCount() - restStack.getCount(), false);
    }

    private void pullItem(Container sourceContainer) {
        for (int i = 0; i < sourceContainer.getContainerSize(); i++) {
            ItemStack sourceStack = ContainerUtils.extractItem(sourceContainer, i, 64, true);
            if (!sourceStack.isEmpty()) {
                ItemStack restStack = ContainerUtils.insertItem(container, sourceStack, false);
                ContainerUtils.extractItem(sourceContainer, i, restStack.isEmpty() ? 64 : sourceStack.getCount() - restStack.getCount(), false);
                break;
            }
        }
    }

    private boolean pullItem(ItemEntity entityItem) {
        ItemStack sourceStack = entityItem.getItem();
        ItemStack restStack = ContainerUtils.insertItem(container, sourceStack, false);
        if (!restStack.isEmpty()) {
            entityItem.setItem(restStack);
        } else {
            entityItem.remove(Entity.RemovalReason.DISCARDED);
        }
        return restStack.isEmpty() || restStack.getCount() < sourceStack.getCount();
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        tag.put("ItemHandler", container.serialize(provider));
        if (customName != null) {
            tag.putString("CustomName", customName.getString());
        }

    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        container.deserialize(tag.getCompound("ItemHandler"), provider);
        customName = tag.contains("CustomName") ? Component.literal(tag.getString("CustomName")) : null;
    }

    public String getUnlocalizedName() {
        return "container.refinedrelocation:fast_hopper";
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(getUnlocalizedName());
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
        return new FastHopperMenu(i, playerInventory, this);
    }

    @Override
    public Component getName() {
        return customName != null ? customName : Component.translatable(getUnlocalizedName());
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    public void onEntityCollision(Entity entity) {
        if (entity instanceof ItemEntity) {
            if (Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move(-worldPosition.getX(), -worldPosition.getY(), -worldPosition.getZ())), FastHopperBlock.INSIDE, BooleanOp.AND)) {
                pullItem((ItemEntity) entity);
            }
        }
    }

}
