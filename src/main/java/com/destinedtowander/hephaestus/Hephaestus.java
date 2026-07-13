package com.destinedtowander.hephaestus;

import com.destinedtowander.hephaestus.common.index.ModAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static com.destinedtowander.hephaestus.common.index.ModAttachments.ATTACHMENT_TYPES;
import static com.destinedtowander.hephaestus.common.index.ModDataComponents.DATA_COMPONENTS;
import static com.destinedtowander.hephaestus.common.index.ModItems.ITEMS;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Hephaestus.MODID)
public class Hephaestus {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "hephaestus";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Hephaestus(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        // Register the Deferred Register to the mod event bus so stuff get registered
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Hephaestus.MODID, path);
    }
}
