package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.filter.ModFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

@Deprecated
public class LoginSyncListMessage implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LoginSyncListMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            RefinedRelocation.MOD_ID,
            "login_sync_list"));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final int TYPE_MODS = 1;

    private final int type;
    private final String[] values;

    public LoginSyncListMessage(int type, String[] values) {
        this.type = type;
        this.values = values;
    }

    public static void encode(FriendlyByteBuf buf, LoginSyncListMessage message) {
        buf.writeByte(message.type);
        buf.writeShort(message.values.length);
        for (String value : message.values) {
            buf.writeUtf(value);
        }
    }

    public static LoginSyncListMessage decode(FriendlyByteBuf buf) {
        int type = buf.readByte();
        String[] values = new String[buf.readShort()];
        for (int i = 0; i < values.length; i++) {
            values[i] = buf.readUtf(Short.MAX_VALUE);
        }
        return new LoginSyncListMessage(type, values);
    }

    public static void handle(Player player, LoginSyncListMessage message) {
        if (message.type == TYPE_MODS) {
            ModFilter.setModList(message.values);
        }
    }
}
