package com.destinedtowander.hephaestus.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Unique
    boolean hephaestus$freeEnchantmentTransfer = false;

    @ModifyVariable(index = 17, method = "createResult", at = @At(value = "STORE"))
    private int detectEnchantmentMerging(int original, @Local(index = 16)int i2, @Local(ordinal = 1)ItemStack left){
        hephaestus$freeEnchantmentTransfer = left.has(DataComponents.STORED_ENCHANTMENTS) && original != i2;
        return original;
    }

    @ModifyExpressionValue(method = "createResult", at = @At(value = "INVOKE", target = "Ljava/lang/Math;max(II)I", ordinal = 1))
    private int cheaperBookMerging(int original){
        return hephaestus$freeEnchantmentTransfer ? 0 : original;
    }

    @ModifyVariable(ordinal = 0, method = "createResult", at = @At(value = "LOAD",ordinal = 4))
    private int allowingFreeBookMerging(int original, @Local(ordinal = 1)ItemStack left){
        return hephaestus$freeEnchantmentTransfer ? 1 : original;
    }

    @ModifyExpressionValue(method = "mayPickup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;get()I", ordinal = 1))
    private int allowExtractingMergedBook(int original) {
        return hephaestus$freeEnchantmentTransfer ? 1 : original;
    }

    @ModifyReturnValue(method = "calculateIncreasedRepairCost", at = @At("RETURN"))
    private static int removeExponentialRepairCost(int original, int initialRepairCost) {
        return initialRepairCost;
    }
}
