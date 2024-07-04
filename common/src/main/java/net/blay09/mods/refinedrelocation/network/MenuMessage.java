package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.refinedrelocation.api.container.IMenuMessage;
import net.blay09.mods.refinedrelocation.api.container.INetworkedMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public abstract class MenuMessage implements IMenuMessage {

    protected final String key;

    public MenuMessage(String key) {
        this.key = key;
    }

    public static void handleServer(ServerPlayer player, MenuMessage message) {
        if (player.containerMenu instanceof INetworkedMenu menu) {
            menu.receivedMessageServer(message);
        }
    }

    public static void handleClient(Player player, MenuMessage message) {
        if (player.containerMenu instanceof INetworkedMenu menu) {
            menu.receivedMessageClient(message);
        }
    }

    @Override
    public String getKey() {
        return key;
    }

}
