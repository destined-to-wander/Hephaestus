package com.destinedtowander.hephaestus.mixin.backslot;

import com.destinedtowander.hephaestus.Hephaestus;
import com.destinedtowander.hephaestus.common.index.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuMixin extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    public InventoryMenuMixin(MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addBackslot(Inventory playerInventory, boolean active, Player owner, CallbackInfo ci) {

        Hephaestus.LOGGER.info("Number of slots {}", this.slots.size());

        Slot backslot = this.addSlot(new Slot(
            new Container() {
                @Override
                public void clearContent() {
                    if (ModAttachments.hasBackslot(owner)) ModAttachments.setBackslotItem(owner, ItemStack.EMPTY);
                }

                @Override
                public int getContainerSize() {
                    return 1;
                }

                @Override
                public boolean isEmpty() {
                    return !(ModAttachments.hasBackslot(owner)) || ModAttachments.getBackslotItem(owner).isEmpty();
                }

                @Override
                public @NotNull ItemStack getItem(int slot) {
                    return ModAttachments.hasBackslot(owner) ? ModAttachments.getBackslotItem(owner) : ItemStack.EMPTY;
                }

                @Override
                public @NotNull ItemStack removeItem(int slot, int amount) {
                    if (!ModAttachments.hasBackslot(owner)) return ItemStack.EMPTY;
                    ItemStack stack = ModAttachments.getBackslotItem(owner).copy();
                    ItemStack returnStack = stack.split(amount);
                    ModAttachments.setBackslotItem(owner, stack);
                    return returnStack;
                }

                @Override
                public @NotNull ItemStack removeItemNoUpdate(int slot) {
                    if (!ModAttachments.hasBackslot(owner)) return ItemStack.EMPTY;
                    ItemStack stack = ModAttachments.getBackslotItem(owner).copy();
                    ModAttachments.setBackslotItem(owner, ItemStack.EMPTY);
                    return stack;
                }

                @Override
                public void setItem(int slot, ItemStack stack) {
                    if (ModAttachments.hasBackslot(owner)) ModAttachments.setBackslotItem(owner, stack);
                }

                @Override
                public void setChanged() {

                }

                @Override
                public boolean stillValid(Player player) {
                    return true;
                }
            },
            0,
            77,
            44
        ));

        Hephaestus.LOGGER.info("Backslot Index {}", this.slots.indexOf(backslot));
        Hephaestus.LOGGER.info("Number of slots {}", this.slots.size());

    }
}
