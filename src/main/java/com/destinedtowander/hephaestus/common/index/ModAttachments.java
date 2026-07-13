package com.destinedtowander.hephaestus.common.index;

import com.destinedtowander.hephaestus.Hephaestus;
import com.destinedtowander.hephaestus.common.networking.packets.SelectBackslotPacket;
import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    // Create the DeferredRegister for attachment types
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Hephaestus.MODID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackHandler> BACKSLOT_CODEC =
        StreamCodec.of(
            (buf, handler) -> {
                ItemStack stack = handler.getStackInSlot(0);
                boolean encode = !stack.isEmpty();
                buf.writeBoolean(encode);
                if (encode) ItemStack.STREAM_CODEC.encode(buf, stack);
            },
            (buf) -> {
                ItemStackHandler handler = new ItemStackHandler(1);
                ItemStack stack;
                if (buf.readBoolean()) stack = ItemStack.STREAM_CODEC.decode(buf);
                else stack = ItemStack.EMPTY;
                handler.setStackInSlot(0, stack);
                return handler;
            }
        );

    private static final Supplier<AttachmentType<ItemStackHandler>> BACKSLOT = ATTACHMENT_TYPES.register(
        "backslot", () -> AttachmentType.serializable(() -> new ItemStackHandler(1))
            .copyOnDeath()
            .sync(BACKSLOT_CODEC)
            .build()
    );

    private static final Supplier<AttachmentType<Boolean>> HOLDING_BACKSLOT = ATTACHMENT_TYPES.register(
        "holding_backslot", () -> AttachmentType.builder(()->false).serialize(Codec.BOOL)
            .sync(ByteBufCodecs.BOOL)
            .build()
    );

    public static boolean hasBackslot(Player player) {
        return player.hasData(BACKSLOT);
    }

    public static ItemStackHandler getBackslotHandler(Player player) {
        player.syncData(BACKSLOT);
        return player.getData(BACKSLOT);
    }

    public static ItemStack getBackslotItem(Player player) {
        player.syncData(BACKSLOT);
        return player.getData(BACKSLOT).getStackInSlot(0);
    }

    public static void setBackslotItem(Player player, ItemStack itemStack) {
        if (itemStack.isEmpty()) player.setData(ModAttachments.HOLDING_BACKSLOT, false);
        player.getData(BACKSLOT).setStackInSlot(0, itemStack);
        player.syncData(BACKSLOT);
    }

    public static void setHoldingBackslot(Player player, boolean holding) {
        if (player.level().isClientSide()) {
            PacketDistributor.sendToServer(new SelectBackslotPacket(holding));
            return;
        }
        player.setData(HOLDING_BACKSLOT, holding);
        player.syncData(HOLDING_BACKSLOT);
    }

    public static boolean getHoldingBackslot(Player player) {
        if (!player.hasData(HOLDING_BACKSLOT)) return false;
        player.syncData(HOLDING_BACKSLOT);
        return player.getData(HOLDING_BACKSLOT);
    }
}
