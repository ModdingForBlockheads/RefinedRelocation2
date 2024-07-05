package net.blay09.mods.refinedrelocation.client.gui.base.element;

import net.blay09.mods.refinedrelocation.mixin.EditBoxAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class MultiLineTextFieldWidget extends EditBox implements IScrollTarget {

    private static final int ENABLED_COLOR = 0xE0E0E0;
    private static final int DISABLED_COLOR = 0x707070;
    private static final int PADDING = 2;

    private final Font font;

    private int scrollOffset;
    private int cursorCounter;

    private String[] renderCache;
    private int lastRowCount;

    private float ticksSinceLastHistory;
    private List<String> history = new ArrayList<>();
    private List<String> redoHistory = new ArrayList<>();

    private Map<String, String> debugRenders = new HashMap<>();

    public MultiLineTextFieldWidget(int x, int y, int width, int height) {
        super(Minecraft.getInstance().font, x, y, width, height, Component.empty());
        font = Minecraft.getInstance().font;
    }

    private int getStartOfLine(int position, int iterations) {
        String text = getValue();
        int startOfLine = position;
        for (int i = 0; i < iterations; i++) {
            startOfLine = text.lastIndexOf('\n', startOfLine - 1);
        }
        return startOfLine != -1 ? startOfLine + 1 : 0;
    }

    private int getEndOfLine(int position, int iteration) {
        String text = getValue();
        int endOfLine = position - 1;
        for (int i = 0; i < iteration; i++) {
            endOfLine = text.indexOf('\n', endOfLine + 1);
            if (endOfLine == -1) {
                return text.length();
            }
        }
        return endOfLine != -1 ? endOfLine : text.length();
    }

    private int getLineLength(int position) {
        return getEndOfLine(position, 1) - getStartOfLine(position, 1);
    }

    private int getStartOfWord(int position) {
        String text = getValue();
        if (text.isEmpty()) {
            return 0;
        }

        position = Math.max(Math.min(position, text.length() - 1), 0);
        if (text.charAt(position) == '\n') {
            return position;
        }
        boolean foundAlphabetic = false;
        for (int i = position; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i + 1;
            }
            if (Character.isAlphabetic(c)) {
                foundAlphabetic = true;
            } else if (foundAlphabetic) {
                return i + 1;
            }
        }
        return 0;
    }

    private int getStartOfNextWord(int position) {
        String text = getValue();
        position = Math.max(Math.min(position, text.length() - 1), 0);
        if (text.charAt(position) == '\n') {
            return position;
        }
        boolean foundNonAlphabetic = false;
        for (int i = position; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i;
            }
            if (!Character.isAlphabetic(c)) {
                foundNonAlphabetic = true;
            } else if (foundNonAlphabetic) {
                return i;
            }
        }
        return text.length();
    }

    @Override
    public void setHighlightPos(int cursorPosition) {
        String text = getValue();
        super.setHighlightPos(Math.min(Math.max(cursorPosition, 0), text.length()));

        // Vertical Scrolling
        int cursorLine = 0;
        for (int i = 0; i < this.getCursorPosition(); i++) {
            if (text.charAt(i) == '\n') {
                cursorLine++;
            }
        }

        int innerHeight = height - PADDING;
        int cursorRenderY = (cursorLine - scrollOffset) * font.lineHeight + PADDING;
        if (cursorRenderY < 0) {
            scroll(0, cursorRenderY / font.lineHeight - 1);
        } else if (cursorRenderY > innerHeight - font.lineHeight) {
            scroll(0, (cursorRenderY - innerHeight) / font.lineHeight + 1);
        }

        // Horizontal Scrolling
        // Reset the scroll offset we got from Vanilla's implementation, since that considered the text as one single line
        setDisplayPos(0);
        final int innerWidth = width - PADDING;
        final int startOfLine = getStartOfLine(cursorPosition, 1);
        final int endOfLine = getEndOfLine(cursorPosition, 1);
        final int cursorPositionX = Math.min(getLineLength(cursorPosition), cursorPosition - startOfLine);
        final String lineText = text.substring(startOfLine, endOfLine);
        setDisplayPos(Math.max(Math.min(getDisplayPos(), lineText.length()), 0));
        final String renderText = font.plainSubstrByWidth(lineText.substring(getDisplayPos()), innerWidth);
        // Allow scrolling back when you get to the beginning
        if (cursorPositionX == getDisplayPos()) {
            setDisplayPos(getDisplayPos() - renderText.length());
        }

        setDisplayPos(Math.max(Math.min(getDisplayPos(), lineText.length()), 0));
        final int offset = renderText.length() + getDisplayPos();
        if (cursorPositionX > offset) {
            setDisplayPos(getDisplayPos() + cursorPositionX - offset);
        } else if (cursorPositionX <= getDisplayPos()) {
            setDisplayPos(-cursorPositionX);
        }

        setDisplayPos(Math.max(Math.min(getDisplayPos(), lineText.length()), 0));
    }

    private void scroll(int x, int y) {
        setDisplayPos(Math.max(getDisplayPos() + x, 0));
        scrollOffset = Math.max(Math.min(scrollOffset + y, getRowCount() - getVisibleRows()), 0);
    }

    private void deleteBack(boolean wholeWord) {
        int cursorPosition = Math.max(getHighlightPos(), getCursorPosition());
        int deleteCount = Math.max(1, getHighlighted().length());
        if (wholeWord) {
            deleteCount = cursorPosition - getStartOfWord(cursorPosition);
        }

        if (cursorPosition > 0) {
            String text = getValue();
            text = text.substring(0, cursorPosition - deleteCount) + text.substring(cursorPosition);
            setValue(text);

            setCursorPosition(cursorPosition - deleteCount);
            renderCache = null;
        }
    }

    private void deleteFront(boolean wholeWord) {
        int cursorPosition = Math.min(getHighlightPos(), getCursorPosition());
        int deleteCount = Math.max(1, getHighlighted().length());
        if (wholeWord) {
            deleteCount = getStartOfNextWord(cursorPosition) - cursorPosition;
        }

        String text = getValue();
        if (cursorPosition < text.length()) {
            text = text.substring(0, cursorPosition) + text.substring(cursorPosition + deleteCount);
            setValue(text);

            setCursorPosition(cursorPosition);
            renderCache = null;
        }
    }

    @Override
    public void insertText(String textToWrite) {
        super.insertText(textToWrite);
        renderCache = null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean isInside = mouseX >= getX() && mouseX < (getX() + width) && mouseY >= getY() && mouseY < (getY() + height);

        if (isFocused() && isInside && mouseButton == 0) {
            double relX = mouseX - getX();
            double relY = mouseY - getY() - font.lineHeight + 5;
            int lineNumber = Math.round((float) relY / (float) font.lineHeight) + scrollOffset + 1;
            int startOfLine = getStartOfLine(getEndOfLine(0, lineNumber), 1);
            int endOfLine = getEndOfLine(startOfLine, 1);
            if (startOfLine == endOfLine) {
                setCursorPosition(startOfLine);
            } else {
                int lineRenderStartIndex = Math.max(startOfLine + getDisplayPos(), 0);
                int lineRenderEndIndex = Math.max(0, endOfLine);
                if (lineRenderStartIndex <= lineRenderEndIndex) {
                    String renderText = font.plainSubstrByWidth(getValue().substring(lineRenderStartIndex, lineRenderEndIndex), width - PADDING);
                    setCursorPosition(startOfLine + font.plainSubstrByWidth(renderText, (int) relX).length() + getDisplayPos());
                } else {
                    setCursorPosition(endOfLine);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }

        String keyName = GLFW.glfwGetKeyName(keyCode, scanCode);
        if ("z".equals(keyName) && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()) {
            if (!history.isEmpty()) {
                String redoText = getValue();
                redoHistory.add(redoText);
                setValue(history.remove(history.size() - 1));
            }
        } else if (("z".equals(keyName) && Screen.hasControlDown() && Screen.hasShiftDown() && !Screen.hasAltDown() || ("y".equals(keyName) && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()))) {
            if (!redoHistory.isEmpty()) {
                String undoText = getValue();
                setValue(redoHistory.remove(redoHistory.size() - 1));
                history.add(undoText);
            }
        }

        if (Screen.isPaste(keyCode)) {
            String clipboardString = Minecraft.getInstance().keyboardHandler.getClipboard();
            String[] lines = clipboardString.split("\n");
            for (String line : lines) {
                insertText(line);
                addNewLine();
            }

            return true;
        }

        int cursorPosition = getCursorPosition();
        switch (keyCode) {
            case GLFW.GLFW_KEY_END -> {
                if (Screen.hasControlDown()) {
                    setCursorPosition(getValue().length());
                } else {
                    setCursorPosition(getEndOfLine(cursorPosition, 1));
                }
                return true;
            }
            case GLFW.GLFW_KEY_HOME -> {
                if (Screen.hasControlDown()) {
                    setCursorPosition(0);
                } else {
                    setCursorPosition(getStartOfLine(cursorPosition, 1));
                }
                return true;
            }
            case GLFW.GLFW_KEY_LEFT -> {
                if (Screen.hasControlDown()) {
                    setCursorPosition(getStartOfWord(cursorPosition - 1));
                } else {
                    setCursorPosition(cursorPosition - 1);
                }
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                if (Screen.hasControlDown()) {
                    setCursorPosition(getStartOfNextWord(cursorPosition + 1));
                } else {
                    setCursorPosition(cursorPosition + 1);
                }
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                if (Screen.hasControlDown()) {
                    scroll(0, -1);
                } else {
                    int upLine = getStartOfLine(cursorPosition, 2);
                    setCursorPosition(upLine + Math.min(getLineLength(upLine), (cursorPosition - getStartOfLine(cursorPosition, 1))));
                }
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                if (Screen.hasControlDown()) {
                    scroll(0, 1);
                } else {
                    int downLine = getEndOfLine(cursorPosition, 2);
                    setCursorPosition(getStartOfLine(downLine, 1) + Math.min(getLineLength(downLine), (cursorPosition - getStartOfLine(cursorPosition, 1))));
                }
                return true;
            }
            case GLFW.GLFW_KEY_ENTER -> {
                if (active) {
                    addNewLine();
                }
                return true;
            }
            case GLFW.GLFW_KEY_DELETE -> {
                if (active) {
                    deleteFront(Screen.hasControlDown());
                }
                return true;
            }
            case GLFW.GLFW_KEY_BACKSPACE -> {
                if (active) {
                    deleteBack(Screen.hasControlDown());
                }
                return true;
            }
        }

        boolean result = super.keyPressed(keyCode, scanCode, modifiers);

        updateHistory();
        return result;
    }

    @Override
    public boolean charTyped(char charCode, int keyCode) {
        boolean result = super.charTyped(charCode, keyCode);
        updateHistory();
        return result;
    }

    private void updateHistory() {
        int historyIntervalSeconds = 5;
        if (ticksSinceLastHistory >= 20 * historyIntervalSeconds) {
            String lastHistoryText = null;
            if (!history.isEmpty()) {
                lastHistoryText = history.get(history.size() - 1);
            }
            String text = getValue();
            if (!Objects.equals(lastHistoryText, text)) {
                history.add(text);
            }
            ticksSinceLastHistory = 0;
        }
    }

    private void addNewLine() {
        writeUnfiltered("\n");
    }

    public void tick() { // TODO not called atm
        scrollOffset = Math.max(Math.min(scrollOffset, getRowCount() - getVisibleRows()), 0);

        cursorCounter++;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        ticksSinceLastHistory += partialTicks;

        if (isVisible()) {
            guiGraphics.fill(getX() - 1, getY() - 1, getX() + width + 1, getY() + height + 1, 0xFFEEEEEE);
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0xFF000000);

            if (renderCache == null) {
                renderCache = getValue().split("\n", -1);
            }

            for (int i = scrollOffset; i < renderCache.length; i++) {
                int y = (i - scrollOffset) * font.lineHeight;
                if (y + font.lineHeight >= height) {
                    break;
                }

                if (getDisplayPos() >= renderCache[i].length()) {
                    continue;
                }

                String renderText = font.plainSubstrByWidth(renderCache[i].substring(getDisplayPos()), width - PADDING);
                guiGraphics.drawString(font, renderText, this.getX() + PADDING, this.getY() + PADDING + y, active ? ENABLED_COLOR : DISABLED_COLOR);
            }

            int cursorLine = 0;
            int lastLineStartIdx = 0;
            for (int i = 0; i < getCursorPosition(); i++) {
                if (getValue().charAt(i) == '\n') {
                    cursorLine++;
                    lastLineStartIdx = i + 1;
                }
            }

            if (cursorCounter / 6 % 2 == 0 && (cursorLine - scrollOffset) * font.lineHeight >= 0 && (cursorLine - scrollOffset + 1) * font.lineHeight < height + PADDING) {
                int startIndex = lastLineStartIdx + getDisplayPos();
                int endIndex = getCursorPosition();
                if (startIndex <= endIndex) {
                    int cursorX = getX() + font.width(getValue().substring(startIndex, endIndex)) + PADDING;
                    int cursorY = getY() + (cursorLine - scrollOffset) * font.lineHeight + PADDING;
                    guiGraphics.fill(cursorX, cursorY, cursorX + 1, cursorY + 9, -3092272);
                }
            }

            // Note that selection can go in both ways, either cursorPos or highlightPos can be the min/max
            int selectionStart = Math.min(getHighlightPos(), getCursorPosition());
            int highlightPos = Math.max(getHighlightPos(), getCursorPosition());
            if (highlightPos != selectionStart) {
                int currentLineStartIndex = 0;
                int lineSelectionStartIndex = selectionStart;
                int linesPassed = 0;
                for (int i = 0; i < highlightPos; i++) {
                    char charAt = getValue().charAt(i);
                    if (charAt == '\n' || i + 1 == highlightPos) {
                        if (i >= selectionStart && linesPassed < scrollOffset + getVisibleRows()) {
                            int offsetX = 0;
                            int unselectedScrolledStartIndex = currentLineStartIndex + getDisplayPos();
                            if (unselectedScrolledStartIndex < selectionStart) {
                                String unselectedLineText = getValue().substring(unselectedScrolledStartIndex, selectionStart);
                                offsetX = font.width(unselectedLineText);
                            }

                            int scrolledStartIndex = lineSelectionStartIndex;
                            if (currentLineStartIndex + getDisplayPos() > selectionStart) {
                                scrolledStartIndex += getDisplayPos();
                            }
                            if (scrolledStartIndex <= i && linesPassed >= scrollOffset) {
                                String selectedLineText = getValue().substring(scrolledStartIndex, charAt == '\n' ? i : i + 1);
                                int selectedLineWidth = font.width(selectedLineText);
                                int selectionX = getX() + offsetX + 1;
                                int selectionY = getY() + (linesPassed - scrollOffset) * 9 + 1;
                                ((EditBoxAccessor) this).callRenderHighlight(guiGraphics, selectionX, selectionY, selectionX + selectedLineWidth + 1, selectionY + 9);
                            }
                            lineSelectionStartIndex = i + 1;
                        }
                        currentLineStartIndex = i + 1;
                        linesPassed++;
                    }
                }
            }

            int debugY = 10;
            for (Map.Entry<String, String> entry : debugRenders.entrySet()) {
                guiGraphics.drawString(font, entry.getKey() + ": " + entry.getValue(), 10, debugY, 0xFFFFFFFF);
                debugY += 10;
            }
        }
    }

    @Override
    public void setValue(String text) {
        int cursorPosition = getCursorPosition();
        super.setValue(text);
        setCursorPosition(cursorPosition);
        renderCache = null;
    }

    @Override
    public int getVisibleRows() {
        return height / font.lineHeight;
    }

    @Override
    public int getRowCount() {
        if (renderCache != null) {
            lastRowCount = renderCache.length;
        }
        return lastRowCount;
    }

    @Override
    public int getCurrentOffset() {
        return scrollOffset;
    }

    @Override
    public void setCurrentOffset(int offset) {
        this.scrollOffset = offset;
    }

    public void writeUnfiltered(String inputText) {
        String resultText = "";
        int lvt_4_1_ = Math.min(getCursorPosition(), getHighlightPos());
        int lvt_5_1_ = Math.max(getCursorPosition(), getHighlightPos());
        int lvt_6_1_ = getMaxLength() - getValue().length() - (lvt_4_1_ - lvt_5_1_);
        if (!getValue().isEmpty()) {
            resultText = resultText + getValue().substring(0, lvt_4_1_);
        }

        int lvt_7_2_;
        if (lvt_6_1_ < inputText.length()) {
            resultText = resultText + inputText.substring(0, lvt_6_1_);
            lvt_7_2_ = lvt_6_1_;
        } else {
            resultText = resultText + inputText;
            lvt_7_2_ = inputText.length();
        }

        if (!getValue().isEmpty() && lvt_5_1_ < getValue().length()) {
            resultText = resultText + getValue().substring(lvt_5_1_);
        }

        setValue(resultText);
        setCursorPosition(lvt_4_1_ + lvt_7_2_);
        setHighlightPos(getCursorPosition());
        ((EditBoxAccessor) this).callOnValueChange(getValue());
    }

    public void addHistoryEntry(String text) {
        history.add(text);
    }

    private int getDisplayPos() {
        return ((EditBoxAccessor) this).getDisplayPos();
    }

    private int getHighlightPos() {
        return ((EditBoxAccessor) this).getHighlightPos();
    }

    private void setDisplayPos(int displayPos) {
        ((EditBoxAccessor) this).setDisplayPos(displayPos);
    }

    private int getMaxLength() {
        return ((EditBoxAccessor) this).getMaxLen();
    }
}
