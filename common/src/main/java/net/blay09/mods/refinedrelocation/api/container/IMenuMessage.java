package net.blay09.mods.refinedrelocation.api.container;

import net.minecraft.nbt.CompoundTag;

public interface IMenuMessage {

    String getKey();

    default int getIndex() {
        return 0;
    }

    default int getIntValue() {
        return 0;
    }

    default String getStringValue() {
        return "";
    }

    default CompoundTag getNBTValue() {
        return new CompoundTag();
    }

    default byte[] getByteArrayValue() {
        return new byte[0];
    }

}
