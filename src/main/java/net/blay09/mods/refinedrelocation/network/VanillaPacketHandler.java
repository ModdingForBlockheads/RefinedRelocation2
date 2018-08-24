package net.blay09.mods.refinedrelocation.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;

public class VanillaPacketHandler {

    public static void sendTileEntityUpdate(TileEntity tileEntity) {
        if (tileEntity.getWorld().isRemote) {
            return;
        }

        SPacketUpdateTileEntity updatePacket = null;
        WorldServer worldServer = (WorldServer) tileEntity.getWorld();
        int chunkX = tileEntity.getPos().getX() >> 4;
        int chunkZ = tileEntity.getPos().getZ() >> 4;
        for (EntityPlayer entityPlayer : tileEntity.getWorld().playerEntities) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) entityPlayer;
            if (worldServer.getPlayerChunkMap().isPlayerWatchingChunk(entityPlayerMP, chunkX, chunkZ)) {
                if (updatePacket == null) {
                    updatePacket = tileEntity.getUpdatePacket();
                    if (updatePacket == null) {
                        return;
                    }
                }

                entityPlayerMP.connection.sendPacket(updatePacket);
            }
        }
    }

}
