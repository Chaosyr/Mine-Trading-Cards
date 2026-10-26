package com.chaosyr.mtc.core.card_pack_selection.buttons;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * This is a helper class that makes our Sprite Icon Button's (namely the one used in the Title Screen.
 */
public class SpriteIconButton extends AbstractWidget {
    /**
     * The Main Texture of the Button.
     */
    private final Identifier texture;
    /**
     * The Highlight Texture of the Button.
     */
    private final Identifier highlightTexture;
    /**
     * The Action to do when the Button is Pressed.
     */
    private final Button.OnPress onPress;

    /**
     * This function updates the Narration Element Output for the Button.
     * @param output What we want to Output for the Button's narration.
     */
    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    /**
     * Constructs a Sprite Icon Button for Usage in Menu's.
     * @param x The X-Coordinate of the Button.
     * @param y The Y-Coordinate of the Button.
     * @param texture The Main Texture of the Button.
     * @param highlightTexture The Highlight Texture of the Button.
     * @param hoverText The Hover Text of the Button.
     * @param onPress The Action to do when the Button is Pressed.
     */
    public SpriteIconButton(int x, int y, Identifier texture, Identifier highlightTexture, Component hoverText, Button.OnPress onPress) {
        super(x, y, 20, 20, Component.empty());
        this.texture = texture;
        this.highlightTexture = highlightTexture;
        this.onPress = onPress;
        this.setTooltip(Tooltip.create(hoverText));
    }

    /**
     * The Extracted Widget Render State.
     * @param graphics The Graphics of the Button.
     * @param mouseX The Mouses X Position.
     * @param mouseY The Mouses Y Position.
     * @param a Does Something I'm not sure as to what, Base Game Code.
     */
    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Identifier renderTexture = this.isHovered() && this.highlightTexture != null
                ? this.highlightTexture
                : this.texture;

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                renderTexture,
                this.getX(),
                this.getY(),
                0.0F,
                0.0F,
                20,
                20,
                20,
                20,
                20,
                20,
                net.minecraft.util.ARGB.color(this.alpha, -1)
        );
    }


    /**
     * The Action we take when the Button is Pressed.
     * @param event The {@link net.minecraft.client.input.MouseButtonEvent} to pass in when the Action is triggered.
     * @param doubleClick A boolean for whether the Button was Double Clicked or Not.
     */
    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        this.onPress.onPress(null);
    }
}