package com.me.master.masterchanges.item;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, MasterChanges.MODID);

    public static final RegistryObject<Item> BUH_CAT_TOTEM = ITEMS.register("buh_cat_totem",
        BuhCatTotemItem::new);
}
