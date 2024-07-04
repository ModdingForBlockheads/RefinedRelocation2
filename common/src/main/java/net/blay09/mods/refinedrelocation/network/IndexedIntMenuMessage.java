package net.blay09.mods.refinedrelocation.network;

import net.minecraft.network.FriendlyByteBuf;

public class IndexedIntMenuMessage extends MenuMessage {

    private final int index;
    private final int value;

    public IndexedIntMenuMessage(String key, int index, int value) {
        super(key);
        this.index = index;
        this.value = value;
    }

    public static void encode(IndexedIntMenuMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.key);
        buf.writeInt(message.index);
        buf.writeInt(message.value);
    }

    public static IndexedIntMenuMessage decode(FriendlyByteBuf buf) {
        String key = buf.readUtf(Short.MAX_VALUE);
        int index = buf.readInt();
        int value = buf.readInt();
        return new IndexedIntMenuMessage(key, index, value);
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public int getIntValue() {
        return value;
    }
}
