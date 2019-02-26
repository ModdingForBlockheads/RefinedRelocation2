package net.blay09.mods.refinedrelocation.client.gui;

import net.blay09.mods.refinedrelocation.RefinedRelocation;
import net.blay09.mods.refinedrelocation.api.filter.IFilter;
import net.blay09.mods.refinedrelocation.client.gui.base.GuiContainerMod;
import net.blay09.mods.refinedrelocation.client.gui.base.element.GuiScrollBar;
import net.blay09.mods.refinedrelocation.client.gui.base.element.GuiScrollPane;
import net.blay09.mods.refinedrelocation.client.gui.base.element.IScrollTarget;
import net.blay09.mods.refinedrelocation.client.gui.element.GuiAddFilterButton;
import net.blay09.mods.refinedrelocation.container.ContainerRootFilter;
import net.blay09.mods.refinedrelocation.filter.FilterRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.List;

public class GuiAddFilter extends GuiContainerMod<ContainerRootFilter> implements IScrollTarget {

    private static final ResourceLocation TEXTURE = new ResourceLocation(RefinedRelocation.MOD_ID, "textures/gui/add_filter.png");

    private final GuiRootFilter parentGui;
    private final GuiAddFilterButton[] filterButtons = new GuiAddFilterButton[3];
    private final List<IFilter> filterList;

    private int currentOffset;

    public GuiAddFilter(GuiRootFilter parentGui) {
        super(parentGui.getContainer());
        this.parentGui = parentGui;
        filterList = FilterRegistry.getApplicableFilters(t -> t.isFilterUsable(parentGui.getContainer().getTileEntity()));

        ySize = 210;

        GuiScrollBar scrollBar = new GuiScrollBar(xSize - 16, 28, 78, this);
        rootNode.addChild(scrollBar);

        GuiScrollPane scrollPane = new GuiScrollPane(scrollBar, 8, 28, 152, 80);
        rootNode.addChild(scrollPane);

        int y = 0;
        for (int i = 0; i < filterButtons.length; i++) {
            filterButtons[i] = new GuiAddFilterButton(this);
            filterButtons[i].setPosition(0, y);
            scrollPane.addChild(filterButtons[i]);
            y += filterButtons[i].getHeight();
        }

        setCurrentOffset(0);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || mc.gameSettings.keyBindInventory.isActiveAndMatches(InputMappings.getInputByCode(keyCode, 0))) {
            mc.displayGuiScreen(parentGui);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        fontRenderer.drawString(I18n.format("container.refinedrelocation:add_filter"), 8, 6, 4210752);
        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 96 + 2, 4210752);
    }

    @Override
    public int getVisibleRows() {
        return filterButtons.length;
    }

    @Override
    public int getRowCount() {
        return filterList.size();
    }

    @Override
    public int getCurrentOffset() {
        return currentOffset;
    }

    @Override
    public void setCurrentOffset(int offset) {
        this.currentOffset = offset;

        for (int i = 0; i < filterButtons.length; i++) {
            int filterIndex = currentOffset + i;
            IFilter filter = null;
            if (filterIndex >= 0 && filterIndex < filterList.size()) {
                filter = filterList.get(filterIndex);
            }
            filterButtons[i].setCurrentFilter(filter);
        }
    }

    public GuiRootFilter getParentGui() {
        return parentGui;
    }

}
