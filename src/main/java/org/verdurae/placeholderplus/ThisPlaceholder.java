package org.verdurae.placeholderplus;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.verdurae.placeholderplus.Util.PlayerAPI;

public class ThisPlaceholder extends PlaceholderExpansion {
    @Override
    public @Nullable("null") String onRequest(OfflinePlayer player, @NotNull String param) {
        String[] params = param.split("_");
        if (params.length < 2) {
            params = new String[]{"normal", params[0]};
        }
        if (params[0].equals("normal")) {
            String a = PlayerAPI.getPlayerData(player.getName()).getString("normal." + params[1]);
            return (a == null) ? PlaceholderPlus.config.getString("Placeholders.normal." + params[1]) : a;
        } else if (params[0].equals("update")) {
            String a = PlayerAPI.getPlayerData(player.getName()).getString("update." + params[1]);
            return (a == null) ? PlaceholderPlus.config.getString("Placeholders.update." + params[1] + ".max") : a;
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "pp";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Kaminy";
    }

    @Override
    public @NotNull String getVersion() {
        return PlaceholderPlus.instance.getDescription().getVersion();
    }
}
