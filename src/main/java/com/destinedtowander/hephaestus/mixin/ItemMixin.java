package com.destinedtowander.hephaestus.mixin;

import com.destinedtowander.hephaestus.common.index.ModItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin {

    @WrapMethod(method = "isValidRepairItem")
    private boolean addRepairMaterials(ItemStack stack, ItemStack repairCandidate, Operation<Boolean> original) {
        if (((Item)(Object)this) instanceof TieredItem tieredItem && tieredItem.getTier().equals(Tiers.NETHERITE)) {
            if (repairCandidate.is(Items.DIAMOND)) return true;
            if (repairCandidate.is(Items.NETHERITE_INGOT)) return false;
        }

        return ((this.equals(Items.BOW) || stack.is(Items.CROSSBOW) || this.equals(Items.FISHING_ROD)) && repairCandidate.is(Items.STRING)) ||
            (this.equals(Items.CARROT_ON_A_STICK) && repairCandidate.is(Items.CARROT)) ||
            (this.equals(Items.WARPED_FUNGUS_ON_A_STICK) && repairCandidate.is(Items.WARPED_FUNGUS)) ||
            (this.equals(Items.SHIELD) && repairCandidate.is(ItemTags.PLANKS)) ||
            (this.equals(Items.SHEARS) && repairCandidate.is(Items.IRON_INGOT)) ||
            (this.equals(Items.FLINT_AND_STEEL) && repairCandidate.is(Items.FLINT)) ||
            (this.equals(Items.TRIDENT) && repairCandidate.is(Items.PRISMARINE)) ||
            repairCandidate.is(ModItems.REPAIR_KIT) ||
            original.call(stack,repairCandidate);
    }
}
