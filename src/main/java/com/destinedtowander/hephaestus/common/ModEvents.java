package com.destinedtowander.hephaestus.common;

import com.destinedtowander.hephaestus.Hephaestus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;

@EventBusSubscriber(modid = Hephaestus.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void swapBackslot(EnchantmentLevelSetEvent event) {
    }
}
