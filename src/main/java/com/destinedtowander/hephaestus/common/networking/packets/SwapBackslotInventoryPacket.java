package com.destinedtowander.hephaestus.common.networking.packets;

import com.destinedtowander.hephaestus.common.index.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.destinedtowander.hephaestus.Hephaestus.id;

public record SwapBackslotInventoryPacket(int slotid) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SwapBackslotInventoryPacket> TYPE = new CustomPacketPayload.Type<>(id("swap_inventory_backslot"));

    public static final StreamCodec<ByteBuf, SwapBackslotInventoryPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SwapBackslotInventoryPacket::slotid,
        SwapBackslotInventoryPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void swapBackslot(final SwapBackslotInventoryPacket data, final IPayloadContext context) {
        Player player = context.player();

        if (!player.isSpectator() && player.containerMenu.isValidSlotIndex(data.slotid())) {
            Slot slot = player.containerMenu.getSlot(data.slotid());
            ItemStack itemStack = ModAttachments.getBackslotItem(player).copy();
            if (!slot.mayPlace(itemStack)) return;
            ModAttachments.setBackslotItem(player, slot.getItem());
            slot.set(itemStack);
        }
    }
}
