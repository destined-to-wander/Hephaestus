package com.destinedtowander.hephaestus.common.index;

import com.destinedtowander.hephaestus.Hephaestus;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

@EventBusSubscriber(modid = Hephaestus.MODID)
public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Hephaestus.MODID);

    // Creates a new food item with the id "hestia:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> REPAIR_KIT;

    static {
        REPAIR_KIT = ITEMS.registerItem("repair_kit", (p) -> new Item(p
            .stacksTo(16)
            .component(DataComponents.LORE, new ItemLore(List.of(
                Component.translatable("item.hephaestus.repair_kit.desc").withStyle(ChatFormatting.GRAY)
            )))
        ));
    }

    public static void register() {

    }


    @SubscribeEvent // on the mod event bus
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        // Is this the tab we want to add to?
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(REPAIR_KIT.get());
        }
    }
}