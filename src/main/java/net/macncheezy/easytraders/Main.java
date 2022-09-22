package net.macncheezy.easytraders;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.macncheezy.easytraders.crafting.StampRecipeSerializer;
import net.macncheezy.easytraders.items.DyedTradeStamp;
import net.macncheezy.easytraders.items.MagicTradeStamp;
import net.macncheezy.easytraders.items.TradeCycler;
import net.macncheezy.easytraders.items.VillagerTrap;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("easytraders");

    public static final ItemGroup MAIN_GROUP = FabricItemGroupBuilder.create(
                    new Identifier("easytraders", "main"))
            .icon(() -> new ItemStack(Main.TRADE_STAMP))
            .build();

    // Items
    public static final VillagerTrap VILLAGER_TRAP = new VillagerTrap(new FabricItemSettings().group(MAIN_GROUP).maxDamageIfAbsent(10));
    public static final TradeCycler TRADE_CYCLER = new TradeCycler(new FabricItemSettings().group(MAIN_GROUP));
    public static final Item TRADE_STAMP = new Item(new FabricItemSettings().group(MAIN_GROUP).maxCount(1));
    public static final DyedTradeStamp DYED_TRADE_STAMP = new DyedTradeStamp(new FabricItemSettings().group(MAIN_GROUP).maxCount(1).recipeRemainder(TRADE_STAMP).maxDamage(2));
    public static final MagicTradeStamp MAGIC_TRADE_STAMP = new MagicTradeStamp(new FabricItemSettings().group(MAIN_GROUP).maxCount(1));
    public static final Item RED_WAX = new Item(new FabricItemSettings().group(MAIN_GROUP));

    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        Registry.register(Registry.ITEM, new Identifier("easytraders", "villager_trap"), VILLAGER_TRAP);
        Registry.register(Registry.ITEM, new Identifier("easytraders", "trade_cycler"), TRADE_CYCLER);
        Registry.register(Registry.ITEM, new Identifier("easytraders", "trade_stamp"), TRADE_STAMP);
        Registry.register(Registry.ITEM, new Identifier("easytraders", "dyed_trade_stamp"), DYED_TRADE_STAMP);
        Registry.register(Registry.ITEM, new Identifier("easytraders", "magic_trade_stamp"), MAGIC_TRADE_STAMP);
        Registry.register(Registry.ITEM, new Identifier("easytraders", "red_wax"), RED_WAX);

        ModelPredicateProviderRegistry.register(VILLAGER_TRAP, new Identifier("closed"), (itemStack, clientWorld, livingEntity, other) -> {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt != null && !nbt.isEmpty() && nbt.contains("IsFull")) {
                return nbt.getBoolean("IsFull") ? 1 : 0;
            }
            return 0;
        });

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && Registry.ENTITY_TYPE.get(new Identifier("minecraft", "pillager")).getLootTableId().equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(ItemEntry.builder(TRADE_STAMP)).rolls(BinomialLootNumberProvider.create(1, 0.1F));
                tableBuilder.pool(poolBuilder);
            }
        });

        Registry.register(Registry.RECIPE_SERIALIZER, new Identifier("easytraders", "stamp"), StampRecipeSerializer.INSTANCE);

        LOGGER.info("Hello Fabric world!");
    }
}
