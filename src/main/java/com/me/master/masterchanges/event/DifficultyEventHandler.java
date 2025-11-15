package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
import com.me.master.masterchanges.config.MixinConfigManager;
import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = MasterChanges.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DifficultyEventHandler {

    @SubscribeEvent
    public static void onSpiderNearPlayer(LivingEvent.LivingTickEvent event) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.SPIDER_WEBS)) return;
        MixinConfigManager config = MixinConfigManager.getInstance();

        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Spider spider)) return;
        Level level = entity.level();
        if (level.isClientSide) return;

        int tickInterval = config.getInt("spider_webs", "tickInterval", 10);
        double detectionRange = config.getDouble("spider_webs", "detectionRange", 5.0);

        if (spider.tickCount % tickInterval != 0) return;

        Player nearestPlayer = level.getNearestPlayer(spider, detectionRange);
        if (nearestPlayer != null) {
            BlockPos pos = nearestPlayer.blockPosition();

            BlockPos[] possiblePositions = {
                    pos,
                    pos.below(),
                    pos.north(),
                    pos.south(),
                    pos.east(),
                    pos.west(),
                    pos.north().east(),
                    pos.north().west(),
                    pos.south().east(),
                    pos.south().west()
            };

            for (BlockPos webPos : possiblePositions) {
                BlockState current = level.getBlockState(webPos);
                if (current.isAir()) {
                    level.setBlock(webPos, Blocks.COBWEB.defaultBlockState(), 3);
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onCobwebBreak(BlockEvent.BreakEvent event) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.COBWEB_SPAWN_SPIDERS)) return;
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (!event.getState().is(Blocks.COBWEB)) return;
        Level level = (Level) event.getLevel();
        if (level.isClientSide) return;
        BlockPos pos = event.getPos();
        RandomSource random = level.getRandom();

        int spiderCount = config.getInt("cobweb_spawn_spiders", "spiderCount", 2);
        int poisonDuration = config.getInt("cobweb_spawn_spiders", "poisonDuration", 600);
        int poisonLevel = config.getInt("cobweb_spawn_spiders", "poisonLevel", 0);
        double spawnSpread = config.getDouble("cobweb_spawn_spiders", "spawnSpread", 3.0);

        for (int i = 0; i < spiderCount; i++) {
            CaveSpider spider = new CaveSpider(EntityType.CAVE_SPIDER, level);
            double offsetX = (random.nextDouble() - 0.5) * spawnSpread;
            double offsetZ = (random.nextDouble() - 0.5) * spawnSpread;
            spider.moveTo(pos.getX() + 0.5 + offsetX, pos.getY(), pos.getZ() + 0.5 + offsetZ, 0, 0);
            spider.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, poisonDuration, poisonLevel));
            level.addFreshEntity(spider);
        }
    }

    @SubscribeEvent
    public static void onPhantomAttack(LivingAttackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getSource().getEntity() instanceof Phantom)) return;
        Level level = player.level();
        if (level.isClientSide) return;
        DifficultyManager manager = DifficultyManager.getInstance();
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (manager.isFeatureEnabled(DifficultyFeature.PHANTOM_SCRAMBLE_INVENTORY)) {
            int itemsToScramble = config.getInt("phantom_scramble_inventory", "itemsToScramble", 36);
            scrambleInventory(player, itemsToScramble);
        }

        if (manager.isFeatureEnabled(DifficultyFeature.PHANTOM_DROP_ARMOR) && !player.isCreative()) {
            float dropChance = config.getFloat("phantom_drop_armor", "dropChance", 0.30f);
            if (player.getRandom().nextFloat() < dropChance) dropRandomArmor(player);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingHurtEvent event) {
        DifficultyManager manager = DifficultyManager.getInstance();
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (manager.isFeatureEnabled(DifficultyFeature.TRIPLE_MOB_DAMAGE)) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && !(attacker instanceof Player)) {
                float multiplier = config.getFloat("triple_mob_damage", "damageMultiplier", 3.0f);
                event.setAmount(event.getAmount() * multiplier);
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.TRIPLE_FALL_DAMAGE)) {
            if (event.getSource().is(DamageTypes.FALL)) {
                float multiplier = config.getFloat("triple_fall_damage", "damageMultiplier", 3.0f);
                event.setAmount(event.getAmount() * multiplier);
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.TRIPLE_FIRE_DAMAGE)) {
            if (event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.LAVA)) {
                float multiplier = config.getFloat("triple_fire_damage", "damageMultiplier", 3.0f);
                event.setAmount(event.getAmount() * multiplier);
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.SILVERFISH_INSANE_DAMAGE)) {
            if (event.getSource().getEntity() instanceof Silverfish) {
                float damage = config.getFloat("silverfish_insane_damage", "damage", Float.MAX_VALUE);
                event.setAmount(damage);
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.ENDERMITE_INSANE_DAMAGE)) {
            if (event.getSource().getEntity() instanceof Endermite) {
                float damage = config.getFloat("endermite_insane_damage", "damage", Float.MAX_VALUE);
                event.setAmount(damage);
            }
        }

        if (event.getEntity() instanceof Player player) {
            if (event.getSource().getDirectEntity() instanceof Arrow arrow) {
                if (arrow.getOwner() instanceof Skeleton || arrow.getOwner() instanceof WitherSkeleton) {
                    if (manager.isFeatureEnabled(DifficultyFeature.SKELETON_ALL_EFFECTS)) {
                        int duration = config.getInt("skeleton_all_effects", "effectDuration", 200);
                        int level = config.getInt("skeleton_all_effects", "effectLevel", 1);
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, duration, level));
                        player.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, level));
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, level));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, level));
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, level));
                    } else if (manager.isFeatureEnabled(DifficultyFeature.SKELETON_RANDOM_EFFECTS)) {
                        int duration = config.getInt("skeleton_random_effects", "effectDuration", 200);
                        int effectLevel = config.getInt("skeleton_random_effects", "effectLevel", 1);
                        int randomIndex = player.getRandom().nextInt(4);
                        MobEffectInstance effect = switch (randomIndex) {
                            case 0 -> new MobEffectInstance(MobEffects.POISON, duration, effectLevel);
                            case 1 -> new MobEffectInstance(MobEffects.WITHER, duration, effectLevel);
                            case 2 -> new MobEffectInstance(MobEffects.WEAKNESS, duration, effectLevel);
                            default -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, effectLevel);
                        };
                        player.addEffect(effect);
                    }
                }
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.HUSK_HUNGER_DRAIN) && event.getSource().getEntity() instanceof Husk) {
            if (event.getEntity() instanceof Player player) {
                int duration = config.getInt("husk_hunger_drain", "duration", 300);
                int hungerLevel = config.getInt("husk_hunger_drain", "hungerLevel", 2);
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, hungerLevel));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        Level level = entity.level();
        DifficultyManager manager = DifficultyManager.getInstance();
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (manager.isFeatureEnabled(DifficultyFeature.INFINITE_FIRE)) {
            if (entity.isOnFire() && !entity.isInWaterOrBubble() && !entity.isInPowderSnow) {
                BlockPos pos = entity.blockPosition();
                BlockState blockBelow = level.getBlockState(pos);

                if (blockBelow.getBlock() instanceof net.minecraft.world.level.block.LayeredCauldronBlock) {
                    entity.clearFire();
                    return;
                }

                int fireTicks = config.getInt("infinite_fire", "fireTicks", 200);
                entity.setRemainingFireTicks(fireTicks);
            }
        }


        if (manager.isFeatureEnabled(DifficultyFeature.ACID_RAIN)) {
            if (entity instanceof Player player && level.isRaining() && level.canSeeSky(player.blockPosition().above())) {
                int tickInterval = config.getInt("acid_rain", "tickInterval", 20);
                float damage = config.getFloat("acid_rain", "damage", 1.0f);
                if (player.tickCount % tickInterval == 0) {
                    player.hurt(level.damageSources().magic(), damage);
                }
            }
        }

        if (entity instanceof Zombie zombie && manager.isFeatureEnabled(DifficultyFeature.ZOMBIE_CALL_HORDE)) {
            int checkInterval = config.getInt("zombie_call_horde", "checkInterval", 200);
            if (zombie.getTarget() instanceof Player && zombie.tickCount % checkInterval == 0) {
                float spawnChance = config.getFloat("zombie_call_horde", "spawnChance", 0.30f);
                if (zombie.getRandom().nextFloat() < spawnChance) {
                    int zombieCount = config.getInt("zombie_call_horde", "zombieCount", 3);
                    double spawnRadius = config.getDouble("zombie_call_horde", "spawnRadius", 4.0);
                    for (int i = 0; i < zombieCount; i++) {
                        Zombie newZombie = new Zombie(EntityType.ZOMBIE, level);
                        double offsetX = (zombie.getRandom().nextDouble() - 0.5) * spawnRadius * 2;
                        double offsetZ = (zombie.getRandom().nextDouble() - 0.5) * spawnRadius * 2;
                        newZombie.moveTo(zombie.getX() + offsetX, zombie.getY(), zombie.getZ() + offsetZ, 0, 0);
                        newZombie.setTarget(zombie.getTarget());
                        level.addFreshEntity(newZombie);
                    }
                }
            }
        }

        if (entity instanceof Vindicator vindicator && manager.isFeatureEnabled(DifficultyFeature.VINDICATOR_SPEED_BOOST)) {
            int checkInterval = config.getInt("vindicator_speed_boost", "checkInterval", 20);
            int effectDuration = config.getInt("vindicator_speed_boost", "effectDuration", 40);
            int speedLevel = config.getInt("vindicator_speed_boost", "speedLevel", 1);
            if (vindicator.getTarget() != null && vindicator.tickCount % checkInterval == 0 && !vindicator.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                vindicator.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, effectDuration, speedLevel));
            }
        }

        if (entity instanceof Witch witch && manager.isFeatureEnabled(DifficultyFeature.WITCH_HEAL_MOBS)) {
            int healInterval = config.getInt("witch_heal_mobs", "healInterval", 60);
            double healRange = config.getDouble("witch_heal_mobs", "healRange", 8.0);
            int regenDuration = config.getInt("witch_heal_mobs", "regenDuration", 100);
            int regenLevel = config.getInt("witch_heal_mobs", "regenLevel", 0);
            int strengthDuration = config.getInt("witch_heal_mobs", "strengthDuration", 200);
            int strengthLevel = config.getInt("witch_heal_mobs", "strengthLevel", 0);

            if (witch.tickCount % healInterval == 0) {
                List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, witch.getBoundingBox().inflate(healRange));
                for (Mob mob : nearbyMobs) {
                    if (mob != witch && mob instanceof Monster) {
                        mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDuration, regenLevel));
                        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, strengthDuration, strengthLevel));
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPiglinInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof Piglin && DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.PIGLINS_NO_BARTER))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onWitchCombat(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Witch witch)) return;
        Level level = witch.level();
        if (level.isClientSide) return;
        DifficultyManager manager = DifficultyManager.getInstance();
        MixinConfigManager config = MixinConfigManager.getInstance();

        if (manager.isFeatureEnabled(DifficultyFeature.WITCH_TELEPORT_ESCAPE) && event.getSource().getEntity() instanceof Player) {
            float teleportChance = config.getFloat("witch_teleport_escape", "teleportChance", 1.0f);
            if (witch.getRandom().nextFloat() < teleportChance) {
                double teleportRange = config.getDouble("witch_teleport_escape", "teleportRange", 16.0);
                double x = witch.getX() + (witch.getRandom().nextDouble() - 0.5) * teleportRange * 2;
                double y = witch.getY();
                double z = witch.getZ() + (witch.getRandom().nextDouble() - 0.5) * teleportRange * 2;
                witch.teleportTo(x, y, z);
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.WITCH_TOTEM_USE)) {
            float healthThreshold = config.getFloat("witch_totem_use", "healthThreshold", 10.0f);
            if (witch.getHealth() < healthThreshold && !witch.hasEffect(MobEffects.REGENERATION)) {
                int regenDuration = config.getInt("witch_totem_use", "regenDuration", 900);
                int regenLevel = config.getInt("witch_totem_use", "regenLevel", 1);
                int absorptionDuration = config.getInt("witch_totem_use", "absorptionDuration", 100);
                int fireResDuration = config.getInt("witch_totem_use", "fireResDuration", 800);

                witch.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDuration, regenLevel));
                witch.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, absorptionDuration, 1));
                witch.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, fireResDuration, 0));
            }
        }

        if (manager.isFeatureEnabled(DifficultyFeature.WITCH_ENDER_PEARL_ESCAPE)) {
            float healthThreshold = config.getFloat("witch_ender_pearl_escape", "healthThreshold", 15.0f);
            float escapeChance = config.getFloat("witch_ender_pearl_escape", "escapeChance", 0.40f);
            if (witch.getHealth() < healthThreshold && witch.getRandom().nextFloat() < escapeChance) {
                double teleportRange = config.getDouble("witch_ender_pearl_escape", "teleportRange", 16.0);
                double x = witch.getX() + (witch.getRandom().nextDouble() - 0.5) * teleportRange * 2;
                double y = Math.max(witch.getY(), level.getMinBuildHeight() + 1);
                double z = witch.getZ() + (witch.getRandom().nextDouble() - 0.5) * teleportRange * 2;
                witch.teleportTo(x, y, z);
            }
        }
    }

    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.VILLAGER_NO_BREEDING)) return;
        if (!(event.getEntity() instanceof Player) || !(event.getTarget() instanceof Villager)) return;

        Player player = (Player) event.getEntity();
        ItemStack held = player.getMainHandItem();
        if (held.is(Items.WHEAT)) {
            event.setCanceled(true);
            player.displayClientMessage(Component.literal("¡Reproducción desactivada!"), true);
        }
    }

    @SubscribeEvent
    public static void onVillagerTrade(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.VILLAGER_TRADE_RAID)) return;
        if (!(event.getEntity() instanceof Player) || !(event.getTarget() instanceof Villager)) return;

        Player player = (Player) event.getEntity();
        Level level = player.level();
        if (level.isClientSide) return;
        MixinConfigManager config = MixinConfigManager.getInstance();

        float raidChance = config.getFloat("villager_raid_trigger", "raidChance", 0.05f);
        int omenDuration = config.getInt("villager_raid_trigger", "omenDuration", 6000);

        if (player.getRandom().nextFloat() < raidChance) {
            player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, omenDuration, 0));
            player.displayClientMessage(Component.literal("Efecto Bad Omen aplicado por comerciar. ¡Prepárate para una raid!"), true);
        }
    }

    private static void scrambleInventory(Player player, int slots) {
        List<ItemStack> items = new ArrayList<>();
        int maxSlots = Math.min(slots, 36);
        for (int i = 0; i < maxSlots; i++) items.add(player.getInventory().getItem(i).copy());
        java.util.Random random = new java.util.Random(player.getRandom().nextLong());
        Collections.shuffle(items, random);
        for (int i = 0; i < maxSlots; i++) player.getInventory().setItem(i, items.get(i));
    }

    private static void dropRandomArmor(Player player) {
        EquipmentSlot[] armorSlots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        EquipmentSlot slot = armorSlots[player.getRandom().nextInt(armorSlots.length)];
        ItemStack armor = player.getItemBySlot(slot);
        if (!armor.isEmpty()) {
            player.drop(armor, true);
            player.setItemSlot(slot, ItemStack.EMPTY);
        }
    }
}
