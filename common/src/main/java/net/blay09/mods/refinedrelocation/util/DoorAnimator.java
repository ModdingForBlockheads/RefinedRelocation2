package net.blay09.mods.refinedrelocation.util;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class DoorAnimator {

    private final BlockEntity blockEntity;
    private final int eventNumPlayers;
    private final int eventForcedOpen;
    private float angle;
    private float prevAngle;
    private int numPlayersUsing;
    private int ticksSinceSync;
    private SoundEvent soundEventOpen;
    private SoundEvent soundEventClose;
    private float openRadius = (float) Math.PI;
    private boolean isForcedOpen;

    public DoorAnimator(BlockEntity blockEntity, int eventNumPlayers, int eventForcedOpen) {
        this.blockEntity = blockEntity;
        this.eventNumPlayers = eventNumPlayers;
        this.eventForcedOpen = eventForcedOpen;
    }

    public DoorAnimator setSoundEventOpen(SoundEvent soundEventOpen) {
        this.soundEventOpen = soundEventOpen;
        return this;
    }

    public DoorAnimator setSoundEventClose(SoundEvent soundEventClose) {
        this.soundEventClose = soundEventClose;
        return this;
    }

    public DoorAnimator setOpenRadius(float openRadius) {
        this.openRadius = openRadius;
        return this;
    }

    public void update() {
        ticksSinceSync++;
        Level world = blockEntity.getLevel();
        if (world == null) {
            return;
        }

        int x = blockEntity.getBlockPos().getX();
        int y = blockEntity.getBlockPos().getY();
        int z = blockEntity.getBlockPos().getZ();
        if (!world.isClientSide&& numPlayersUsing != 0 && (ticksSinceSync + x + y + z) % 200 == 0) {
            numPlayersUsing = 0;
            float range = 5f;
            for (Player player : world.getEntitiesOfClass(Player.class, new AABB(x - range, y - range, z - range, x + 1 + range, y + 1 + range, z + 1 + range))) {
                if (player.containerMenu instanceof IMenuWithDoor menu) {
                    if (menu.matches(blockEntity)) {
                        numPlayersUsing++;
                    }
                }
            }
        }

        prevAngle = angle;

        if ((isForcedOpen || numPlayersUsing > 0) && angle == 0f && soundEventOpen != null) {
            blockEntity.getLevel().playSound(null, blockEntity.getBlockPos(), soundEventOpen, SoundSource.BLOCKS, 0.5f, blockEntity.getLevel().random.nextFloat() * 0.1f + 0.9f);
        }

        float angleSpeed = 0.1f;
        if (((numPlayersUsing == 0 || !isForcedOpen) && angle > 0f) || ((isForcedOpen || numPlayersUsing > 0) && angle < 1f)) {
            float angleBefore = angle;
            if (numPlayersUsing > 0 || isForcedOpen) {
                angle += angleSpeed;
            } else {
                angle -= angleSpeed;
            }
            angle = Math.min(angle, 1f);
            float playCloseSound = 0.5f;
            if (angle < playCloseSound && angleBefore >= playCloseSound && soundEventClose != null) {
                blockEntity.getLevel().playSound(null, blockEntity.getBlockPos(), soundEventClose, SoundSource.BLOCKS, 0.5f, blockEntity.getLevel().random.nextFloat() * 0.1f + 0.9f);
            }
            angle = Math.max(angle, 0f);
        }
    }

    public void toggleForcedOpen() {
        setForcedOpen(!isForcedOpen);
    }

    public boolean isForcedOpen() {
        return isForcedOpen;
    }

    public void setForcedOpen(boolean isForcedOpen) {
        this.isForcedOpen = isForcedOpen;
        Level level = blockEntity.getLevel();
        if (level != null) {
            level.blockEvent(blockEntity.getBlockPos(), blockEntity.getBlockState().getBlock(), 2, isForcedOpen ? 1 : 0);
        }
    }

    public boolean receiveClientEvent(int id, int type) {
        if (id == eventNumPlayers) {
            numPlayersUsing = type;
            return true;
        } else if (id == eventForcedOpen) {
            isForcedOpen = type == 1;
            return true;
        }
        return false;
    }

    public void openContainer(Player player) {
        if (!player.isSpectator()) {
            numPlayersUsing = Math.max(0, numPlayersUsing + 1);
            Block block = blockEntity.getBlockState().getBlock();
            Level level = blockEntity.getLevel();
            if (level != null) {
                level.blockEvent(blockEntity.getBlockPos(), block, eventNumPlayers, numPlayersUsing);
                level.updateNeighborsAt(blockEntity.getBlockPos(), block);
                level.updateNeighborsAt(blockEntity.getBlockPos().below(), block);
            }
        }
    }

    public void closeContainer(Player player) {
        if (!player.isSpectator()) {
            numPlayersUsing--;
            Block block = blockEntity.getBlockState().getBlock();
            Level level = blockEntity.getLevel();
            if (level != null) {
                level.blockEvent(blockEntity.getBlockPos(), block, eventNumPlayers, numPlayersUsing);
                level.updateNeighborsAt(blockEntity.getBlockPos(), block);
                level.updateNeighborsAt(blockEntity.getBlockPos().below(), block);
            }
        }
    }

    public float getRenderAngle(float partialTicks) {
        float renderAngle = prevAngle + (angle - prevAngle) * partialTicks;
        renderAngle = 1f - renderAngle;
        renderAngle = 1f - renderAngle * renderAngle * renderAngle;
        return (float) ((Math.PI / openRadius) * renderAngle);
    }

    public int getNumPlayersUsing() {
        return numPlayersUsing;
    }

    public void setNumPlayersUsing(int numPlayersUsing) {
        this.numPlayersUsing = numPlayersUsing;
    }
}
