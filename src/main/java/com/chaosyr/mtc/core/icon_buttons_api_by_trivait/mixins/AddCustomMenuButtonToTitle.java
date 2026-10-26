package com.chaosyr.mtc.core.icon_buttons_api_by_trivait.mixins;

import com.chaosyr.mtc.core.icon_buttons_api_by_trivait.IconButtonsAPIButtons;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * This code is credit to the Mod Creator Trivait and their Mod IconButtons API
 */
@Mixin(TitleScreen.class)
public abstract class AddCustomMenuButtonToTitle extends Screen {
    protected AddCustomMenuButtonToTitle(Component title) {
        super(title);
    }

    @Shadow
    protected abstract int getHorizontalPosition(int currentButton, int numberOfButtons, int buttonWidth);

    @Definition(id = "numberOfButtons", local = @Local(type = int.class, name = "numberOfButtons"))
    @Expression("numberOfButtons = ?")
    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private void adjustAmountOfIconButtons(CallbackInfo ci, @Local(name = "numberOfButtons") LocalIntRef numberOfButtons, @Share("addIconWidgets") LocalBooleanRef addIconWidgets) {
        List<AbstractWidget> widgets = IconButtonsAPIButtons.getTitleScreenWidgets();

        if (widgets.isEmpty()) return;

        numberOfButtons.set(numberOfButtons.get() + widgets.size());
        addIconWidgets.set(true);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;getHorizontalPosition(III)I"))
    private int replaceInlinedConstant(TitleScreen instance, int currentButton, int numberOfButtons, int buttonWidth, Operation<Integer> original, @Local(name = "numberOfButtons") int actualNumberOfButtons) {
        return original.call(instance, currentButton, actualNumberOfButtons, buttonWidth);
    }

    @Definition(id = "width", field = "Lnet/minecraft/client/gui/screens/TitleScreen;width:I")
    @Expression("this.width / 2 - 100")
    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private void addMinewseeperIconWidget(CallbackInfo ci, @Local(name = "currentButton") LocalIntRef currentButton, @Local(name = "topPos") int topPos, @Local(name = "numberOfButtons") int numberOfButtons, @Share("addIconWidgets") LocalBooleanRef addIconWidgets) {
        List<AbstractWidget> widgets = IconButtonsAPIButtons.getTitleScreenWidgets();

        if (!addIconWidgets.get() || widgets.isEmpty()) return;
        Screen screen = (TitleScreen) (Object) this;

        for (AbstractWidget widget : widgets) {
            currentButton.set(currentButton.get()+1);
            widget.setPosition(this.getHorizontalPosition(currentButton.get(), numberOfButtons, widget.getWidth()), topPos);
            ((ScreenAccessor) this).invokeAddRenderableWidget(widget);
        }
    }
}