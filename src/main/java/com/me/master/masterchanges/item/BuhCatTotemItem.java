package com.me.master.masterchanges.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class BuhCatTotemItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BuhCatTotemItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, state -> {
            return state.setAndContinue(RawAnimation.begin().thenLoop("idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BuhCatTotemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new BuhCatTotemRenderer();
                }
                return this.renderer;
            }
        });
    }

    public static boolean tryActivateTotem(LivingEntity entity, ItemStack stack) {
        if (entity.getHealth() <= 0.0F) {
            if (!entity.level().isClientSide()) {
                entity.setHealth(1.0F);
                entity.removeAllEffects();
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 4));
                entity.playSound(SoundEvents.TOTEM_USE, 1.0F, 1.0F);
                stack.shrink(1);
            }
            return true;
        }
        return false;
    }
}
