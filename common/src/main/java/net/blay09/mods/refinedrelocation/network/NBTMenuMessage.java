package net.blay09.mods.refinedrelocation.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public class NBTMenuMessage extends MenuMessage {

    private final CompoundTag value;

    public NBTMenuMessage(String key, @Nullable CompoundTag value) {
        super(key);
        this.value = value;
    }

    public static void encode(NBTMenuMessage message, FriendlyByteBuf buf) {
        buf.writeUtf(message.key);
        buf.writeNbt(message.value);
    }

    public static NBTMenuMessage decode(FriendlyByteBuf buf) {
        String key = buf.readUtf(Short.MAX_VALUE);
        CompoundTag value = buf.readNbt();
        return new NBTMenuMessage(key, value);
    }

    @Override
    public CompoundTag getNBTValue() {
        return value != null ? value : new CompoundTag();
    }
}
