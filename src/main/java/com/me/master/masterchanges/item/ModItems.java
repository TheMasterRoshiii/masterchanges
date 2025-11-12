package com.me.master.masterchanges.item;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, MasterChanges.MODID);
    public static final RegistryObject<Item> NUTRITOTEM = ITEMS.register("nutritotem",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TOTEM_ESPECIAL = ITEMS.register("totem_especial",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TOTEM_MARIPOSA = ITEMS.register("totem_mariposa",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TOTEM_REVIL = ITEMS.register("totem_revil",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> TOTEM_EON = ITEMS.register("totem_eon",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));


    public static final RegistryObject<Item> TOTEM_OWL = ITEMS.register("totem_owl",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));

    public static final RegistryObject<Item> BUH_CAT_TOTEM = ITEMS.register("buh_cat_totem",
        BuhCatTotemItem::new);
}
