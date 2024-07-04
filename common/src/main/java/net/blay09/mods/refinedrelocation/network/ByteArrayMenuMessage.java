package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class ByteArrayMenuMessage extends MenuMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ByteArrayMenuMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID, "byte_array_menu"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final byte[] value;

    public ByteArrayMenuMessage(String key, byte[] value) {
        super(key);
        this.value = value;
    }

    public static void encode(FriendlyByteBuf buf, ByteArrayMenuMessage message) {
        buf.writeUtf(message.key);
        buf.writeByteArray(message.value);
    }

    public static ByteArrayMenuMessage decode(FriendlyByteBuf buf) {
        String key = buf.readUtf(Short.MAX_VALUE);
        byte[] value = buf.readByteArray();
        return new ByteArrayMenuMessage(key, value);
    }

    @Override
    public byte[] getByteArrayValue() {
        return value;
    }
}
