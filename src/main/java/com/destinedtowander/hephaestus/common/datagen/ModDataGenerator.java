package com.destinedtowander.hephaestus.common.datagen;

import com.destinedtowander.hephaestus.Hephaestus;
import com.destinedtowander.hephaestus.common.index.ModEffectComponents;
import com.destinedtowander.hephaestus.common.index.ModItems;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.destinedtowander.hephaestus.Hephaestus.MODID;

@EventBusSubscriber(modid = MODID)
public class ModDataGenerator {
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
        .add(Registries.ENCHANTMENT, ModEnchantments::bootstrap);

    @SubscribeEvent // on the mod event bus
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // other providers here
        generator.addProvider(
            event.includeClient(),
            new ModItemModelProvider(packOutput, existingFileHelper)
        );

        generator.addProvider(
            event.includeServer(),
            new DatapackBuiltinEntriesProvider(
                packOutput,
                event.getLookupProvider(),
                BUILDER,
                Set.of("minecraft")
            )
        );
    }

    public static class ModEnchantments {
        public static void bootstrap(BootstrapContext<Enchantment> context) {
            HolderGetter<Item> items = context.lookup(Registries.ITEM);
            HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);

            context.register(
                Enchantments.MENDING,
                Enchantment.enchantment(
                    Enchantment.definition(
                        items.getOrThrow(ItemTags.DURABILITY_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(5, 8),
                        Enchantment.dynamicCost(55, 8),
                        2,
                        EquipmentSlotGroup.ANY
                    )
                ).withEffect(ModEffectComponents.REPAIR_EFFICIENCY.get(),
                    new AddValue(LevelBasedValue.perLevel(1))
                ).exclusiveWith(enchantments.getOrThrow(ModEnchantmentTags.ITEM_EXCLUSIVE))
                    .build(Enchantments.MENDING.location())
            );
        }
    }

    public static class ModEnchantmentTags extends EnchantmentTagsProvider {
        public static final TagKey<Enchantment> ITEM_EXCLUSIVE = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.withDefaultNamespace("exclusive_set/item"));

        public ModEnchantmentTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, MODID, null);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider provider) {
            tag(ITEM_EXCLUSIVE)
                .add(Enchantments.MENDING,Enchantments.UNBREAKING);
            tag(EnchantmentTags.BOW_EXCLUSIVE)
                .remove(Enchantments.MENDING);
        }
    }

    public static class ModItemModelProvider extends ItemModelProvider {
        public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, MODID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            basicItem(ModItems.REPAIR_KIT.getId());
        }
    }
}
