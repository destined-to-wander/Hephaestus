package com.destinedtowander.hephaestus.common.index;

import com.destinedtowander.hephaestus.Hephaestus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModEffectComponents {
    public static final DeferredRegister<DataComponentType<?>> ENCHANTMENT_COMPONENT_TYPES = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Hephaestus.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> REPAIR_EFFICIENCY = ENCHANTMENT_COMPONENT_TYPES.register(
        "repair_efficiency",
        () -> DataComponentType.<List<ConditionalEffect<EnchantmentValueEffect>>>builder()
            .persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf())
            .build()
    );
}
