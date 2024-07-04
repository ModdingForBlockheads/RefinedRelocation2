package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class StringMenuMessage extends MenuMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<StringMenuMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID, "string_menu"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final String value;

    public StringMenuMessage(String key, String value) {
        super(key);
        this.value = value;
    }

    public static void encode(FriendlyByteBuf buf, StringMenuMessage message) {
        buf.writeUtf(message.key);
        buf.writeUtf(message.value);
    }

    public static StringMenuMessage decode(FriendlyByteBuf buf) {
        String key = buf.readUtf(Short.MAX_VALUE);
        String value = buf.readUtf(Short.MAX_VALUE);
        return new StringMenuMessage(key, value);
    }

    @Override
    public String getStringValue() {
        return value;
    }
}
