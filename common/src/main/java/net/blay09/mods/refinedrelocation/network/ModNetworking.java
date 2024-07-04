package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.balm.api.network.BalmNetworking;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(ReturnToParentScreenMessage.TYPE, ReturnToParentScreenMessage.class, (message, buf) -> {
        }, it -> new ReturnToParentScreenMessage(), ReturnToParentScreenMessage::handle);
        networking.registerClientboundPacket(FilterPreviewMessage.TYPE, FilterPreviewMessage.class, FilterPreviewMessage::encode, FilterPreviewMessage::decode, FilterPreviewMessage::handle);
        networking.registerClientboundPacket(LoginSyncListMessage.TYPE, LoginSyncListMessage.class, LoginSyncListMessage::encode, LoginSyncListMessage::decode, LoginSyncListMessage::handle);
        networking.registerServerboundPacket(RequestFilterScreenMessage.TYPE, RequestFilterScreenMessage.class, RequestFilterScreenMessage::encode, RequestFilterScreenMessage::decode, RequestFilterScreenMessage::handle);

        networking.registerServerboundPacket(IntMenuMessage.TYPE, IntMenuMessage.class, IntMenuMessage::encode, IntMenuMessage::decode, IntMenuMessage::handleServer);
        networking.registerServerboundPacket(StringMenuMessage.TYPE, StringMenuMessage.class, StringMenuMessage::encode, StringMenuMessage::decode, StringMenuMessage::handleServer);
        networking.registerServerboundPacket(ByteArrayMenuMessage.TYPE, ByteArrayMenuMessage.class, ByteArrayMenuMessage::encode, ByteArrayMenuMessage::decode, ByteArrayMenuMessage::handleServer);
        networking.registerServerboundPacket(NBTMenuMessage.TYPE, NBTMenuMessage.class, NBTMenuMessage::encode, NBTMenuMessage::decode, NBTMenuMessage::handleServer);
        networking.registerServerboundPacket(IndexedIntMenuMessage.TYPE, IndexedIntMenuMessage.class, IndexedIntMenuMessage::encode, IndexedIntMenuMessage::decode, IndexedIntMenuMessage::handleServer);

        networking.registerClientboundPacket(IntMenuMessage.TYPE, IntMenuMessage.class, IntMenuMessage::encode, IntMenuMessage::decode, IntMenuMessage::handleClient);
        networking.registerClientboundPacket(StringMenuMessage.TYPE, StringMenuMessage.class, StringMenuMessage::encode, StringMenuMessage::decode, StringMenuMessage::handleClient);
        networking.registerClientboundPacket(ByteArrayMenuMessage.TYPE, ByteArrayMenuMessage.class, ByteArrayMenuMessage::encode, ByteArrayMenuMessage::decode, ByteArrayMenuMessage::handleClient);
        networking.registerClientboundPacket(NBTMenuMessage.TYPE, NBTMenuMessage.class, NBTMenuMessage::encode, NBTMenuMessage::decode, NBTMenuMessage::handleClient);
        networking.registerClientboundPacket(IndexedIntMenuMessage.TYPE, IndexedIntMenuMessage.class, IndexedIntMenuMessage::encode, IndexedIntMenuMessage::decode, IndexedIntMenuMessage::handleClient);
    }

}
