package net.blay09.mods.refinedrelocation.client.gui;

import net.blay09.mods.refinedrelocation.api.client.IDrawable;
import net.blay09.mods.refinedrelocation.client.gui.base.GuiTextureSprite;
import net.blay09.mods.refinedrelocation.client.gui.base.GuiTextureSpriteButton;
import net.minecraft.client.gui.GuiGraphics;

public class GuiTextures {

    public static final GuiTextureSprite NONE = new GuiTextureSprite(0, 0, 0, 0) {
        @Override
        public void draw(GuiGraphics guiGraphics, double x, double y) {
            // NOP
        }

        @Override
        public void draw(GuiGraphics guiGraphics, double x, double y, double width, double height) {
            // NOP
        }
    };

    public static final GuiTextureSpriteButton BUTTON_NONE = new GuiTextureSpriteButton(NONE, NONE, NONE);
    public static final GuiTextureSpriteButton DELETE_FILTER = new GuiTextureSpriteButton(
            new GuiTextureSprite(48, 26, 16, 16),
            new GuiTextureSprite(64, 26, 16, 16),
            new GuiTextureSprite(80, 26, 16, 16)
    );

    public static final GuiTextureSpriteButton OPEN_FILTER = new GuiTextureSpriteButton(
            new GuiTextureSprite(48, 10, 16, 16),
            new GuiTextureSprite(64, 10, 16, 16),
            new GuiTextureSprite(80, 10, 16, 16)
    );

    public static final GuiTextureSpriteButton CHEST_BUTTON = new GuiTextureSpriteButton(
            new GuiTextureSprite(0, 10, 16, 16),
            new GuiTextureSprite(16, 10, 16, 16),
            new GuiTextureSprite(32, 10, 16, 16)
    );

    public static final GuiTextureSpriteButton FILTER_WHITELIST = new GuiTextureSpriteButton(
            new GuiTextureSprite(0, 42, 16, 16),
            new GuiTextureSprite(16, 42, 16, 16),
            NONE
    );

    public static final GuiTextureSpriteButton FILTER_BLACKLIST = new GuiTextureSpriteButton(
            new GuiTextureSprite(0, 26, 16, 16),
            new GuiTextureSprite(16, 26, 16, 16),
            NONE
    );

    public static final GuiTextureSpriteButton[] SIDE_BUTTONS = new GuiTextureSpriteButton[] {
            new GuiTextureSpriteButton( // BACK
                    new GuiTextureSprite(64, 113, 16, 16),
                    new GuiTextureSprite(64, 129, 16 ,16),
                    NONE
            ),
            new GuiTextureSpriteButton( // LEFT
                    new GuiTextureSprite(32, 113, 16, 16),
                    new GuiTextureSprite(32, 129, 16 ,16),
                    NONE
            ),
            new GuiTextureSpriteButton( // RIGHT
                    new GuiTextureSprite(16, 113, 16, 16),
                    new GuiTextureSprite(16, 129, 16 ,16),
                    NONE
            ),
            new GuiTextureSpriteButton( // TOP
                    new GuiTextureSprite(0, 113, 16, 16),
                    new GuiTextureSprite(0, 129, 16 ,16),
                    NONE
            ),
            new GuiTextureSpriteButton( // BOTTOM
                    new GuiTextureSprite(48, 113, 16, 16),
                    new GuiTextureSprite(48, 129, 16 ,16),
                    NONE
            ),
            new GuiTextureSpriteButton( // FRONT
                    new GuiTextureSprite(80, 113, 16, 16),
                    new GuiTextureSprite(80, 113, 16, 16),
                    NONE
            )
    };

    public static final IDrawable SCROLLBAR_TOP = new GuiTextureSprite(48, 65, 11, 11);
    public static final IDrawable SCROLLBAR_MIDDLE = new GuiTextureSprite(48, 76, 11, 11);
    public static final IDrawable SCROLLBAR_BOTTOM = new GuiTextureSprite(59, 65, 11, 11);
    public static final IDrawable CHECKLIST = new GuiTextureSprite(0, 0, 10, 10);
    public static final IDrawable CHECKLIST_CHECKED = new GuiTextureSprite(10, 0, 10, 10);
    public static final IDrawable FILTER_SLOT = new GuiTextureSprite(24, 65, 24, 24);
    public static final IDrawable FILTER_SEPARATOR = new GuiTextureSprite(20, 0, 9, 9);
    public static final IDrawable PRESET_FILTER_ICON = new GuiTextureSprite(48, 89, 24, 24);
    public static final IDrawable NAME_FILTER_ICON = new GuiTextureSprite(24, 89, 24, 24);
    public static final IDrawable CREATIVE_TAB_FILTER_ICON = new GuiTextureSprite(0, 89, 24, 24);
    public static final IDrawable MOD_FILTER_ICON = new GuiTextureSprite(160, 0, 96, 96);
    public static final IDrawable SAME_ITEM_FILTER_ICON = new GuiTextureSprite(160, 96, 96, 96);
    public static final IDrawable SAME_MOD_FILTER_ICON = new GuiTextureSprite(0, 160, 96, 96);
}
