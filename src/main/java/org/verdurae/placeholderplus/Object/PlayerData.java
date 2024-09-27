package org.verdurae.placeholderplus.Object;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.verdurae.placeholderplus.API.PlayerAPI;
import org.verdurae.placeholderplus.PlaceholderPlus;

import java.io.IOException;
import java.util.Map;

public class PlayerData {
    public String name;
    public FileConfiguration data;

    public PlayerData(String name, FileConfiguration data) {
        this.name = name;
        if (data == null) data = new YamlConfiguration();
        this.data = defaultData(data);
        save();
        PlayerAPI.playerData.put(name, this);
    }

    public FileConfiguration defaultData(FileConfiguration data) {
        Map<String, Object> value = PlaceholderPlus.config.getValues(true);
        for (String key : value.keySet()) {
            if (key.startsWith("Placeholders.")) {
                if (!data.contains(key)) data.set(key.replace("Placeholders.", ""), value.get(key));
            }
        }
        return data;
    }

    public void save() {
        try {
            data.save(PlaceholderPlus.dataFolder + "/" + name + ".yml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
