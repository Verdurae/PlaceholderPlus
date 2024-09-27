package org.verdurae.placeholderplus;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.verdurae.placeholderplus.API.PlayerAPI;

public class ThisPlaceholder extends PlaceholderExpansion {
    @Override
    public @Nullable("null") String onRequest(OfflinePlayer player, @NotNull String params) {
        String a = PlayerAPI.getPlayerData(player.getName()).getString(params);
        return (a == null) ? PlaceholderPlus.config.getString("Placeholders." + params) : a;
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
