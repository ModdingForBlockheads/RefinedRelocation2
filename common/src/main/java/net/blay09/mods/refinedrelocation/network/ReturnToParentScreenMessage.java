package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.container.IHasReturnCallback;
import net.blay09.mods.refinedrelocation.api.container.ReturnCallback;
import net.blay09.mods.refinedrelocation.menu.RootFilterMenu;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReturnToParentScreenMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ReturnToParentScreenMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID,
            "return_to_parent_screen"));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerPlayer player, ReturnToParentScreenMessage message) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu instanceof RootFilterMenu rootFilterMenu) {
            BlockEntity blockEntity = rootFilterMenu.getBlockEntity();
            if (blockEntity instanceof BalmMenuProvider<?> menuProvider) {
                Balm.getNetworking().openGui(player, menuProvider);
            }
        } else if (menu instanceof IHasReturnCallback returnable) {
            ReturnCallback callback = returnable.getReturnCallback();
            if (callback != null) {
                callback.returnToParentGui();
            }
        }
    }
}
