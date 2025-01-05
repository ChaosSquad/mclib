package net.chaossquad.mclib.storage;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DSSerializer {

    public static YamlConfiguration serialize(@NotNull DataStorage storage) {
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<String, Object> entry : storage.entrySet()) {
            config.set(entry.getKey(), entry.getValue());
        }

        return config;
    }

    public static DataStorage deserialize(@NotNull YamlConfiguration config) {
        DataStorage storage = new DataStorage();

        for (String key : config.getKeys(true)) {
            Object value = config.get(key);
            if (value instanceof ConfigurationSection) continue;
            storage.set(key, value);
        }

        return storage;
    }

    public static DataStorage loadConfig(@NotNull File file) throws IOException, InvalidConfigurationException {
        if (!file.exists()) return null;

        YamlConfiguration config = new YamlConfiguration();
        config.load(file);

        return deserialize(config);
    }

    public static void saveConfig(@NotNull DataStorage storage, @NotNull File file) throws IOException {

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        YamlConfiguration config = serialize(storage);
        config.save(file);

    }

}
