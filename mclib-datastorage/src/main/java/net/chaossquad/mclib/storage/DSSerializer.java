package net.chaossquad.mclib.storage;

import net.jandie1505.datastorage.DataStorage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Contains utilities for serializing/deserializing DataStorages.
 */
public final class DSSerializer {

    private DSSerializer() {}

    /**
     * Serializes a DataStorage to a Bukkit YamlConfiguration.
     * @param storage storage
     * @return yaml configuration
     */
    public static YamlConfiguration serialize(@NotNull DataStorage storage) {
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, Object> entry : storage.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }

        return config;
    }

    /**
     * Deserializes a DataStorage from a Bukkit YamlConfiguration.
     * @param config config
     * @return storage
     */
    public static DataStorage deserialize(@NotNull YamlConfiguration config) {
        DataStorage storage = new DataStorage();

        for (String key : config.getKeys(true)) {
            Object value = config.get(key);
            if (value instanceof ConfigurationSection) continue;
            storage.set(key, value);
        }

        return storage;
    }

    /**
     * Loads a YamlConfig file and deserializes it to a DataStorage.
     * @param file file path
     * @return storage
     * @throws IOException file error
     * @throws InvalidConfigurationException malformed config
     */
    public static DataStorage loadConfig(@NotNull File file) throws IOException, InvalidConfigurationException {
        if (!file.exists()) return null;

        YamlConfiguration config = new YamlConfiguration();
        config.load(file);

        return deserialize(config);
    }

    /**
     * Deserializes a DataStorage and saves it as a YamlConfig.
     * @param storage storage
     * @param file file path
     * @throws IOException file error
     */
    public static void saveConfig(@NotNull DataStorage storage, @NotNull File file) throws IOException {

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        YamlConfiguration config = serialize(storage);
        config.save(file);

    }

}
