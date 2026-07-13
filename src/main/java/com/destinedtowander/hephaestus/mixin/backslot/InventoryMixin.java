package com.destinedtowander.hephaestus.mixin.backslot;

import com.destinedtowander.hephaestus.common.index.ModAttachments;
import com.destinedtowander.hephaestus.common.networking.packets.SelectBackslotPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Inject(method = "getSelected", at = @At("HEAD"), cancellable = true)
    private void hephaestus$holdBackslot(CallbackInfoReturnable<ItemStack> cir) {
        if (!ModAttachments.getHoldingBackslot(player)) return;
        if (ModAttachments.getBackslotItem(player).isEmpty()) ModAttachments.setHoldingBackslot(player, false);
        else cir.setReturnValue(ModAttachments.getBackslotItem(player));
    }

    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    private void hephaestus$slotBreaking(BlockState block, CallbackInfoReturnable<Float> cir) {
        if (!ModAttachments.getHoldingBackslot(player)) return;
        if (ModAttachments.getBackslotItem(player).isEmpty()) ModAttachments.setHoldingBackslot(player, false);
        else cir.setReturnValue(ModAttachments.getBackslotItem(player).getDestroySpeed(block));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void hephaestus$tickBackslot(CallbackInfo ci) {
        if (!ModAttachments.getBackslotItem(player).isEmpty()) ModAttachments.getBackslotItem(player).inventoryTick(player.level(), player, 0, ModAttachments.getHoldingBackslot(player));
    }

    @Inject(method = "setPickedItem", at = @At("HEAD"))
    private void hephaestus$nonPick(CallbackInfo ci) {
        ModAttachments.setHoldingBackslot(player, false);
    }

    @Inject(method = "pickSlot", at = @At("HEAD"))
    private void hephaestus$nonSwap(int slot, CallbackInfo ci) {
        ModAttachments.setHoldingBackslot(player, false);
    }

    @Inject(method = "swapPaint", at = @At("HEAD"))
    private void hephaestus$nonScroll(double scrollAmount, CallbackInfo ci) {
        ModAttachments.setHoldingBackslot(player, false);
    }
}
