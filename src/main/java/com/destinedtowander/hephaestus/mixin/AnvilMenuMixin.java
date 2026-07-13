package com.destinedtowander.hephaestus.mixin;

import com.destinedtowander.hephaestus.common.index.ModDataComponents;
import com.destinedtowander.hephaestus.common.index.ModEffectComponents;
import com.destinedtowander.hephaestus.common.index.ModItems;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.minecraft.world.item.enchantment.Enchantment.applyEffects;
import static net.minecraft.world.item.enchantment.Enchantment.itemContext;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu{
    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Unique
    boolean hephaestus$freeEnchantmentTransfer = false;

    @Shadow
    public int repairItemCountCost;

    @ModifyVariable(index = 17, method = "createResult", at = @At(value = "STORE", ordinal = 0))
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

    // Repair Kit
    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I", ordinal = 1))
    private void repairKitCostScaling(CallbackInfo ci, @Local(ordinal = 0) LocalIntRef i, @Local(ordinal = 3)int j3,@Local(ordinal = 0)ItemStack left, @Local(ordinal = 2)ItemStack right, @Local(ordinal = 1)ItemStack output) {
        if (right.is(ModItems.REPAIR_KIT)) i.set(i.get() + left.getOrDefault(ModDataComponents.REPAIR_KIT_USAGES,0) + j3);
    }

    @Inject(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", ordinal = 1))
    private void repairKitCostSet(CallbackInfo ci, @Local(ordinal = 0)ItemStack left, @Local(ordinal = 2)ItemStack right, @Local(ordinal = 1)ItemStack output) {
        if (right.is(ModItems.REPAIR_KIT)) output.set(ModDataComponents.REPAIR_KIT_USAGES, left.getOrDefault(ModDataComponents.REPAIR_KIT_USAGES, 0) + repairItemCountCost);
        else if (output.getItem().isValidRepairItem(left, right)) output.set(ModDataComponents.REPAIR_KIT_USAGES, 0);
    }

    // No repair cost retain
    @ModifyReturnValue(method = "calculateIncreasedRepairCost", at = @At("RETURN"))
    private static int noScalingOperationCost(int original) {
        return 0;
    }

    @WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int customRepairAmount(int damageValue, int repairAmount, Operation<Integer> original, @Local(ordinal = 1)ItemStack itemstack1, @Local(ordinal = 2)ItemStack itemstack2) {
        MutableFloat repairMultiplier = new MutableFloat(1);

        EnchantmentHelper.runIterationOnItem(itemstack1, (holder, enchantmentLevel) -> {
            for (EnchantmentValueEffect effect : holder.value().getEffects(ModEffectComponents.REPAIR_EFFICIENCY.get())) {
                repairMultiplier.setValue(effect.process(enchantmentLevel, player.level().random, repairMultiplier.floatValue()));
            }
        });

        if (!itemstack2.is(ModItems.REPAIR_KIT)) return original.call(damageValue, repairAmount);
        if (damageValue < itemstack1.getMaxDamage() / 2) return 0;
        int repairCap = damageValue - itemstack1.getMaxDamage() / 2;
        return Math.min(repairCap, Math.round(repairAmount * repairMultiplier.floatValue() / 2.0F));
    }
}
