package com.me.master.masterchanges.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MobcapConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static MobcapConfig INSTANCE;
    private Path configPath;

    private final Map<String, Integer> mobcaps = new HashMap<>();
    private final Map<String, Boolean> allowedMobs = new HashMap<>();

    private MobcapConfig() {
        this.configPath = FMLPaths.CONFIGDIR.get().resolve("masterchanges_mobcap.json");

        mobcaps.put("hostile", 70);
        mobcaps.put("passive", 10);
        mobcaps.put("ambient", 15);
        mobcaps.put("water", 5);
        mobcaps.put("water_ambient", 20);

        allowedMobs.put("illusioner", false);
        allowedMobs.put("giant", false);
        allowedMobs.put("elder_guardian", false);

        load();
    }

    public static MobcapConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MobcapConfig();
        }
        return INSTANCE;
    }

    public Map<String, Integer> getMobcaps() {
        return new HashMap<>(mobcaps);
    }

    public Map<String, Boolean> getAllowedMobs() {
        return new HashMap<>(allowedMobs);
    }

    public void setMobcap(String type, int value) {
        mobcaps.put(type, value);
    }

    public void setMobAllowed(String mob, boolean allowed) {
        allowedMobs.put(mob, allowed);
    }

    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            ConfigData data = new ConfigData();
            data.mobcaps = new HashMap<>(mobcaps);
            data.allowedMobs = new HashMap<>(allowedMobs);

            try (Writer writer = new FileWriter(configPath.toFile())) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving mobcap config: " + e.getMessage());
        }
    }

    public void load() {
        if (!Files.exists(configPath)) return;

        try (Reader reader = new FileReader(configPath.toFile())) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            if (data != null) {
                if (data.mobcaps != null) mobcaps.putAll(data.mobcaps);
                if (data.allowedMobs != null) allowedMobs.putAll(data.allowedMobs);
            }
        } catch (IOException e) {
            System.err.println("Error loading mobcap config: " + e.getMessage());
        }
    }

    private static class ConfigData {
        Map<String, Integer> mobcaps;
        Map<String, Boolean> allowedMobs;
    }
}
