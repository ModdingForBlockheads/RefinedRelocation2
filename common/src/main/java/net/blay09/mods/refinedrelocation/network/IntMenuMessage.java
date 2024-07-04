package net.blay09.mods.refinedrelocation.network;

import net.minecraft.network.FriendlyByteBuf;

public class IntMenuMessage extends MenuMessage {

    private final int value;

    public IntMenuMessage(String key, int value) {
        super(key);
        this.value = value;
    }

    public static void encode(IntMenuMessage message, FriendlyByteBuf buf) {
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
