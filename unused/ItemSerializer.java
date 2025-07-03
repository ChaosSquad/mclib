package net.chaossquad.mclib;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class ItemSerializer {

    private ItemSerializer() {}

    // ----- SERIALIZE -----

    public static void serialize(@NotNull ItemStack itemStack, @NotNull ConfigurationSection section) {

        section.set("type", itemStack.getType().toString());
        section.set("amount", itemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return;

        Component customName = meta.customName();
        if (customName != null) {
            section.set("custom_name", MiniMessage.miniMessage().serialize(customName));
        }

        serializeLore(meta, section);
        serializeItemFlags(meta, section);
        serializeEnchantments(meta, section);

    }

    private static void serializeLore(@NotNull ItemMeta meta, @NotNull ConfigurationSection section) {

        List<Component> lore = meta.lore();
        if (lore == null) return;

        List<String> strLore = new ArrayList<>();
        for (Component component : lore) {
            strLore.add(MiniMessage.miniMessage().serialize(component));
        }

        section.set("lore", strLore);
    }

    private static void serializeItemFlags(@NotNull ItemMeta meta, @NotNull ConfigurationSection section) {

        List<String> flags = new ArrayList<>();

        for (ItemFlag flag : meta.getItemFlags()) {
            flags.add(flag.name());
        }

        section.set("item_flags", flags);

    }

    private static void serializeEnchantments(@NotNull ItemMeta meta, @NotNull ConfigurationSection section) {

        ConfigurationSection enchantmentsSection = new MemoryConfiguration();

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            ConfigurationSection enchantSection = new MemoryConfiguration();

            enchantSection.set("level", entry.getValue());

            enchantmentsSection.set(entry.getKey().getKey().getKey(), enchantmentsSection);
        }

        section.set("enchantments", enchantmentsSection);

    }

    private static void serializeAttributeModifiers(@NotNull ItemMeta meta, @NotNull ConfigurationSection section) {

        ConfigurationSection attributeModifiersSection = new MemoryConfiguration();

        for (meta.getAttributeModifiers().) {

        }

    }

    // ----- DESERIALIZE -----

    public static ItemStack deserialize(@NotNull ConfigurationSection section, @Nullable String pluginNamespace) {

        Material type = Material.getMaterial(section.getString("type", ""));
        if (type == null) {
            throw new IllegalArgumentException("ShopEntry: Invalid item type");
        }

        int amount = section.getInt("amount", 1);
        if (amount <= 0) amount = 1;

        ItemStack item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();

        String name = section.getString("custom_name");
        if (name != null) {
            meta.customName(MiniMessage.miniMessage().deserialize(name));
        }

        deserializeLore(section, meta);
        deserializeItemFlags(section, meta);
        deserializeEnchantments(section, meta);
        deserializeAttributeModifiers(section, meta);
        if (pluginNamespace != null) deserializePersistentDataContainer(section, meta, pluginNamespace);

        if (section.contains("custom_model_data")) {
            int customModelData = section.getInt("custom_model_data", -1);
            meta.setCustomModelData(customModelData >= 0 ? customModelData : null);
        }
        if (section.contains("glider")) meta.setGlider(section.getBoolean("glider"));
        if (section.contains("enchantable")) {
            int enchantable = section.getInt("enchantable", -1);
            meta.setEnchantable(enchantable > 0 ? enchantable : null);
        }
        if (section.contains("hide_tooltip")) {
            meta.setHideTooltip(section.getBoolean("hide_tooltip"));
        }
        if (section.contains("unbreakable")) meta.setUnbreakable(section.getBoolean("unbreakable"));

        return item;
    }

    private static void deserializeLore(@NotNull ConfigurationSection section, @NotNull ItemMeta meta) {

        List<String> lore = section.getStringList("lore");
        if (!lore.isEmpty()) {
            List<Component> ll = new ArrayList<>();

            for (String s : lore) {
                ll.add(MiniMessage.miniMessage().deserialize(s));
            }

            meta.lore(ll);
        }

    }

    private static void deserializeItemFlags(@NotNull ConfigurationSection section, @NotNull ItemMeta meta) {

        List<String> itemFlags = section.getStringList("item_flags");
        for (String s : itemFlags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(s));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ShopEntry: Invalid item flag: " + s);
            }
        }

    }

    private static void deserializeEnchantments(@NotNull ConfigurationSection section, @NotNull ItemMeta meta) {

        ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
        if (enchantments == null) return;

        for (String key : enchantments.getKeys(false)) {

            ConfigurationSection enchantSection = enchantments.getConfigurationSection(key);
            if (enchantSection == null) {
                throw new IllegalArgumentException("ShopEntry: Invalid enchantment section: " + key);
            }

            Enchantment enchantment = RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(NamespacedKey.minecraft(key));
            if (enchantment == null) {
                throw new IllegalArgumentException("ShopEntry: Enchantment not found: " + key);
            }

            int level = enchantSection.getInt("level");
            if (level <= 0) {
                throw new IllegalArgumentException("ShopEntry: Invalid enchantment level: " + level);
            }

            meta.addEnchant(enchantment, level, true);
        }

    }

    private static void deserializeAttributeModifiers(@NotNull ConfigurationSection section, @NotNull ItemMeta meta) {

        ConfigurationSection modifiers = section.getConfigurationSection("attribute_modifiers");
        if (modifiers == null) return;

        for (String key : modifiers.getKeys(false)) {

            ConfigurationSection attributeSection = modifiers.getConfigurationSection(key);
            if (attributeSection == null) {
                throw new IllegalArgumentException("ShopEntry: Invalid attribute modifier: " + key);
            }

            Attribute attribute = RegistryAccess.registryAccess().getRegistry(RegistryKey.ATTRIBUTE).get(NamespacedKey.minecraft(key));
            if (attribute == null) {
                throw new IllegalArgumentException("ShopEntry: Invalid attribute modifier: " + key);
            }

            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(attributeSection.getString("operation", ""));

            AttributeModifier modifier;

            if (attributeSection.contains("slot_group")) {
                EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.getByName(attributeSection.getString("slot_group", ""));
                if (equipmentSlotGroup == null) {
                    throw new IllegalArgumentException("ShopEntry: Invalid attribute modifier slot group: slot_group");
                }

                modifier = new AttributeModifier(attribute.getKey(), section.getDouble("value", 0), operation, equipmentSlotGroup);
            } else {
                modifier = new AttributeModifier(attribute.getKey(), section.getDouble("value", 0), operation);
            }

            meta.addAttributeModifier(attribute, modifier);
        }

    }

    private static void deserializePersistentDataContainer(@NotNull ConfigurationSection section, @NotNull ItemMeta meta, @NotNull String pluginNamespace) {

        ConfigurationSection pdcSection =  section.getConfigurationSection("persistent_data");
        if (pdcSection == null) return;

        for (String key : pdcSection.getKeys(false)) {

            ConfigurationSection entrySection = pdcSection.getConfigurationSection(key);
            if (entrySection == null) {
                throw new IllegalArgumentException("ShopEntry: Invalid persistent data entry: " + key);
            }

            String namespacedKeyKey = entrySection.getString("key");
            if (namespacedKeyKey == null) {
                throw new IllegalArgumentException("ShopEntry: Invalid persistent data key: " + key);
            }

            PersistentDataContainer pdc = meta.getPersistentDataContainer();

            NamespacedKey namespacedKey = new NamespacedKey(pluginNamespace, namespacedKeyKey);

            switch (Objects.requireNonNullElse(entrySection.getString("type"), "").toLowerCase()) {
                case "boolean" -> pdc.set(namespacedKey, PersistentDataType.BOOLEAN, entrySection.getBoolean("value", false));
                case "short" -> pdc.set(namespacedKey, PersistentDataType.SHORT, (short) entrySection.getInt("value", 0));
                case "int" -> pdc.set(namespacedKey, PersistentDataType.INTEGER, entrySection.getInt("value", 0));
                case "long" -> pdc.set(namespacedKey, PersistentDataType.LONG, entrySection.getLong("value", 0));
                case "float" -> pdc.set(namespacedKey, PersistentDataType.FLOAT, (float) entrySection.getDouble("value", 0));
                case "double" -> pdc.set(namespacedKey, PersistentDataType.DOUBLE, entrySection.getDouble("value", 0));
                case "string" -> pdc.set(namespacedKey, PersistentDataType.STRING, entrySection.getString("value", ""));
                default -> throw new IllegalArgumentException("ShopEntry: Invalid persistent data type: " + entrySection.getString("type"));
            }

        }

    }

}
