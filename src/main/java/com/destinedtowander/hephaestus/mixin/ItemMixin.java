package com.destinedtowander.hephaestus.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin {

    @WrapMethod(method = "isValidRepairItem")
    private boolean addRepairMaterials(ItemStack stack, ItemStack repairCandidate, Operation<Boolean> original) {

        return ((this.equals(Items.BOW) || this.equals(Items.CROSSBOW) || this.equals(Items.FISHING_ROD)) && repairCandidate.is(Items.STRING)) ||
            (this.equals(Items.CARROT_ON_A_STICK) && repairCandidate.is(Items.CARROT)) ||
            (this.equals(Items.WARPED_FUNGUS_ON_A_STICK) && repairCandidate.is(Items.WARPED_FUNGUS)) ||
            (this.equals(Items.SHIELD) && repairCandidate.is(ItemTags.PLANKS)) ||
            (this.equals(Items.SHEARS) && repairCandidate.is(Items.IRON_INGOT)) ||
            (this.equals(Items.FLINT_AND_STEEL) && repairCandidate.is(Items.FLINT)) ||
            original.call(stack,repairCandidate);
    }
}
