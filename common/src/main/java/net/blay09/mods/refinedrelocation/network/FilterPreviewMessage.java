package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.fabric.client.FilterPreviewHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class FilterPreviewMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FilterPreviewMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID,
            "filter_preview"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final int STATE_FAILURE = 0;
    public static final int STATE_SUCCESS = 1;

    public static final int INVENTORY_SLOT_COUNT = 36;

    private final byte[] slotStates;

    public FilterPreviewMessage(byte[] slotStates) {
        this.slotStates = slotStates;
        assert slotStates.length == INVENTORY_SLOT_COUNT;
    }

    public static void encode(FriendlyByteBuf buf, FilterPreviewMessage message) {
        buf.writeBytes(message.slotStates);
    }

    public static FilterPreviewMessage decode(FriendlyByteBuf buf) {
        byte[] slotStates = new byte[INVENTORY_SLOT_COUNT];
        buf.readBytes(slotStates);
        return new FilterPreviewMessage(slotStates);
    }

    public static void handle(Player player, FilterPreviewMessage message) {
        FilterPreviewHandler.setSlotStates(message.slotStates);
    }
}
