package org.verdurae.placeholderplus.API;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.verdurae.placeholderplus.Object.PlayerData;
import org.verdurae.placeholderplus.PlaceholderPlus;

import java.io.File;
import java.util.HashMap;

public class PlayerAPI {
    public static HashMap<String, PlayerData> playerData = new HashMap<>();

    public static FileConfiguration getPlayerData(String playername) {
        if (playerData.containsKey(playername)) return playerData.get(playername).data;
        YamlConfiguration data = YamlConfiguration.loadConfiguration(new File(PlaceholderPlus.dataFolder, playername + ".yml"));
        new PlayerData(playername, data);
        return data;
    }

    public static FileConfiguration getPlayerData(Player player) {
        return getPlayerData(player.getName());
    }
}
