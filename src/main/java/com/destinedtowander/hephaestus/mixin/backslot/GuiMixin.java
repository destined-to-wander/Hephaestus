package com.destinedtowander.hephaestus.mixin.backslot;

import com.destinedtowander.hephaestus.common.index.ModAttachments;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import javax.annotation.Nullable;

@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow @Nullable protected abstract Player getCameraPlayer();

    @WrapOperation(method = "renderItemHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private void hephaestus$selection(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height, Operation<Void> original) {
        if (this.getCameraPlayer() instanceof Player player && ModAttachments.getHoldingBackslot(player)) return;
        original.call(instance, sprite, x, y, width, height);
    }
}
