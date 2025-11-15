package com.me.master.masterchanges.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigPersistence {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "masterchanges_config.json");

    public static void saveConfigs(Map<String, MixinConfig> configs) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            
            Map<String, ConfigData> dataMap = new HashMap<>();
            configs.forEach((id, config) -> {
                ConfigData data = new ConfigData();
                data.enabled = config.isEnabled();
                data.parameters = config.getAllParameters();
                dataMap.put(id, data);
            });
            
            try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
                GSON.toJson(dataMap, writer);
            }
        } catch (IOException e) {
            System.err.println("Error saving configs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Map<String, ConfigData> loadConfigs() {
        if (!Files.exists(CONFIG_PATH)) {
            return new HashMap<>();
        }
        
        try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
            Type type = new TypeToken<Map<String, ConfigData>>(){}.getType();
            Map<String, ConfigData> loaded = GSON.fromJson(reader, type);
            return loaded != null ? loaded : new HashMap<>();
        } catch (IOException e) {
            System.err.println("Error loading configs: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public static class ConfigData {
        public boolean enabled;
        public Map<String, Object> parameters;
    }
}
