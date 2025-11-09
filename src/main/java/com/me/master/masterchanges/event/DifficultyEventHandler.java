package com.me.master.masterchanges.event;

import com.me.master.masterchanges.MasterChanges;
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
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
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
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Spider spider)) return;
        Level level = entity.level();
        if (level.isClientSide) return;
        if (spider.tickCount % 40 != 0) return;
        Player nearestPlayer = level.getNearestPlayer(spider, 5.0);
        if (nearestPlayer != null) {
            BlockPos playerPos = nearestPlayer.blockPosition().below();
            RandomSource random = spider.getRandom();
            double offsetX = (random.nextDouble() - 0.5) * 2;
            double offsetZ = (random.nextDouble() - 0.5) * 2;
            BlockPos webPos = playerPos.offset((int) offsetX, 0, (int) offsetZ);
            BlockState current = level.getBlockState(webPos);
            if (current.isAir() && !level.getBlockState(webPos.above()).isSolid()) {
                level.setBlock(webPos, Blocks.COBWEB.defaultBlockState(), 3);
            }
        }
    }

    @SubscribeEvent
    public static void onCobwebBreak(BlockEvent.BreakEvent event) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.COBWEB_SPAWN_SPIDERS)) return;
        if (!event.getState().is(Blocks.COBWEB)) return;
        Level level = (Level) event.getLevel();
        if (level.isClientSide) return;
        BlockPos pos = event.getPos();
        RandomSource random = level.getRandom();
        for (int i = 0; i < 2; i++) {
            CaveSpider spider = new CaveSpider(EntityType.CAVE_SPIDER, level);
            double offsetX = (random.nextDouble() - 0.5) * 3;
            double offsetZ = (random.nextDouble() - 0.5) * 3;
            spider.moveTo(pos.getX() + 0.5 + offsetX, pos.getY(), pos.getZ() + 0.5 + offsetZ, 0, 0);
            spider.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 0));
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
        if (manager.isFeatureEnabled(DifficultyFeature.PHANTOM_SCRAMBLE_INVENTORY)) scrambleInventory(player);
        if (manager.isFeatureEnabled(DifficultyFeature.PHANTOM_DROP_ARMOR) && !player.isCreative()) {
            if (player.getRandom().nextFloat() < 0.30f) dropRandomArmor(player);
        }
    }

    @SubscribeEvent
    public static void onDamage(LivingHurtEvent event) {
        DifficultyManager manager = DifficultyManager.getInstance();
        if (manager.isFeatureEnabled(DifficultyFeature.TRIPLE_MOB_DAMAGE)) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker && !(attacker instanceof Player)) {
                event.setAmount(event.getAmount() * 3.0f);
            }
        }
        if (manager.isFeatureEnabled(DifficultyFeature.TRIPLE_FALL_DAMAGE)) {
            if (event.getSource().is(DamageTypes.FALL)) event.setAmount(event.getAmount() * 3.0f);
        }
        if (manager.isFeatureEnabled(DifficultyFeature.TRIPLE_FIRE_DAMAGE)) {
            if (event.getSource().is(DamageTypes.IN_FIRE) || event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.LAVA)) event.setAmount(event.getAmount() * 3.0f);
        }
        if (manager.isFeatureEnabled(DifficultyFeature.SILVERFISH_INSANE_DAMAGE)) {
            if (event.getSource().getEntity() instanceof Silverfish) event.setAmount(Float.MAX_VALUE);
        }
        if (manager.isFeatureEnabled(DifficultyFeature.ENDERMITE_INSANE_DAMAGE)) {
            if (event.getSource().getEntity() instanceof Endermite) event.setAmount(Float.MAX_VALUE);
        }
        if (event.getEntity() instanceof Player player) {
            if (event.getSource().getDirectEntity() instanceof Arrow arrow) {
                if (arrow.getOwner() instanceof Skeleton skeletonOwner || arrow.getOwner() instanceof WitherSkeleton witherOwner) {
                    DifficultyFeature df = DifficultyFeature.SKELETON_ALL_EFFECTS;
                    DifficultyFeature ds = DifficultyFeature.SKELETON_RANDOM_EFFECTS;
                    if (manager.isFeatureEnabled(df)) {
                        player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 1));
                    } else if (manager.isFeatureEnabled(ds)) {
                        int randomIndex = player.getRandom().nextInt(4);
                        switch (randomIndex) {
                            case 0 -> player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                            case 1 -> player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
                            case 2 -> player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
                            case 3 -> player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
                        }
                    }
                }
            }
        }
        if (manager.isFeatureEnabled(DifficultyFeature.HUSK_HUNGER_DRAIN) && event.getSource().getEntity() instanceof Husk) {
            if (event.getEntity() instanceof Player player) player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 300, 2));
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        Level level = entity.level();
        DifficultyManager manager = DifficultyManager.getInstance();

        if (manager.isFeatureEnabled(DifficultyFeature.INFINITE_FIRE)) {
            if (entity.isOnFire() && !entity.isInWaterOrBubble()) entity.setRemainingFireTicks(200);
        }

        if (manager.isFeatureEnabled(DifficultyFeature.ACID_RAIN)) {
            if (entity instanceof Player player && level.isRaining() && level.canSeeSky(player.blockPosition())) {
                if (player.tickCount % 20 == 0) player.hurt(level.damageSources().magic(), 1.0f);
            }
        }

        if (entity instanceof Zombie zombie && manager.isFeatureEnabled(DifficultyFeature.ZOMBIE_CALL_HORDE)) {
            if (zombie.getTarget() instanceof Player && zombie.tickCount % 200 == 0) {
                if (zombie.getRandom().nextFloat() < 0.30f) {
                    for (int i = 0; i < 3; i++) {
                        Zombie newZombie = new Zombie(EntityType.ZOMBIE, level);
                        double offsetX = (zombie.getRandom().nextDouble() - 0.5) * 4;
                        double offsetZ = (zombie.getRandom().nextDouble() - 0.5) * 4;
                        newZombie.moveTo(zombie.getX() + offsetX, zombie.getY(), zombie.getZ() + offsetZ, 0, 0);
                        newZombie.setTarget(zombie.getTarget());
                        level.addFreshEntity(newZombie);
                    }
                }
            }
        }

        if (entity instanceof Vindicator vindicator && manager.isFeatureEnabled(DifficultyFeature.VINDICATOR_SPEED_BOOST)) {
            if (vindicator.getTarget() != null && vindicator.tickCount % 20 == 0 && !vindicator.hasEffect(MobEffects.MOVEMENT_SPEED))
                vindicator.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 1));
        }

        if (entity instanceof Witch witch && manager.isFeatureEnabled(DifficultyFeature.WITCH_HEAL_MOBS)) {
            if (witch.tickCount % 60 == 0) {
                List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, witch.getBoundingBox().inflate(8.0));
                for (Mob mob : nearbyMobs) {
                    if (mob != witch && mob instanceof Monster) {
                        mob.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
                        mob.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 0));
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
        if (manager.isFeatureEnabled(DifficultyFeature.WITCH_TELEPORT_ESCAPE) && event.getSource().getEntity() instanceof Player) {
            double x = witch.getX() + (witch.getRandom().nextDouble() - 0.5) * 16;
            double y = witch.getY();
            double z = witch.getZ() + (witch.getRandom().nextDouble() - 0.5) * 16;
            witch.teleportTo(x, y, z);
        }
        if (manager.isFeatureEnabled(DifficultyFeature.WITCH_TOTEM_USE) && witch.getHealth() < 10.0f && !witch.hasEffect(MobEffects.REGENERATION)) {
            witch.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            witch.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            witch.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        }
        if (manager.isFeatureEnabled(DifficultyFeature.WITCH_ENDER_PEARL_ESCAPE) && witch.getHealth() < 15.0f && witch.getRandom().nextFloat() < 0.40f) {
            double x = witch.getX() + (witch.getRandom().nextDouble() - 0.5) * 16;
            double y = Math.max(witch.getY(), level.getMinBuildHeight() + 1);
            double z = witch.getZ() + (witch.getRandom().nextDouble() - 0.5) * 16;
            witch.teleportTo(x, y, z);
        }
    }

    @SubscribeEvent
    public static void onVillagerInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (!DifficultyManager.getInstance().isFeatureEnabled(DifficultyFeature.VILLAGER_NO_BREEDING)) return;
        if (!(event.getEntity() instanceof Player) || !(event.getTarget() instanceof Villager)) return;

        Player player = (Player) event.getEntity();
        Villager villager = (Villager) event.getTarget();

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
        Villager villager = (Villager) event.getTarget();

        Level level = player.level();
        if (level.isClientSide) return;

        if (player.getRandom().nextFloat() < 0.05f) {
            player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 6000, 0));
            player.displayClientMessage(Component.literal("Efecto Bad Omen aplicado por comerciar. ¡Prepárate para una raid!"), true);
        }
    }


    private static void scrambleInventory(Player player) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 36; i++) items.add(player.getInventory().getItem(i).copy());
        java.util.Random random = new java.util.Random(player.getRandom().nextLong());
        Collections.shuffle(items, random);
        for (int i = 0; i < 36; i++) player.getInventory().setItem(i, items.get(i));
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
