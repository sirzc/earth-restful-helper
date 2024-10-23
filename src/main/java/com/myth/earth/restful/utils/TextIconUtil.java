package com.myth.earth.restful.utils;

import com.intellij.ui.JBColor;
import com.intellij.ui.TextIcon;
import com.intellij.ui.scale.JBUIScale;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 文字图标
 *
 * @author zhouchao
 * @date 2024-06-21 下午2:58
 */
public class TextIconUtil {

    public static final JBColor GRAY_COLOR         = new JBColor(0x808080, 0xA9A9A9);
    public static final JBColor ERROR_COLOR        = new JBColor(0xff0000, 0xff5555);
    public static final JBColor BLUE_COLOR         = new JBColor(0x87ceeb, 0xb0e2ff);
    public static final JBColor WARNING_COLOR      = new JBColor(0xff8000, 0xffa500);
    public static final JBColor NORMAL_COLOR       = new JBColor(0x00b53d, 0x6ba65d);
    public static final JBColor DEFAULT_BACKGROUND = new JBColor(0xebfcf1, 0x313b32);

    public static Icon createTextIcon(@NotNull String text, @NotNull JBColor foreground) {
        TextIcon textIcon = new TextIcon(text, foreground, null, JBUIScale.scale(0));
        textIcon.setFont(UIManager.getDefaults().getFont("TextField.font"));
        return textIcon;
    }

    public static Icon createTextIcon(@NotNull String text, @NotNull JBColor foreground, JBColor background) {
        return createTextIcon(text, foreground, background, 5);
    }

    public static Icon createTextIcon(@NotNull String text, @NotNull JBColor foreground, JBColor background, int margin) {
        TextIcon textIcon = new TextIcon(text, foreground, background, JBUIScale.scale(margin));
        // textIcon.setFont(JBFont.label().deriveFont(Font.BOLD));
        textIcon.setFont(UIManager.getDefaults().getFont("TextField.font"));
        textIcon.setRound(JBUIScale.scale(3));
        return textIcon;
    }
}
