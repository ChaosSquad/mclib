package net.chaossquad.mclib.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DataStorage implements Iterable<Map.Entry<String, Object>> {
    @NotNull private final Map<String, Object> storage;

    public DataStorage() {
        this.storage = new HashMap<>();
    }

    public DataStorage(@NotNull Map<?, ?> storage) {
        this();

        for (Map.Entry<?, ?> entry : storage.entrySet()) {
            this.storage.put(entry.getKey().toString(), DataStorage.convertObject(entry.getValue()));
        }

    }

    // --- BASIC OPERATIONS ---

    @Nullable
    public final Object get(String key) {
        return this.storage.get(key);
    }

    public final void set(@NotNull String key, @Nullable Object value) {

        if (value == null) {
            this.storage.remove(key);
            return;
        }

        if (value instanceof DataStorage s) {
            this.mergeSection(key, s);
            return;
        }

        if (value instanceof Map<?, ?> m) {
            this.mergeSection(key, new DataStorage(m));
            return;
        }

        this.storage.put(key, convertObject(value));
    }

    @Nullable
    public final Object remove(String key) {
        return this.storage.remove(key);
    }

    @NotNull
    public final Map<String, Object> asMap() {
        return Map.copyOf(this.storage);
    }

    public final void clear() {
        this.storage.clear();
    }

    // --- SECTIONS ---

    @NotNull
    public DataStorage getSection(@NotNull String key) {
        DataStorage sectionStorage = new DataStorage();

        for (Map.Entry<String, Object> entry : Map.copyOf(this.storage).entrySet()) {
            String entryKey = entry.getKey();

            if (entryKey.startsWith(key + ".")) {
                String newKey = entryKey.substring(key.length() + 1);
                sectionStorage.set(newKey, entry.getValue());
            }
        }

        return sectionStorage;
    }

    public void mergeSection(@NotNull String key, @NotNull DataStorage section) {
        for (Map.Entry<String, Object> entry : section.storage.entrySet()) {
            String sectionKey = entry.getKey();
            Object value = entry.getValue();

            String mergedKey = key + "." + sectionKey;

            this.set(mergedKey, value);
        }
    }

    // --- ITERABLE ---

    @Override
    @NotNull
    public Iterator<Map.Entry<String, Object>> iterator() {
        return this.asMap().entrySet().iterator();
    }

    // --- SETS ---

    @NotNull
    public Set<String> keySet() {
        return this.asMap().keySet();
    }

    @NotNull
    public Set<Map.Entry<String, Object>> entrySet() {
        return this.asMap().entrySet();
    }

    // --- GET SPECIFIC TYPES ---

    public int optInt(@NotNull String key, int defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Integer v) return v;
        return defaultValue;
    }

    public long optLong(@NotNull String key, long defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Long v) return v;
        return defaultValue;
    }

    public double optDouble(@NotNull String key, double defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Double v) return v;
        return defaultValue;
    }

    public float optFloat(@NotNull String key, float defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Float v) return v;
        return defaultValue;
    }

    public boolean optBoolean(@NotNull String key, boolean defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Boolean v) return v;
        return defaultValue;
    }

    @Nullable
    public String optString(@NotNull String key, @Nullable String defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof String v) return v;
        return defaultValue;
    }

    public byte optByte(@NotNull String key, byte defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Byte v) return v;
        return defaultValue;
    }

    public short optShort(@NotNull String key, short defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Short v) return v;
        return defaultValue;
    }

    public char optChar(@NotNull String key, char defaultValue) {
        Object value = this.storage.get(key);
        if (value instanceof Character v) return v;
        return defaultValue;
    }

    // --- CLONE ---

    @Override
    public DataStorage clone() {
        DataStorage storage = new DataStorage();
        storage.storage.putAll(this.storage);
        return storage;
    }

    // --- STATIC ---

    @NotNull
    public static Object convertObject(@NotNull Object o) {

        return switch (o) {
            case Integer i -> i;
            case Long l -> l;
            case Double d -> d;
            case Float f -> f;
            case Boolean b -> b;
            case String s -> s;
            case Byte b -> b;
            case Short s -> s;
            case Character c -> c;
            default -> o.toString();
        };

    }
}
