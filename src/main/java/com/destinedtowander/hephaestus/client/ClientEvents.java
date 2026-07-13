package com.destinedtowander.hephaestus.client;

import com.destinedtowander.hephaestus.Hephaestus;
import com.destinedtowander.hephaestus.common.index.ModAttachments;
import com.destinedtowander.hephaestus.common.networking.packets.SwapBackslotPacket;
import com.destinedtowander.hephaestus.common.networking.packets.SwapBackslotInventoryPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

import static com.destinedtowander.hephaestus.Hephaestus.id;

@EventBusSubscriber(modid = Hephaestus.MODID, value = Dist.CLIENT)
public class ClientEvents {
    private static final ResourceLocation BACKSLOT_SELECTION_SPRITE = id("textures/hud/backslot_selection.png");

    public static final Lazy<KeyMapping> keyBackslot = Lazy.of(() ->
        new KeyMapping(
            "key.hephaestus.backslot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "category.hephaestus"
        )
    );
    public static final Lazy<KeyMapping> keySwapBackslot = Lazy.of(() ->
        new KeyMapping(
            "key.hephaestus.swap_backslot",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "category.hephaestus"
        )
    );

    @SubscribeEvent
    public static void swapBackslot(InputEvent.Key event) {
        Minecraft instance = Minecraft.getInstance();
        if (!(instance.player instanceof LocalPlayer player)) return;

        if (instance.screen instanceof AbstractContainerScreen<?> screen) {
            if (screen.getSlotUnderMouse() != null && player.containerMenu.getCarried().isEmpty() && keySwapBackslot.get().matches(event.getKey(), event.getScanCode()) && event.getAction() == InputConstants.PRESS) {
                PacketDistributor.sendToServer(new SwapBackslotInventoryPacket(screen.getSlotUnderMouse().index));
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft instance = Minecraft.getInstance();
        if (!(instance.player instanceof LocalPlayer player)) return;

        for (KeyMapping mapping : instance.options.keyHotbarSlots) {
            if (mapping.isDown()) ModAttachments.setHoldingBackslot(player, false);
        }
        if (ModAttachments.getHoldingBackslot(player) && instance.options.keySwapOffhand.consumeClick()) PacketDistributor.sendToServer(new SwapBackslotPacket(true));
        while (keyBackslot.get().consumeClick()) if (!ModAttachments.getBackslotItem(player).isEmpty()) ModAttachments.setHoldingBackslot(player, !ModAttachments.getHoldingBackslot(player));
        while (keySwapBackslot.get().consumeClick()) if (!ModAttachments.getHoldingBackslot(player)) PacketDistributor.sendToServer(new SwapBackslotPacket());
    }

    @SubscribeEvent
    public static void clearBackslotOnPickBlock(InputEvent.InteractionKeyMappingTriggered event) {
        if (!(Minecraft.getInstance().player instanceof LocalPlayer player)) return;
        if (event.isPickBlock()) ModAttachments.setHoldingBackslot(player, false);
    }

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(keyBackslot.get());
        event.register(keySwapBackslot.get());
    }

    @SubscribeEvent
    public static void addBackslotToHud(RenderGuiEvent.Post event) {
        Minecraft minecraftInstance = Minecraft.getInstance();
        if (!(minecraftInstance.player instanceof LocalPlayer player)) return;
        ItemStack stack = ModAttachments.getBackslotItem(player);
        if (stack.isEmpty()) return;
        GuiGraphics graphics = event.getGuiGraphics();
        Window window = minecraftInstance.getWindow();

        int screenWidth = window.getGuiScaledWidth() / 2;
        if (ModAttachments.getHoldingBackslot(player)) {
            graphics.blit(BACKSLOT_SELECTION_SPRITE, screenWidth - 12, window.getGuiScaledHeight() - 23 - 70,0,0, 24,24,24, 24);
            RenderSystem.enableBlend();
            graphics.blitSprite(Gui.HOTBAR_OFFHAND_LEFT_SPRITE, 29,24, 3, 4, screenWidth - 12 + 4, window.getGuiScaledHeight() - 23 - 70 + 4, 16, 16);
            RenderSystem.defaultBlendFunc();

            int o = screenWidth - 90 + 4 * 20 + 2;
            int p = window.getGuiScaledHeight() - 19 - 70;
            graphics.renderItem(stack, o, p, 0);
            graphics.renderItemDecorations(minecraftInstance.font, stack, o, p);
            RenderSystem.disableBlend();
        } else {
            HumanoidArm arm = player.getMainArm().getOpposite();
            RenderSystem.enableBlend();
            if (arm == HumanoidArm.RIGHT) graphics.blitSprite(Gui.HOTBAR_OFFHAND_LEFT_SPRITE, screenWidth - 91 - 29, window.getGuiScaledHeight() - 23, 29, 24);
            else graphics.blitSprite(Gui.HOTBAR_OFFHAND_RIGHT_SPRITE, screenWidth + 91, window.getGuiScaledHeight() - 23, 29, 24);
            RenderSystem.defaultBlendFunc();

            int n = window.getGuiScaledHeight() - 16 - 3;
            int xOffset = screenWidth + (arm == HumanoidArm.RIGHT ? - 91 - 26 : 91 + 10);
            graphics.renderItem(stack, xOffset, n, 0);
            graphics.renderItemDecorations(minecraftInstance.font, stack, xOffset, n);
            RenderSystem.disableBlend();
        }
    }
}
