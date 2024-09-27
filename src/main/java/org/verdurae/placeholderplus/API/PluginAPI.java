package org.verdurae.placeholderplus.API;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.verdurae.placeholderplus.Object.PlayerData;

public class PluginAPI {
    public static void saveAllPlayerData() {
        for (PlayerData playerData : PlayerAPI.playerData.values()) {
            playerData.save();
        }
    }
    public static void loadOnlinePlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerAPI.getPlayerData(player);
        }
    }
}
