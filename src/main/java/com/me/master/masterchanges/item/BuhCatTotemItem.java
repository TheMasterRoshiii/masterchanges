package com.me.master.masterchanges.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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
            CompoundTag tag = stack.getOrCreateTag();
            int uses = tag.getInt("TotemUses");
            uses++;
            tag.putInt("TotemUses", uses);

            entity.setHealth(1.0F);
            entity.removeAllEffects();
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 4));
            entity.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

            if (entity instanceof ServerPlayer player) {
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                if (uses >= 3) {
                    ServerLevel serverLevel = (ServerLevel) player.level();
                    BlockPos spawnPos = player.getRespawnPosition();

                    if (spawnPos != null) {
                        player.teleportTo(serverLevel, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                                player.getYRot(), player.getXRot());
                    } else {
                        BlockPos worldSpawn = serverLevel.getSharedSpawnPos();
                        player.teleportTo(serverLevel, worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5,
                                player.getYRot(), player.getXRot());
                    }
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
                    stack.shrink(1);
                } else {
                    BlockPos deathPos = entity.blockPosition();
                    tag.putInt("DeathX", deathPos.getX());
                    tag.putInt("DeathY", deathPos.getY());
                    tag.putInt("DeathZ", deathPos.getZ());
                }
            }

            return true;
        }
        return false;
    }
}
