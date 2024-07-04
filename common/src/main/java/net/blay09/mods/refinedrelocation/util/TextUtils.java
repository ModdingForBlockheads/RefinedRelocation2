package net.blay09.mods.refinedrelocation.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class TextUtils {

    public static Component formattedTranslation(ChatFormatting formatting, String langKey, Object... args) {
        final var translationTextComponent = Component.translatable(langKey, args);
        translationTextComponent.withStyle(formatting);
        return translationTextComponent;
    }
}
