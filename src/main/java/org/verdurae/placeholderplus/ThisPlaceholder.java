package org.verdurae.placeholderplus;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.verdurae.placeholderplus.Util.PlayerUtil;
import org.verdurae.placeholderplus.Util.PluginUtil;

import java.util.Arrays;
import java.util.List;

public class ThisPlaceholder extends PlaceholderExpansion {
    public final static List<String> placeholderTypes = Arrays.asList("normal", "update", "server");

    @Override
    public String onRequest(OfflinePlayer player, String param) {
        String[] params = param.split("_");
        String defaultValue = "未定义的变量";
        for (String arg : params) {
            if (arg.startsWith("d:")) {
                defaultValue = arg.replaceFirst("d:", "");
            }
        }
        if (!placeholderTypes.contains(params[0])) {
            params = new String[]{"normal", params[0]};
        }
        switch (params[0]) {
            case "normal": {
                String a = PlayerUtil.getPlayerData(player.getName()).getString("normal." + params[1]);
                if (a == null) {
                    a = PlaceholderPlus.config.getString("Placeholders.normal." + params[1]);
                    if (a == null) {
                        a = defaultValue;
                    }
                }
                return a;
            }
            case "update": {
                String a = PlayerUtil.getPlayerData(player.getName()).getString("update." + params[1]);
                if (a == null) {
                    a = PlaceholderPlus.config.getString("Placeholders.update." + params[1] + ".max");
                    if (a == null) {
                        a = defaultValue;
                    }
                }
                return a;
            }
            case "server": {
                String a = PluginUtil.serverData.getString(params[1]);
                if (a == null) {
                    a = PlaceholderPlus.config.getString("Placeholders.server." + params[1]);
                    if (a == null) {
                        a = defaultValue;
                    }
                }
                return a;
            }
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return "pp";
    }

    @Override
    public String getAuthor() {
        return "Kaminy";
    }

    @Override
    public String getVersion() {
        return PlaceholderPlus.instance.getDescription().getVersion();
    }
}
