package org.verdurae.placeholderplus.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.verdurae.placeholderplus.API.MathAPI;
import org.verdurae.placeholderplus.API.PlayerAPI;
import org.verdurae.placeholderplus.API.PluginAPI;
import org.verdurae.placeholderplus.PlaceholderPlus;

import java.util.concurrent.ConcurrentHashMap;

public class PlaceholderPlusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) {
            if (args.length == 1 && args[0].equals("reload")) {
                PlaceholderPlus.instance.reloadConfig();
                PlaceholderPlus.config = PlaceholderPlus.instance.getConfig();
                PluginAPI.saveAllPlayerData();
                PlayerAPI.playerData = new ConcurrentHashMap<>();
                PluginAPI.loadOnlinePlayerData();
                PlaceholderPlus.timer.cancel();
                PlaceholderPlus.newTimer();
                sender.sendMessage("已重载");
                return true;
            }
            sender.sendMessage("PlaceholderPlus：");
            sender.sendMessage("/pp 玩家名 set 变量名 数值");
            sender.sendMessage("/pp 玩家名 add 变量名 数值");
            sender.sendMessage("/pp 玩家名 remove 变量名");
            sender.sendMessage("/pp reload");
        } else {
            String holdername = args[2];
            String[] perms = holdername.split("_");
            switch (args[1]) {
                case "set":
                    PlayerAPI.getPlayerData(args[0]).set(perms[0] + "." + perms[1], args[3]);
                    break;
                case "add":
                    PlayerAPI.getPlayerData(args[0]).set(perms[0] + "." + perms[1], MathAPI.calculate(PlayerAPI.getPlayerData(args[0]).getDouble(perms[0] + "." + perms[1]), args[3]));
                    break;
                case "remove":
                    PlayerAPI.getPlayerData(args[0]).set(perms[0] + "." + perms[1], null);
                    break;
                default:
                    break;
            }
        }
        return false;
    }
}
