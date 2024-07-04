package net.blay09.mods.refinedrelocation.network;

import net.minecraft.network.FriendlyByteBuf;

public class StringMenuMessage extends MenuMessage {

    private final String value;

    public StringMenuMessage(String key, String value) {
        super(key);
        this.value = value;
    }

    public static void encode(StringMenuMessage message, FriendlyByteBuf buf) {
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
