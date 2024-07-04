package net.blay09.mods.refinedrelocation.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Invoker
    void callOnValueChange(String value);

    @Invoker
    void callRenderHighlight(GuiGraphics guiGraphics, int x, int y, int x2, int y2);

    @Accessor
    void setDisplayPos(int displayPos);

    @Accessor
    int getDisplayPos();

    @Accessor
    int getHighlightPos();

    @Accessor("maxLength")
    int getMaxLen(); // getMaxLength taken by private method

    @Accessor
    void setShiftPressed(boolean shiftPressed);
}