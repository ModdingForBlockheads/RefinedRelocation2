package net.blay09.mods.refinedrelocation.network;

import net.minecraft.network.FriendlyByteBuf;

public class ByteArrayMenuMessage extends MenuMessage {

    private final byte[] value;

    public ByteArrayMenuMessage(String key, byte[] value) {
        super(key);
        this.value = value;
    }

    public static void encode(ByteArrayMenuMessage message, FriendlyByteBuf buf) {
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
