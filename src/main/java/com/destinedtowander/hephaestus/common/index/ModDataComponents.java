package com.destinedtowander.hephaestus.common.index;

import com.destinedtowander.hephaestus.Hephaestus;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.EncoderCache;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

import static com.destinedtowander.hephaestus.Hephaestus.id;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Hephaestus.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REPAIR_KIT_USAGES =
        DATA_COMPONENTS.registerComponentType(
            "repair_kit_usages",
            builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT)
        );
}
