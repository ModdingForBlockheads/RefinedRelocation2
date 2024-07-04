package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class IndexedIntMenuMessage extends MenuMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<IndexedIntMenuMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID, "indexed_int_menu"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final int index;
    private final int value;

    public IndexedIntMenuMessage(String key, int index, int value) {
        super(key);
        this.index = index;
        this.value = value;
    }

    public static void encode(FriendlyByteBuf buf, IndexedIntMenuMessage message) {
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
