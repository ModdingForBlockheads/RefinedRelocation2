package net.blay09.mods.refinedrelocation.block.entity;

import net.minecraft.world.Container;

import java.util.Collection;

public interface IDroppableItemHandler {
    Collection<Container> getDroppedItemHandlers();
}
