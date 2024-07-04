package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class IntMenuMessage extends MenuMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<IntMenuMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RefinedRelocation.MOD_ID,
            "int_menu"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int value;

    public IntMenuMessage(String key, int value) {
        super(key);
        this.value = value;
    }

    public static void encode(FriendlyByteBuf buf, IntMenuMessage message) {
        buf.writeUtf(message.key);
        buf.writeInt(message.value);
    }

    public static IntMenuMessage decode(FriendlyByteBuf buf) {
        String key = buf.readUtf(Short.MAX_VALUE);
        int value = buf.readInt();
        return new IntMenuMessage(key, value);
    }

    @Override
    public int getIntValue() {
        return value;
    }
}
