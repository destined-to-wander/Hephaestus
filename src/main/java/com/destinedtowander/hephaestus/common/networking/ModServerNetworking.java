package com.destinedtowander.hephaestus.common.networking;

import com.destinedtowander.hephaestus.Hephaestus;
import com.destinedtowander.hephaestus.common.networking.packets.SelectBackslotPacket;
import com.destinedtowander.hephaestus.common.networking.packets.SwapBackslotPacket;
import com.destinedtowander.hephaestus.common.networking.packets.SwapBackslotInventoryPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Hephaestus.MODID)
public class ModServerNetworking {
    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
            SwapBackslotPacket.TYPE,
            SwapBackslotPacket.STREAM_CODEC,
            SwapBackslotPacket::swapBackslot
        );
        registrar.playToServer(
            SelectBackslotPacket.TYPE,
            SelectBackslotPacket.STREAM_CODEC,
            SelectBackslotPacket::selectBackslot
        );
        registrar.playToServer(
            SwapBackslotInventoryPacket.TYPE,
            SwapBackslotInventoryPacket.STREAM_CODEC,
            SwapBackslotInventoryPacket::swapBackslot
        );
    }
}
