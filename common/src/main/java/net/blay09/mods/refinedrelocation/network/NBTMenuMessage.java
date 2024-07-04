package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class NBTMenuMessage extends MenuMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<NBTMenuMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID, "nbt_menu"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private final CompoundTag value;

    public NBTMenuMessage(String key, @Nullable CompoundTag value) {
        super(key);
        this.value = value;
    }

    public static void encode(FriendlyByteBuf buf, NBTMenuMessage message) {
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
