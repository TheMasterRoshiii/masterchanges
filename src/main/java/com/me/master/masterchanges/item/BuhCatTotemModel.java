package com.me.master.masterchanges.item;

import com.me.master.masterchanges.MasterChanges;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BuhCatTotemModel extends GeoModel<BuhCatTotemItem> {

    @Override
    public ResourceLocation getModelResource(BuhCatTotemItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(MasterChanges.MODID, "geo/buh_cat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BuhCatTotemItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(MasterChanges.MODID, "textures/item/buh_cat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BuhCatTotemItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(MasterChanges.MODID, "animations/buh_cat.animation.json");
    }
}
