package net.chaossquad.mclib;

import com.google.common.collect.Multimap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utilities for json configurations.
 */
public final class JSONConfigUtils {

    private JSONConfigUtils() {}

    /**
     * Converts a JSONObject to a location.
     * @param json location json
     * @return location or null if invalid
     */
    public static Location jsonObjectToLocation(JSONObject json) {

        try {

            Location location = new Location(null, json.getDouble("x"), json.getDouble("y"), json.getDouble("z"));

            if (json.has("yaw")) location.setYaw(json.getFloat("yaw"));
            if (json.has("pitch")) location.setPitch(json.getFloat("pitch"));

            return location;

        } catch (JSONException e) {
            return null;
        }

    }

    /**
     * Converts a location to a JSONObject.
     * @param location location
     * @return location json
     */
    public static JSONObject locationToJSONObject(Location location) {
        JSONObject json = new JSONObject();

        json.put("x", location.getX());
        json.put("y", location.getY());
        json.put("z", location.getZ());

        json.put("yaw", location.getYaw());
        json.put("pitch", location.getPitch());

        if (location.getWorld() != null) json.put("world", location.getWorld().getName());

        return json;
    }

    /**
     * Converts a JSONArray of location json into a list of locations.
     * @param json json array of locations
     * @return list of Locations
     */
    public static List<Location> jsonLocationArrayToLocationList(JSONArray json) {
        List<Location> locations = new ArrayList<>();

        for (Object object : json) {
            if (!(object instanceof JSONObject locData)) continue;

            Location location = jsonObjectToLocation(locData);
            if (location == null) continue;

            locations.add(location);
        }

        return List.copyOf(locations);
    }

    /**
     * Converts a list of Locations into a JSONArray of location json
     * @param locations list of Locations
     * @return json array of location json
     */
    public static JSONArray locationListToJSONArray(List<Location> locations) {
        JSONArray json = new JSONArray();

        for (Location location : locations) {
            json.put(locationToJSONObject(location));
        }

        return json;
    }

    // ITEM META SERIALIZATION

    /**
     * Serializes a namespaced key into json.
     * @param key namespaced key
     * @return json
     */
    public JSONObject serializeNamespacedKey(NamespacedKey key) {
        JSONObject data = new JSONObject();

        data.put("namespace", key.getNamespace());
        data.put("key", key.getKey());

        return data;
    }

    /**
     * Deserializes json into a namespaced key.
     * @param data json
     * @return namespaced key
     */
    public NamespacedKey deserializeNamespacedKey(JSONObject data) {
        return new NamespacedKey(data.getString("namespace"), data.getString("key"));
    }

    // PERSISTENT DATA CONTAINER SERIALIZATION

    /**
     * Serializes some data of the persistent data container to json.
     * @param container persistent data container
     * @return json
     */
    public JSONArray serializePersistentDataContainer(PersistentDataContainer container) {
        JSONArray data = new JSONArray();

        for (NamespacedKey key : container.getKeys()) {

            if (container.has(key, PersistentDataType.BOOLEAN)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "BOOLEAN");
                element.put("value", container.get(key, PersistentDataType.BOOLEAN));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.BYTE)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "BYTE");
                element.put("value", container.get(key, PersistentDataType.BYTE));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.DOUBLE)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "DOUBLE");
                element.put("value", container.get(key, PersistentDataType.DOUBLE));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.FLOAT)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "FLOAT");
                element.put("value", container.get(key, PersistentDataType.FLOAT));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.INTEGER)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "INTEGER");
                element.put("value", container.get(key, PersistentDataType.INTEGER));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.LONG)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "LONG");
                element.put("value", container.get(key, PersistentDataType.LONG));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.SHORT)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "SHORT");
                element.put("value", container.get(key, PersistentDataType.SHORT));
                data.put(element);
            }

            if (container.has(key, PersistentDataType.STRING)) {
                JSONObject element = new JSONObject();
                element.put("key", serializeNamespacedKey(key));
                element.put("type", "STRING");
                element.put("value", container.get(key, PersistentDataType.STRING));
                data.put(element);
            }

        }

        return data;
    }

    /**
     * Deserializes some data from json into the persistent data container.
     * @param data json
     * @param target target container
     */
    public void deserializePersistentDataContainer(JSONArray data, PersistentDataContainer target) {

        for (JSONObject container : data.toList().stream().filter(object -> object instanceof JSONObject).map(object -> (JSONObject) object).toList()) {

            switch (container.optString("type")) {

                case "BOOLEAN" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.BOOLEAN, container.getBoolean("value"));
                case "BYTE" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.BYTE, (byte) container.getInt("value"));
                case "DOUBLE" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.DOUBLE, container.getDouble("value"));
                case "FLOAT" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.FLOAT, container.getFloat("value"));
                case "INTEGER" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.INTEGER, container.getInt("value"));
                case "LONG" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.LONG, container.getLong("value"));
                case "SHORT" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.SHORT, (short) container.getInt("value"));
                case "STRING" -> target.set(deserializeNamespacedKey(container.optJSONObject("key")), PersistentDataType.STRING, container.getString("value"));

            }

        }

    }

    // ITEM META SERIALIZATION

    /**
     * Serializes damageable part of item meta into json.
     * @param damageable damageable item meta
     * @param data existing json
     */
    private void serializeDamageableItemMeta(Damageable damageable, JSONObject data) {
        data.put("damage", damageable.getDamage());
        data.put("max_damage", damageable.getMaxDamage());
    }

    /**
     * Deserializes json into damageable part of item meta.
     * @param data json
     * @param damageable target damageable item meta
     */
    private void deserializeDamageableItemMeta(JSONObject data, Damageable damageable) {
        if (data.has("damage")) damageable.setDamage(data.getInt("damage"));
        if (data.has("max_damage")) damageable.setMaxDamage(data.getInt("max_damage"));
    }

    /**
     * Serializes an item meta to json
     * @param meta item meta
     * @return json
     */
    public JSONObject serializeItemMeta(ItemMeta meta) {
        JSONObject data = new JSONObject();

        // Display Name

        if (!meta.getDisplayName().isEmpty()) {
            data.put("displayName", meta.getDisplayName());
        }

        // Lore

        if (meta.getLore() != null) {
            JSONArray lore = new JSONArray();
            for (String line : meta.getLore()) {
                lore.put(line);
            }
            data.put("lore", lore);
        }

        // Enchantments

        if (!meta.getEnchants().isEmpty()) {
            JSONArray enchantments = new JSONArray();
            for (Map.Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet()) {
                JSONObject enchantData = new JSONObject();
                enchantData.put("key", serializeNamespacedKey(enchantment.getKey().getKey()));
                enchantData.put("level", enchantment.getValue());
                enchantments.put(enchantData);
            }
            data.put("enchantments", enchantments);
        }

        // Item Flags

        if (!meta.getItemFlags().isEmpty()) {
            JSONArray itemFlags = new JSONArray();
            for (ItemFlag flag : meta.getItemFlags()) {
                itemFlags.put(flag.name());
            }
            data.put("item_flags", itemFlags);
        }

        // Attribute Modifiers

        if (meta.getAttributeModifiers() != null || !meta.getAttributeModifiers().isEmpty()) {
            JSONArray attributeModifiers = new JSONArray();
            for (Attribute attribute : meta.getAttributeModifiers().keySet()) {
                for (AttributeModifier modifier : meta.getAttributeModifiers().get(attribute)) {

                    JSONObject modifierData = new JSONObject();
                    modifierData.put("attribute_key", attribute.getKey().getKey());
                    modifierData.put("modifier_key", modifier.getKey().getKey());
                    modifierData.put("modifier_amount", modifier.getAmount());
                    modifierData.put("modifier_operation", modifier.getOperation().name());
                    modifierData.put("modifier_equipment_slot_group", modifier.getSlotGroup().toString());
                    attributeModifiers.put(modifierData);

                }
            }
            data.put("attribute_modifiers", attributeModifiers);
        }

        // Custom Model Data

        if (meta.hasCustomModelData()) {
            data.put("custom_model_data", meta.getCustomModelData());
        }

        // Persistent data container

        if (!meta.getPersistentDataContainer().isEmpty()) {
            data.put("persistent_data_container", serializePersistentDataContainer(meta.getPersistentDataContainer()));
        }

        // Subclasses

        if (meta instanceof Damageable damageable) {
            serializeDamageableItemMeta(damageable, data);
        }

        // Return

        return data;
    }

    /**
     * Deserializes json into an existing item meta.
     * @param data json
     * @param target target item meta
     */
    public void deserializeItemMeta(JSONObject data, ItemMeta target) {

        // Display Name

        String displayName = data.optString("displayName");
        if (displayName != null) target.setDisplayName(displayName);

        // Lore

        JSONArray loreData = data.optJSONArray("lore");
        if (loreData != null) {
            List<String> lore = new ArrayList<>();
            for (int i = 0; i < loreData.length(); i++) {
                String line = loreData.optString(i);
                if (line == null) continue;
                lore.add(line);
            }
            target.setLore(lore);
        }

        // Enchantments

        JSONArray enchantments = data.optJSONArray("enchantments");
        if (enchantments != null) {
            for (int i = 0; i < enchantments.length(); i++) {
                JSONObject enchantmentData = enchantments.optJSONObject(i);
                if (enchantmentData == null) continue;
                NamespacedKey key = deserializeNamespacedKey(enchantmentData.getJSONObject("key"));
                Enchantment enchantment = Registry.ENCHANTMENT.get(key);
                if (enchantment == null) continue;
                target.addEnchant(enchantment, enchantmentData.optInt("level"), true);
            }
        }

        // Item Flags

        JSONArray itemFlags = data.optJSONArray("item_flags");
        if (itemFlags != null) {
            for (int i = 0; i < itemFlags.length(); i++) {
                String flagName = itemFlags.optString(i);
                if (flagName == null) continue;
                ItemFlag flag;
                try {
                    flag = ItemFlag.valueOf(flagName);
                } catch (IllegalArgumentException e) {
                    continue;
                }
                target.addItemFlags(flag);
            }
        }

        // Attribute Modifiers

        JSONArray attributeModifiers = data.optJSONArray("attribute_modifiers");
        if (attributeModifiers != null) {
            for (int i = 0; i < attributeModifiers.length(); i++) {
                JSONObject modifierData = attributeModifiers.optJSONObject(i);
                if (modifierData == null) continue;

                Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(modifierData.getString("attribute_key")));
                if (attribute == null) continue;

                AttributeModifier.Operation operation;
                try {
                    operation = AttributeModifier.Operation.valueOf(modifierData.getString("modifier_operation"));
                } catch (IllegalArgumentException e) {
                    continue;
                }

                EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.getByName(modifierData.getString("modifier_equipment_slot_group"));
                if (equipmentSlotGroup == null) continue;

                AttributeModifier modifier = new AttributeModifier(NamespacedKey.minecraft(modifierData.getString("modifier_key")), modifierData.getDouble("modifier_amount"), operation, equipmentSlotGroup);
                target.addAttributeModifier(attribute, modifier);
            }
        }

        // Custom Model Data

        int customModelData = data.optInt("custom_model_data", -1);
        if (customModelData >= 0) {
            target.setCustomModelData(customModelData);
        }

        // Persistent Data Container

        JSONArray persistentDataContainer = data.optJSONArray("persistent_data_container");
        if (persistentDataContainer != null) {
            deserializePersistentDataContainer(persistentDataContainer, target.getPersistentDataContainer());
        }

        // Subclasses

        if (target instanceof Damageable damageable) {
            deserializeDamageableItemMeta(data, damageable);
        }

    }

    // ITEM SERIALIZATION

    /**
     * Serializes an item into json.
     * @param item item stack
     * @return json
     */
    public JSONObject serializeItemToJSON(ItemStack item) {
        JSONObject data = new JSONObject();

        data.put("type", item.getType());
        data.put("amount", item.getAmount());
        data.put("meta", serializeItemMeta(item.getItemMeta()));

        return data;
    }

    /**
     * Deserializes json into an item.
     * @param data json
     * @return item stack
     */
    public ItemStack deserializeItemFromJSON(JSONObject data) {
        Material material = Material.getMaterial(data.getString("type"));
        if (material == null) return null;

        ItemStack item = new ItemStack(material);
        item.setAmount(data.optInt("amount", 1));

        ItemMeta meta = item.getItemMeta();
        deserializeItemMeta(data, meta);
        item.setItemMeta(meta);

        return item;
    }

}
