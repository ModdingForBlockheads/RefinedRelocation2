package net.blay09.mods.refinedrelocation.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.blay09.mods.refinedrelocation.api.client.IFilterPreviewScreen;
import net.blay09.mods.refinedrelocation.network.FilterPreviewMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;

public class FilterPreviewHandler {

    private static byte[] slotStates;

    public static void setSlotStates(byte[] slotStates) {
        FilterPreviewHandler.slotStates = slotStates;
    }

    public static void onDrawScreen(ScreenDrawEvent.Post event) {
        if (slotStates != null) {
            if (event.getScreen() instanceof IFilterPreviewScreen screen) {
                final var player = Minecraft.getInstance().player;
                final var menu = screen.getFilterContainer();
                final PoseStack poseStack = event.getGuiGraphics().pose();
                poseStack.pushPose();
                poseStack.translate(0, 0, 200);
                for (Slot slot : menu.slots) {
                    if (slot.container == player.getInventory()) {
                        int guiLeft = screen.getFilterLeftPos();
                        int guiTop = screen.getFilterTopPos();
                        if (slotStates[slot.getContainerSlot()] == FilterPreviewMessage.STATE_SUCCESS) {
                            event.getGuiGraphics().fill(guiLeft + slot.x, guiTop + slot.y, guiLeft + slot.x + 16, guiTop + slot.y + 16, 0x5500FF00);
                        }
                    }
                }
                poseStack.popPose();
            } else {
                slotStates = null;
            }
        }
    }

}
