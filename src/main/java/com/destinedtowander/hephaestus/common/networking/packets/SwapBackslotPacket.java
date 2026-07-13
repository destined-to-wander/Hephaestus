package com.destinedtowander.hephaestus.common.networking.packets;

import com.destinedtowander.hephaestus.common.index.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.destinedtowander.hephaestus.Hephaestus.id;

public record SwapBackslotPacket(boolean offhand) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SwapBackslotPacket> TYPE = new CustomPacketPayload.Type<>(id("swap_backslot"));

    public SwapBackslotPacket() {
        this(false);
    }

    public static final StreamCodec<ByteBuf, SwapBackslotPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        SwapBackslotPacket::offhand,
        SwapBackslotPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void swapBackslot(final SwapBackslotPacket data, final IPayloadContext context) {
        Player player = context.player();
        if (!player.isSpectator()) {
            InteractionHand hand = data.offhand() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            boolean toggled = ModAttachments.getHoldingBackslot(player);
            ModAttachments.setHoldingBackslot(player, false);
            ItemStack itemStack = ModAttachments.getBackslotItem(player).copy();
            ModAttachments.setBackslotItem(player, player.getItemInHand(hand));
            player.setItemInHand(hand, itemStack);
            if (data.offhand()) player.stopUsingItem();
            ModAttachments.setHoldingBackslot(player, toggled);
        }
    }
}
