package net.chaossquad.mclib.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

        if (key.contains(" ")) {
            throw new IllegalArgumentException("Key must not contain spaces");
        }
        
        if (!key.matches("^[a-zA-Z0-9.,:;\\-_]+$")) {
            throw new IllegalArgumentException("key must only contain letters, numbers, points or underscores");
        }

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

    /**
     * Merge another DataStorage into this DataStorage as a section.<br/>
     * This means if you merge "section1.section2" into this DataStorage with key "section0", the value in this DataStorage will be "section0.section1.section2".
     * @param key section key
     * @param section section
     * @param overwrite when true, existing values will be replaced (recommended)
     */
    public void mergeSection(@NotNull String key, @NotNull DataStorage section, boolean overwrite) {
        for (Map.Entry<String, Object> entry : section.storage.entrySet()) {
            String sectionKey = entry.getKey();
            Object value = entry.getValue();

            String mergedKey = key + "." + sectionKey;

            if (!overwrite && this.storage.containsKey(mergedKey)) continue;
            this.set(mergedKey, value);
        }
    }

    /**
     * Merge another DataStorage into this DataStorage as a section.<br/>
     * This means if you merge "section1.section2" into this DataStorage with key "section0", the value in this DataStorage will be "section0.section1.section2".<br/>
     * This method has "overwrite" set to true.
     * @param key
     * @param section
     */
    public void mergeSection(@NotNull String key, @NotNull DataStorage section) {
        this.mergeSection(key, section, true);
    }

    public Map<String, DataStorage> getSections() {
        Map<String, DataStorage> sections = new HashMap<>();

        for (String key : this.storage.keySet()) {
            String[] sectionKey  = key.split("\\.");
            if (sectionKey.length <= 1) continue;
            sections.put(sectionKey[0], this.getSection(sectionKey[0]));
        }

        return sections;
    }

    // --- MERGE ---

    /**
     * Merge the values of another DataStorage into this DataStorage.
     * @param other other data storage
     * @param overwrite when true, existing values will be replaced (recommended)
     */
    public void merge(@NotNull DataStorage other, boolean overwrite) {
        for (Map.Entry<String, Object> entry : other) {
            if (!overwrite && this.storage.containsKey(entry.getKey())) continue;
            this.set(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Merge the values of another DataStorage into this DataStorage.<br/>
     * This method has "overwrite" set to true.
     * @param other other data storage
     */
    public void merge(@NotNull DataStorage other) {
        this.merge(other, true);
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
