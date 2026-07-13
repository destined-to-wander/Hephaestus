package com.destinedtowander.hephaestus.common.networking.packets;

import com.destinedtowander.hephaestus.common.index.ModAttachments;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import static com.destinedtowander.hephaestus.Hephaestus.id;

public record SelectBackslotPacket(boolean hold) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SelectBackslotPacket> TYPE = new CustomPacketPayload.Type<>(id("select_backslot"));

    public static final StreamCodec<ByteBuf, SelectBackslotPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        SelectBackslotPacket::hold,
        SelectBackslotPacket::new
    );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void selectBackslot(final SelectBackslotPacket data, final IPayloadContext context) {
        Player player = context.player();
        ModAttachments.setHoldingBackslot(player, data.hold());
    }
}
