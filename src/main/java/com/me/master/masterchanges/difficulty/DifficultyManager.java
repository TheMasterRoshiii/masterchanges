package com.me.master.masterchanges.difficulty;

import java.util.HashMap;
import java.util.Map;

public class DifficultyManager {
    private static final DifficultyManager INSTANCE = new DifficultyManager();
    private final Map<DifficultyFeature, Boolean> features = new HashMap<>();
    private float ghastExplosionMultiplier = 2.0f;

    private DifficultyManager() {
        for (DifficultyFeature feature : DifficultyFeature.values()) {
            features.put(feature, false);
        }
    }

    public static DifficultyManager getInstance() {
        return INSTANCE;
    }

    public void setFeature(DifficultyFeature feature, boolean enabled) {
        features.put(feature, enabled);
    }

    public boolean isFeatureEnabled(DifficultyFeature feature) {
        return features.getOrDefault(feature, false);
    }

    public void enableAll() {
        for (DifficultyFeature feature : DifficultyFeature.values()) {
            features.put(feature, true);
        }
    }

    public void disableAll() {
        for (DifficultyFeature feature : DifficultyFeature.values()) {
            features.put(feature, false);
        }
    }

    public Map<DifficultyFeature, Boolean> getAllFeatures() {
        return new HashMap<>(features);
    }

    public void setGhastExplosionMultiplier(float multiplier) {
        this.ghastExplosionMultiplier = Math.max(1.0f, multiplier);
    }

    public float getGhastExplosionMultiplier() {
        return ghastExplosionMultiplier;
    }
}
