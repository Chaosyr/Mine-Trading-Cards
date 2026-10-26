package com.chaosyr.mtc.core.card_pack_selection.buttons;

import com.chaosyr.mtc.core.card_pack_selection.screens.CardPackSelectionScreen;
import com.chaosyr.mtc.core.icon_buttons_api_by_trivait.IconButtonsAPIButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * This is a class for registering the Card Pack Selection Button and its Screens.
 */
public class CardPackSelectionButton {
    /**
     * A function that registers our Buttons.
     */
    public static void registerButtons() {
        SpriteIconButton myButton = new SpriteIconButton(
                0,
                0,
                Identifier.fromNamespaceAndPath(
                        "mine_trading_cards",
                        "textures/gui/mtc-gui-icon.png"
                ),
                Identifier.fromNamespaceAndPath(
                        "mine_trading_cards",
                        "textures/gui/mtc-gui-icon-highlight.png"
                ),
                Component.literal("Mine Trading Cards Menu"),
                button -> {
                    Minecraft.getInstance().setScreenAndShow(
                            new CardPackSelectionScreen(Minecraft.getInstance().gui.screen())
                    );
                }
        );

        IconButtonsAPIButtons.addTitleScreenButton(myButton, () -> Minecraft.getInstance().gui.screen() instanceof TitleScreen);
    }
}
