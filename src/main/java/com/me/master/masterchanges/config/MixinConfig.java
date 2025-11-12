package com.me.master.masterchanges.config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MixinConfig {
    private final String id;
    private final String displayName;
    private boolean enabled;
    private final Map<String, Object> parameters;

    public MixinConfig(String id, String displayName, boolean enabled) {
        this.id = id;
        this.displayName = displayName;
        this.enabled = enabled;
        this.parameters = new ConcurrentHashMap<>();
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void addParameter(String key, String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) { parameters.put(key, Boolean.parseBoolean(value)); }
        else {
            try {
                if (value.contains(".")) { parameters.put(key, Double.parseDouble(value)); }
                else { parameters.put(key, Integer.parseInt(value)); }
            } catch (NumberFormatException e) { parameters.put(key, value); }
        }
    }
    public void setParameter(String key, Object value) { parameters.put(key, value); }
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, Class<T> type, T defaultValue) {
        Object value = parameters.get(key);
        if (value == null) { return defaultValue; }
        if (type.isInstance(value)) { return (T) value; }
        if (type == Double.class && value instanceof Number) { return (T) Double.valueOf(((Number) value).doubleValue()); }
        if (type == Float.class && value instanceof Number) { return (T) Float.valueOf(((Number) value).floatValue()); }
        if (type == Integer.class && value instanceof Number) { return (T) Integer.valueOf(((Number) value).intValue()); }
        if (type == Boolean.class && value instanceof Boolean) { return (T) value; }
        return defaultValue;
    }
    public Map<String, Object> getAllParameters() { return new HashMap<>(parameters); }
    public Set<String> getParameterKeys() { return parameters.keySet(); }
}
