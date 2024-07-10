package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.RefinedRelocationAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RequestFilterScreenMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestFilterScreenMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID,
            "request_filter_screen"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final BlockPos pos;
    private final int rootFilterIndex;

    public RequestFilterScreenMessage(BlockPos pos, int rootFilterIndex) {
        this.pos = pos;
        this.rootFilterIndex = rootFilterIndex;
    }

    public static void encode(FriendlyByteBuf buf, RequestFilterScreenMessage message) {
        buf.writeBlockPos(message.pos);
        buf.writeByte(message.rootFilterIndex);
    }

    public static RequestFilterScreenMessage decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int rootFilterIndex = buf.readByte();
        return new RequestFilterScreenMessage(pos, rootFilterIndex);
    }

    public static void handle(ServerPlayer player, RequestFilterScreenMessage message) {
        final var blockEntity = player.level().getBlockEntity(message.pos);
        if (blockEntity != null) {
            RefinedRelocationAPI.openRootFilterGui(player, blockEntity, message.rootFilterIndex);
        }
    }
}
