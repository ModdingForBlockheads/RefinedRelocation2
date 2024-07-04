package net.blay09.mods.refinedrelocation.network;

import net.blay09.mods.balm.api.network.BalmNetworking;
import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.minecraft.resources.ResourceLocation;

public class ModNetworking {

    public static void initialize(BalmNetworking networking) {
        networking.registerServerboundPacket(id("return_to_parent_screen"), ReturnToParentScreenMessage.class, (message, buf) -> {
        }, it -> new ReturnToParentScreenMessage(), ReturnToParentScreenMessage::handle);
        networking.registerClientboundPacket(id("filter_preview"), FilterPreviewMessage.class, FilterPreviewMessage::encode, FilterPreviewMessage::decode, FilterPreviewMessage::handle);
        networking.registerClientboundPacket(id("login_sync_list"), LoginSyncListMessage.class, LoginSyncListMessage::encode, LoginSyncListMessage::decode, LoginSyncListMessage::handle);
        networking.registerServerboundPacket(id("request_filter_screen"), RequestFilterScreenMessage.class, RequestFilterScreenMessage::encode, RequestFilterScreenMessage::decode, RequestFilterScreenMessage::handle);

        networking.registerServerboundPacket(id("post_menu_int"), IntMenuMessage.class, IntMenuMessage::encode, IntMenuMessage::decode, IntMenuMessage::handleServer);
        networking.registerServerboundPacket(id("post_menu_string"), StringMenuMessage.class, StringMenuMessage::encode, StringMenuMessage::decode, StringMenuMessage::handleServer);
        networking.registerServerboundPacket(id("post_menu_byte_array"), ByteArrayMenuMessage.class, ByteArrayMenuMessage::encode, ByteArrayMenuMessage::decode, ByteArrayMenuMessage::handleServer);
        networking.registerServerboundPacket(id("post_menu_nbt"), NBTMenuMessage.class, NBTMenuMessage::encode, NBTMenuMessage::decode, NBTMenuMessage::handleServer);
        networking.registerServerboundPacket(id("post_menu_indexed_int"), IndexedIntMenuMessage.class, IndexedIntMenuMessage::encode, IndexedIntMenuMessage::decode, IndexedIntMenuMessage::handleServer);

        networking.registerClientboundPacket(id("sync_menu_int"), IntMenuMessage.class, IntMenuMessage::encode, IntMenuMessage::decode, IntMenuMessage::handleClient);
        networking.registerClientboundPacket(id("sync_menu_string"), StringMenuMessage.class, StringMenuMessage::encode, StringMenuMessage::decode, StringMenuMessage::handleClient);
        networking.registerClientboundPacket(id("sync_menu_byte_array"), ByteArrayMenuMessage.class, ByteArrayMenuMessage::encode, ByteArrayMenuMessage::decode, ByteArrayMenuMessage::handleClient);
        networking.registerClientboundPacket(id("sync_menu_nbt"), NBTMenuMessage.class, NBTMenuMessage::encode, NBTMenuMessage::decode, NBTMenuMessage::handleClient);
        networking.registerClientboundPacket(id("sync_menu_indexed_int"), IndexedIntMenuMessage.class, IndexedIntMenuMessage::encode, IndexedIntMenuMessage::decode, IndexedIntMenuMessage::handleClient);
    }

    private static ResourceLocation id(String path) {
        return new ResourceLocation(RefinedRelocation.MOD_ID, path);
    }

}
