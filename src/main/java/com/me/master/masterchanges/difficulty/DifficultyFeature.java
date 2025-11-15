package com.me.master.masterchanges.difficulty;

public enum DifficultyFeature {
    SPIDER_WEBS("spider_webs", "Arañas ponen telerañas en los pies del jugador"),
    COBWEB_SPAWN_SPIDERS("cobweb_spawn_spiders", "Romper telerañas spawnea arañas venenosas"),
    ELECTRIC_CREEPERS("electric_creepers", "Creepers eléctricos con spawn natural"),
    SPAWN_ON_BUTTONS("spawn_on_buttons", "Mobs hostiles spawnean en botones"),
    SPAWN_ON_LEAVES("spawn_on_leaves", "Mobs hostiles spawnean en hojas"),
    SPAWN_ON_SLABS("spawn_on_slabs", "Mobs hostiles spawnean en losas"),
    IGNORE_LIGHT_LEVEL("ignore_light_level", "Mobs hostiles spawnean sin importar luz"),
    PHANTOM_SCRAMBLE_INVENTORY("phantom_scramble_inventory", "Phantoms revuelven el inventario"),
    PHANTOM_DROP_ARMOR("phantom_drop_armor", "Phantoms hacen dropear armadura"),
    TRIPLE_MOB_DAMAGE("triple_mob_damage", "Mobs hacen 3x de daño"),
    TRIPLE_FALL_DAMAGE("triple_fall_damage", "Daño de caída 3x"),
    FAST_LAVA("fast_lava", "Lava se mueve más rápido"),
    TRIPLE_FIRE_DAMAGE("triple_fire_damage", "Fuego hace 3x de daño"),
    INFINITE_FIRE("infinite_fire", "Fuego dura para siempre"),
    TOTEM_PROBABILITY("totem_probability", "Probabilidad de activación de totems"),
    ACID_RAIN("acid_rain", "Lluvia ácida"),
    BEDS_EXPLODE_NIGHT("beds_explode_night", "Camas explotan de noche"),
    MAX_SIZE_SLIMES("max_size_slimes", "Slimes y Magma Cubes siempre en máximo tamaño"),
    GHAST_DOUBLE_FIREBALL("ghast_double_fireball", "Ghasts lanzan 2 bolas de fuego"),
    GHAST_EXPLOSION_MULTIPLIER("ghast_explosion_multiplier", "Bolas de Ghast explotan más"),
    BLAZE_TRIPLE_SHOT("blaze_triple_shot", "Blazes lanzan 3 disparos"),
    PIGLINS_ALWAYS_HOSTILE("piglins_always_hostile", "Piglins siempre hostiles"),
    PIGLINS_NO_BARTER("piglins_no_barter", "Piglins no dan items al tradear"),
    SILVERFISH_INSANE_DAMAGE("silverfish_insane_damage", "Silverfish hacen daño masivo"),
    ENDERMITE_INSANE_DAMAGE("endermite_insane_damage", "Endermites hacen daño masivo"),
    GUARDIAN_MULTI_TARGET("guardian_multi_target", "Guardianes atacan múltiples jugadores"),
    SHULKER_MULTI_SHOT("shulker_multi_shot", "Shulkers disparan múltiples proyectiles"),
    ENDERMAN_ALWAYS_HOSTILE("enderman_always_hostile", "Enderman siempre agresivo"),
    ENDERMAN_STEAL_ITEMS("enderman_steal_items", "Enderman roba items del inventario"),
    ENDERMAN_CANCEL_TOTEM("enderman_cancel_totem", "Enderman anula totems"),
    ENDERMAN_TELEPORT_HUNT("enderman_teleport_hunt", "Enderman se teletransporta cerca del jugador"),
    ENDERMAN_NO_WATER_FEAR("enderman_no_water_fear", "Enderman no le teme al agua"),
    FAST_DURABILITY_LOSS("fast_durability_loss", "Herramientas/armaduras pierden durabilidad 2-3x más rápido"),
    SHIELD_FAIL_CHANCE("shield_fail_chance", "Escudos tienen % de fallar al bloquear"),
    ENCHANTMENT_FAIL_CHANCE("enchantment_fail_chance", "Encantamientos tienen % de fallar temporalmente"),
    SKELETON_RANDOM_EFFECTS("skeleton_random_effects", "Esqueletos aplican efectos aleatorios"),
    SKELETON_ALL_EFFECTS("skeleton_all_effects", "Esqueletos aplican todos los efectos a la vez"),
    ZOMBIE_IMMOBILIZE("zombie_immobilize", "Zombies inmovilizan por 3 segundos"),
    ZOMBIE_CALL_HORDE("zombie_call_horde", "Zombies llaman hordas al detectar jugador"),
    DROWNED_FAST_TRIDENT("drowned_fast_trident", "Drowned lanzan tridentes más rápido"),
    HUSK_HUNGER_DRAIN("husk_hunger_drain", "Husks drenan hambre más rápido"),
    VINDICATOR_SPEED_BOOST("vindicator_speed_boost", "Vindicators se mueven más rápido en combate"),
    EVOKER_TRIPLE_VEX("evoker_triple_vex", "Evokers invocan 3 vexes"),
    VEX_LONGER_LIFE("vex_longer_life", "Vexes persisten más tiempo"),
    EVOKER_CIRCLE_FANGS("evoker_circle_fangs", "Colmillos de Evoker en círculo"),
    WITCH_FAST_POTIONS("witch_fast_potions", "Brujas lanzan pociones 2x más rápido"),
    WITCH_STRONG_POTIONS("witch_strong_potions", "Pociones de brujas con efectos aumentados"),
    WITCH_TELEPORT_ESCAPE("witch_teleport_escape", "Brujas se teletransportan si se les golpea"),
    WITCH_HEAL_MOBS("witch_heal_mobs", "Brujas curan mobs hostiles y dan buffos"),
    WITCH_TOTEM_USE("witch_totem_use", "Brujas usan totem al estar cerca de morir"),
    WITCH_ENDER_PEARL_ESCAPE("witch_ender_pearl_escape", "Brujas usan perla del end para escapar"),
    NO_INHALATION("no_inhalation", "No pueden respirar bajo agua incluso con puertas, antorchas, trampillas, etc."),
    TRIPLE_DROWNING_SPEED("triple_drowning_speed", "Te ahogas 3x más rápido"),
    ACID_RAIN_BREAK_WEAK_BLOCKS("acid_rain_break_weak_blocks", "Lluvia ácida destruye bloques débiles como hojas y madera"),
    EVOKER_VEX_AGRO("evoker_vex_agro", "Vexes siempre agresivos al ser invocados por Evoker"),
    RAVAGER_CHARGE_BLOCKS("ravager_charge_blocks", "Ravager rompe bloques al cargar si objetivo demasiado alto"),
    STRAY_SNOW_FREEZE("stray_snow_freeze", "Stray aplica efecto instantáneo de congelado al disparar flechas"),
    ILLUSIONER_NATURAL_SPAWN("illusioner_spawn", "Illusioners spawnean naturalmente en bosques oscuros"),
    GIANT_AI("giant_ai", "Giant tiene IA completa para atacar y moverse"),
    VILLAGER_NO_BREEDING("villager_no_breeding", "Aldeanos no se reproducen"),
    VILLAGER_STRESS_SMALL_SPACE("villager_stress", "Aldeanos estresados en espacios pequeños: trades caros o bloqueados"),
    VILLAGER_TRADE_RAID("villager_raid_trigger", "Trades con aldeanos pueden invocar raids"),
    PASSIVE_MOBS_AGGRESSIVE("passive_mobs_aggressive", "Mobs pacíficos son agresivos (excepto ajolote)"),
    CACTUS_INSANE_DAMAGE("cactus_insane_damage", "Cactus hace 9M de daño e insta-rompe armadura"),
    BUTTON_INSANE_DAMAGE("button_insane_damage", "Botones hacen 9M de daño"),
    DOOR_INSANE_DAMAGE("door_insane_damage", "Puertas hacen 9M de daño"),
    SPAWN_ON_NON_SOLID("spawn_on_non_solid", "Mobs spawnean en vallas, puertas, cadenas"),
    BLAZE_REMOVE_FIRE_PROTECTION("blaze_remove_fire_protection", "Blazes eliminan protección contra fuego"),
    BLAZE_BLUE_FIRE("blaze_blue_fire", "Blazes disparan bolas de fuego del dragon (moradas)"),
    BUTTON_DAMAGE_ON_PRESS("button_damage_on_press", "Botones hacen 9M de daño al presionarse"),
    DOOR_DAMAGE_ON_OPEN("door_damage_on_open", "Puertas hacen 9M de daño al abrirse"),
    LEVER_DAMAGE_ON_USE("lever_damage_on_use", "Palancas hacen 9M de daño al usarse"),
    PILLAGER_ROCKET("pillager_rocket", "Pillagers usan cohetes explosivos"),
    TRAPDOOR_DAMAGE_ON_USE("trapdoor_damage_on_use", "Trampillas hacen 9M de daño al usarse"),
    MOB_SUN_IMMUNITY("mob_sun_immunity", "Mobs hostiles no reciben daño del sol");

    private final String id;
    private final String description;

    DifficultyFeature(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
