package com.me.master.masterchanges.config;

import com.me.master.masterchanges.difficulty.DifficultyFeature;
import com.me.master.masterchanges.difficulty.DifficultyManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MixinConfigManager {
    private static final MixinConfigManager INSTANCE = new MixinConfigManager();
    private final Map<String, MixinConfig> mixinConfigs = new ConcurrentHashMap<>();

    private MixinConfigManager() { initializeConfigs(); }
    public static MixinConfigManager getInstance() { return INSTANCE; }

    private void initializeConfigs() {
        try {
            registerMixin("spider_webs", "Arañas ponen telerañas", false, "tickInterval:40", "detectionRange:5.0", "spreadRadius:2.0");
            registerMixin("cobweb_spawn_spiders", "Telerañas spawnean arañas", false, "spiderCount:2", "poisonDuration:600", "poisonLevel:0", "spawnSpread:3.0");
            registerMixin("electric_creepers", "Creepers eléctricos", false, "damage:8.0", "lightningChance:0.5");
            registerMixin("spawn_on_buttons", "Spawn en botones", false, "spawnChance:0.1");
            registerMixin("spawn_on_leaves", "Spawn en hojas", false, "spawnChance:0.05");
            registerMixin("spawn_on_slabs", "Spawn en losas", false, "spawnChance:0.15");
            registerMixin("ignore_light_level", "Ignorar nivel de luz", false);
            registerMixin("phantom_scramble_inventory", "Phantoms revuelven inventario", false, "itemsToScramble:36");
            registerMixin("phantom_drop_armor", "Phantoms dropean armadura", false, "dropChance:0.30");
            registerMixin("triple_mob_damage", "Daño mobs 3x", false, "damageMultiplier:3.0");
            registerMixin("triple_fall_damage", "Daño caída 3x", false, "damageMultiplier:3.0");
            registerMixin("fast_lava", "Lava rápida", false, "speedMultiplier:2.0");
            registerMixin("triple_fire_damage", "Daño fuego 3x", false, "damageMultiplier:3.0");
            registerMixin("infinite_fire", "Fuego infinito", false, "fireTicks:200");
            registerMixin("totem_probability", "Probabilidad totem", false, "failChance:0.5");
            registerMixin("acid_rain", "Lluvia ácida", false, "damage:1.0", "tickInterval:20");
            registerMixin("beds_explode_night", "Camas explotan de noche", false, "explosionRadius:5.0", "explosionDamage:10.0");
            registerMixin("max_size_slimes", "Slimes tamaño máximo", false, "maxSize:4");
            registerMixin("ghast_double_fireball", "Ghast doble bola fuego", false, "fireballCount:2");
            registerMixin("ghast_explosion_multiplier", "Multiplicador explosión Ghast", false, "multiplier:2.0");
            registerMixin("blaze_triple_shot", "Blaze triple disparo", false, "projectileCount:3", "damage:5.0");
            registerMixin("piglins_always_hostile", "Piglins siempre hostiles", false);
            registerMixin("piglins_no_barter", "Piglins sin tradeo", false);
            registerMixin("silverfish_insane_damage", "Silverfish daño masivo", false, "damage:20.0");
            registerMixin("endermite_insane_damage", "Endermite daño masivo", false, "damage:20.0");
            registerMixin("guardian_multi_target", "Guardian multi-objetivo", false, "targetCount:3", "damage:9.0");
            registerMixin("shulker_multi_shot", "Shulker multi-disparo", false, "projectileCount:3", "levitationDuration:200");
            registerMixin("enderman_always_hostile", "Enderman siempre hostil", false);
            registerMixin("enderman_steal_items", "Enderman roba items", false, "stealChance:0.3", "itemsToSteal:3");
            registerMixin("enderman_cancel_totem", "Enderman anula totem", false, "cancelChance:0.5");
            registerMixin("enderman_teleport_hunt", "Enderman teleport caza", false, "teleportRange:16.0", "huntInterval:60");
            registerMixin("fast_durability_loss", "Pérdida durabilidad rápida", false, "multiplierMin:2", "multiplierExtra:2");
            registerMixin("shield_fail_chance", "Escudo falla", false, "failChance:0.25");
            registerMixin("enchantment_fail_chance", "Encantamiento falla", false, "failChance:0.2", "failDuration:100");
            registerMixin("skeleton_random_effects", "Skeleton efectos aleatorios", false, "effectDuration:200", "effectLevel:2");
            registerMixin("skeleton_all_effects", "Skeleton todos efectos", false, "effectDuration:200", "effectLevel:1");
            registerMixin("zombie_immobilize", "Zombie inmoviliza", false, "duration:60", "hitChance:0.4");
            registerMixin("zombie_call_horde", "Zombie llama horda", false, "checkInterval:200", "spawnChance:0.30", "zombieCount:3", "spawnRadius:4.0");
            registerMixin("drowned_fast_trident", "Drowned tridente rápido", false, "attackSpeed:0.5", "damage:9.0");
            registerMixin("husk_hunger_drain", "Husk drenar hambre", false, "duration:300", "hungerLevel:2");
            registerMixin("vindicator_speed_boost", "Vindicator velocidad", false, "checkInterval:20", "effectDuration:40", "speedLevel:1");
            registerMixin("evoker_triple_vex", "Evoker triple vex", false, "vexCount:3");
            registerMixin("vex_longer_life", "Vex vida larga", false, "lifetimeMultiplier:3.0");
            registerMixin("evoker_circle_fangs", "Evoker colmillos círculo", false, "fangCount:16", "radius:3.0", "delayPerFang:2");
            registerMixin("witch_fast_potions", "Bruja pociones rápidas", false, "throwSpeed:2.0");
            registerMixin("witch_strong_potions", "Bruja pociones fuertes", false, "effectMultiplier:2.0", "durationMultiplier:1.5");
            registerMixin("witch_teleport_escape", "Bruja Teleport Escape", false, "healthThreshold:0.4", "teleportChance:1.0", "teleportRange:16.0", "maxAttempts:16");
            registerMixin("witch_heal_mobs", "Bruja Cura Mobs", false, "healInterval:60", "healRange:8.0", "regenDuration:100", "regenLevel:0", "strengthDuration:200", "strengthLevel:0");
            registerMixin("witch_totem_use", "Bruja usa totem", false, "healthThreshold:10.0", "regenDuration:900", "regenLevel:1", "absorptionDuration:100", "fireResDuration:800");
            registerMixin("witch_ender_pearl_escape", "Bruja ender pearl escape", false, "healthThreshold:15.0", "escapeChance:0.40", "teleportRange:16.0");
            registerMixin("no_inhalation", "Sin respiración bajo agua", false);
            registerMixin("triple_drowning_speed", "Ahogarse 3x rápido", false, "drowningMultiplier:3.0");
            registerMixin("acid_rain_break_weak_blocks", "Lluvia ácida rompe bloques", false, "breakChance:0.1");
            registerMixin("evoker_vex_agro", "Vex siempre agro", false);
            registerMixin("ravager_charge_blocks", "Ravager rompe bloques", false, "breakRadius:2.0", "chargeDamage:20.0");
            registerMixin("stray_snow_freeze", "Stray congelación", false, "freezeDuration:200", "slowLevel:3");
            registerMixin("illusioner_spawn", "Illusioner spawn natural", false, "spawnChance:0.05", "cloneCount:4");
            registerMixin("giant_ai", "Giant con IA", false, "damage:25.0", "health:100.0", "speed:0.3");
            registerMixin("villager_no_breeding", "Aldeanos sin reproducción", false);
            registerMixin("villager_stress", "Aldeanos estresados", false, "maxStress:100", "stressPerTick:1", "priceMultiplier:2.0");
            registerMixin("villager_raid_trigger", "Trade invoca raid", false, "raidChance:0.05", "omenDuration:6000");
            registerMixin("passive_mobs_aggressive", "Mobs pasivos agresivos", false, "damage:4.0", "aggroRange:16.0");
            registerMixin("cactus_insane_damage", "Cactus daño masivo", false, "damage:9000000.0", "armorBreak:true");
            registerMixin("button_insane_damage", "Botón daño masivo", false, "damage:9000000.0");
            registerMixin("door_insane_damage", "Puerta daño masivo", false, "damage:9000000.0");
            registerMixin("spawn_on_non_solid", "Spawn en no sólidos", false, "spawnChance:0.1");
            registerMixin("blaze_remove_fire_protection", "Blaze quita protección fuego", false);
            registerMixin("blaze_blue_fire", "Blaze fuego azul", false, "damage:10.0", "effectDuration:200");
            registerMixin("button_damage_on_press", "Botón daño al presionar", false, "damage:9000000.0");
            registerMixin("door_damage_on_open", "Puerta daño al abrir", false, "damage:9000000.0");
            registerMixin("lever_damage_on_use", "Palanca daño al usar", false, "damage:9000000.0");
            registerMixin("trapdoor_damage_on_use", "Trampilla daño al usar", false, "damage:9000000.0");
            registerMixin("pillager_rocket", "Pillager Cohetes Explosivos", false, "rocketCount:64", "explosionPower:3", "explosionRadius:2.5", "multishotLevel:1", "quickChargeLevel:3", "setFire:false");
        } catch (Exception e) {
            System.err.println("Error initializing configs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void registerMixin(String id, String displayName, boolean enabled, String... defaultParams) {
        try {
            MixinConfig config = new MixinConfig(id, displayName, enabled);
            for (String param : defaultParams) {
                String[] parts = param.split(":");
                if (parts.length == 2) { config.addParameter(parts[0], parts[1]); }
            }
            mixinConfigs.put(id, config);
        } catch (Exception e) {
            System.err.println("Error registering mixin " + id + ": " + e.getMessage());
        }
    }

    public MixinConfig getConfig(String mixinId) { return mixinConfigs.get(mixinId); }
    public Collection<MixinConfig> getAllConfigs() { return mixinConfigs.values(); }

    public void setMixinEnabled(String mixinId, boolean enabled) {
        try {
            MixinConfig config = mixinConfigs.get(mixinId);
            if (config != null) {
                config.setEnabled(enabled);
                syncToDifficultyManager(mixinId, enabled);
            }
        } catch (Exception e) {
            System.err.println("Error setting mixin enabled " + mixinId + ": " + e.getMessage());
        }
    }

    private void syncToDifficultyManager(String mixinId, boolean enabled) {
        try {
            DifficultyFeature feature = findFeatureById(mixinId);
            if (feature != null) {
                DifficultyManager.getInstance().setFeature(feature, enabled);
            }
            if (mixinId.equals("ghast_explosion_multiplier")) {
                float multiplier = getFloat(mixinId, "multiplier", 2.0f);
                DifficultyManager.getInstance().setGhastExplosionMultiplier(multiplier);
            }
        } catch (Exception e) {
            System.err.println("Error syncing to difficulty manager: " + e.getMessage());
        }
    }

    private DifficultyFeature findFeatureById(String id) {
        for (DifficultyFeature feature : DifficultyFeature.values()) {
            if (feature.getId().equals(id)) {
                return feature;
            }
        }
        return null;
    }

    public boolean isMixinEnabled(String mixinId) {
        MixinConfig config = mixinConfigs.get(mixinId);
        return config != null && config.isEnabled();
    }

    public void setParameter(String mixinId, String key, Object value) {
        try {
            MixinConfig config = mixinConfigs.get(mixinId);
            if (config != null) {
                config.setParameter(key, value);
                if (mixinId.equals("ghast_explosion_multiplier") && key.equals("multiplier")) {
                    DifficultyManager.getInstance().setGhastExplosionMultiplier(((Number)value).floatValue());
                }
            }
        } catch (Exception e) {
            System.err.println("Error setting parameter: " + e.getMessage());
        }
    }

    public <T> T getParameter(String mixinId, String key, Class<T> type, T defaultValue) {
        try {
            MixinConfig config = mixinConfigs.get(mixinId);
            if (config != null) { return config.getParameter(key, type, defaultValue); }
        } catch (Exception e) {
            System.err.println("Error getting parameter: " + e.getMessage());
        }
        return defaultValue;
    }

    public double getDouble(String mixinId, String key, double defaultValue) { return getParameter(mixinId, key, Double.class, defaultValue); }
    public int getInt(String mixinId, String key, int defaultValue) { return getParameter(mixinId, key, Integer.class, defaultValue); }
    public boolean getBoolean(String mixinId, String key, boolean defaultValue) { return getParameter(mixinId, key, Boolean.class, defaultValue); }
    public float getFloat(String mixinId, String key, float defaultValue) { return getParameter(mixinId, key, Float.class, defaultValue); }
}
