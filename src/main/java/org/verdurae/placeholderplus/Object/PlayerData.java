package org.verdurae.placeholderplus.Object;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.verdurae.placeholderplus.Util.PlayerUtil;
import org.verdurae.placeholderplus.PlaceholderPlus;

import java.io.IOException;

import static org.verdurae.placeholderplus.PlaceholderPlus.config;

public class PlayerData {
    public String name;
    public FileConfiguration data;

    public PlayerData(String name, FileConfiguration data) {
        this.name = name;
        if (data == null) data = new YamlConfiguration();
        this.data = defaultData(data);
        save();
        PlayerUtil.playerData.put(name, this);
    }

    public FileConfiguration defaultData(FileConfiguration data) {
        for (String key : config.getStringList("Placeholders.normal")) {
            if (!data.contains("normal." + key)) {
                data.set("normal." + key, config.get("Placeholders.normal." + key));
            }
        }
        for (String key : config.getStringList("Placeholders.update")) {
            if (!data.contains("update." + key)) {
                data.set("update." + key, config.get("Placeholders.update." + key + ".max"));
            }
        }
        return data;
    }

    public void save() {
        try {
            data.save(PlaceholderPlus.playerDataFolder + "/" + name + ".yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
