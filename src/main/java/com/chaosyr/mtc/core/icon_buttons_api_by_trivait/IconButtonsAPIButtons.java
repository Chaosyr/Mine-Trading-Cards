package com.chaosyr.mtc.core.icon_buttons_api_by_trivait;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * This code is credit to the Mod Creator Trivait and their Mod IconButtons API
 */
public class IconButtonsAPIButtons {
    private static Map<AbstractWidget, BooleanSupplier> titleScreenWidgets = new HashMap<>();
    private static Map<AbstractWidget, BooleanSupplier> pauseScreenWidgets = new HashMap<>();

    public static void addTitleScreenButton(AbstractWidget widget) {
        titleScreenWidgets.put(widget, () -> {
            return true;
        });
    }
    public static void addPauseScreenButton(AbstractWidget widget) {
        pauseScreenWidgets.put(widget, () -> {
            return true;
        });
    }

    public static void addTitleScreenButton(AbstractWidget widget, BooleanSupplier willShow) {
        titleScreenWidgets.put(widget, willShow);
    }
    public static void addPauseScreenButton(AbstractWidget widget, BooleanSupplier willShow) {
        pauseScreenWidgets.put(widget, willShow);
    }

    public static int getButtonWidthAndHeight() {
        return 20;
    }

    public static List<AbstractWidget> getTitleScreenWidgets() {
        List<AbstractWidget> widgets = new ArrayList<>();

        titleScreenWidgets.forEach((widget, willShow) -> {
            if (willShow.getAsBoolean()) widgets.add(widget);
        });

        return widgets;
    }
    public static List<AbstractWidget> getPauseScreenWidgets() {
        List<AbstractWidget> widgets = new ArrayList<>();

        pauseScreenWidgets.forEach((widget, willShow) -> {
            if (willShow.getAsBoolean()) widgets.add(widget);
        });

        return widgets;
    }
}