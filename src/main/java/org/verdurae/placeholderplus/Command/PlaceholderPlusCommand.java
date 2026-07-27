package org.verdurae.placeholderplus.Command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.verdurae.placeholderplus.PlaceholderPlus;
import org.verdurae.placeholderplus.ThisPlaceholder;
import org.verdurae.placeholderplus.Util.MathUtil;
import org.verdurae.placeholderplus.Util.PlayerUtil;
import org.verdurae.placeholderplus.Util.PluginUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceholderPlusCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) {
            if (args.length == 1 && args[0].equals("reload")) {
                PlaceholderPlus.instance.reloadConfig();
                PlaceholderPlus.config = PlaceholderPlus.instance.getConfig();
                PluginUtil.saveAllPlayerData();
                PluginUtil.saveServerData();
                PlayerUtil.playerData = new ConcurrentHashMap<>();
                PluginUtil.serverData = new YamlConfiguration();
                PluginUtil.loadAllPlayerData();
                PluginUtil.loadServerData();
                PluginUtil.loadAllHolder();
                PlaceholderPlus.timer.cancel();
                PlaceholderPlus.newTimer();
                sender.sendMessage("已重载");
                return true;
            }
            sender.sendMessage("PlaceholderPlus：<>是必填 []是选填");
            sender.sendMessage("/pp 玩家名 set <变量名> <数值> [参数...]");
            sender.sendMessage("/pp 玩家名 add <变量名> <数值> [参数...]");
            sender.sendMessage("/pp 玩家名 remove <变量名> [参数...]");
            sender.sendMessage("/pp reload");
            return true;
        } else {
            try {

                String holdername = args[2];
                String[] perms = holdername.split("_");
                if (perms.length < 1) {
                    sender.sendMessage("请输入正确的变量名");
                    return false;
                }
                if (!ThisPlaceholder.placeholderTypes.contains(perms[0])) {
                    perms = new String[]{"normal", perms[0]};
                }
                boolean message = PlaceholderPlus.config.getBoolean("Setting.message", true);
                for (String arg : args) {
                    if (arg.startsWith("m:")) {
                        message = Boolean.parseBoolean(arg.substring(2));
                    }
                }
                if (perms[0].equals("server")) {
                    switch (args[1]) {
                        case "set":
                            PluginUtil.serverData.set(perms[1], args[3]);
                            if (message) {
                                sender.sendMessage("将服务器的" + holdername + "变量设置为" + args[3]);
                            }
                            break;
                        case "add":
                            Number number = MathUtil.calculate(PlayerUtil.getPlayerData(args[0]).getDouble(perms[0] + "." + perms[1]), args[3]);
                            PluginUtil.serverData.set(perms[1], number);
                            if (message) {
                                sender.sendMessage("将服务器的" + holdername + "变量设置为" + number);
                            }
                            break;
                        case "remove":
                            PluginUtil.serverData.set(perms[1], null);
                            if (message) {
                                sender.sendMessage("将服务器的" + holdername + "变量移除（解析将会获得默认值）");
                            }
                            break;
                        default:
                            break;
                    }
                    return true;
                }
                switch (args[1]) {
                    case "set":
                        PlayerUtil.getPlayerData(args[0]).set(perms[0] + "." + perms[1], args[3]);
                        if (message) {
                            sender.sendMessage("将" + args[0] + "的" + holdername + "变量设置为" + args[3]);
                        }
                        break;
                    case "add":
                        Number number = MathUtil.calculate(PlayerUtil.getPlayerData(args[0]).getDouble(perms[0] + "." + perms[1]), args[3]);
                        PlayerUtil.getPlayerData(args[0]).set(perms[0] + "." + perms[1], number);
                        if (message) {
                            sender.sendMessage("将" + args[0] + "的" + holdername + "变量设置为" + number);
                        }
                        break;
                    case "remove":
                        PlayerUtil.getPlayerData(args[0]).set(perms[0] + "." + perms[1], null);
                        if (message) {
                            sender.sendMessage("将" + args[0] + "的" + holdername + "变量记录移除（解析将会获得默认值）");
                        }
                        break;
                    default:
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                sender.sendMessage("参数不足");
            }
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();

        if (args.length == 1) {
            Bukkit.getOnlinePlayers().forEach(player -> list1.add(player.getName()));
        } else if (args.length == 2) {
            list1.add("set");
            list1.add("add");
            list1.add("remove");

        } else if (args.length == 3) {
            String arg = args[2];
            if (arg.startsWith("normal_")) {
                FileConfiguration playerData = PlayerUtil.getPlayerData(args[0]);
                if (playerData.getConfigurationSection("normal") != null) {
                    for (String key : playerData.getConfigurationSection("normal").getKeys(false)) {
                        list1.add("normal_" + key);
                    }
                } else {
                    list1.add("变量名");
                }
            } else if (arg.startsWith("update_")) {
                FileConfiguration playerData = PlayerUtil.getPlayerData(args[0]);
                if (playerData.getConfigurationSection("update") != null) {
                    for (String key : playerData.getConfigurationSection("update").getKeys(false)) {
                        list1.add("update_" + key);
                    }
                } else {
                    list1.add("变量名");
                }
            } else if (arg.startsWith("server_")) {
                FileConfiguration serverData = PluginUtil.serverData;
                if (serverData.getKeys(false).isEmpty()) {
                    list1.add("变量名");
                } else {
                    for (String key : serverData.getKeys(false)) {
                        list1.add("server_" + key);
                    }
                }
            } else {
                list1.add("normal_");
                list1.add("update_");
                list1.add("server_");
            }
        }
        for (String param : list1) {
            if (param.startsWith(args[args.length - 1])) {
                list.add(param);
            }
        }
        return list;
    }
}