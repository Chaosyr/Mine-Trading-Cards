package com.chaosyr.mtc.core.card_pack_selection.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * This class covers the Root Card Pack Selection Screen.
 */
public class CardPackSelectionScreen extends Screen {
    /**
     * The Parent Screen.
     */
    private final Screen parent;

    /**
     * Instantiates the Card Pack Selection Screen.
     * @param parent The Parent Screen.
     */
    public CardPackSelectionScreen(Screen parent) {
        super(Component.literal("My Custom Screen"));
        this.parent = parent;
    }

    /**
     * Initializes the Card Pack Selection Screen.
     */
    @Override
    protected void init() {
        int buttonWidth = 100;
        int buttonHeight = 20;

        this.addRenderableWidget(Button.builder(
                Component.literal("Back"),
                button -> Minecraft.getInstance().gui.setScreen(parent)
        ).bounds(
                (this.width - buttonWidth) / 2,
                (this.height - buttonHeight) / 2,
                buttonWidth,
                buttonHeight
        ).build());
    }
}
