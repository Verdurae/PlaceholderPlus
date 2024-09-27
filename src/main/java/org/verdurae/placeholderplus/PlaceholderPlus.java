package org.verdurae.placeholderplus;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.verdurae.placeholderplus.API.PlayerAPI;
import org.verdurae.placeholderplus.API.PluginAPI;
import org.verdurae.placeholderplus.Command.PlaceholderPlusCommand;
import org.verdurae.placeholderplus.Object.PlayerData;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public final class PlaceholderPlus extends JavaPlugin {
    public static PlaceholderPlus instance;
    public static Logger logger;
    public static FileConfiguration config;
    public static boolean jsSupport = false;
    public static ArrayList<PlaceholderExpansion> expansions = new ArrayList<>();
    public static File dataFolder;
    public static boolean autosave = true;

    @Override
    public void onLoad() {
        instance = this;
        logger = getLogger();
        logger.info("准备加载");
        logger.info("作者：Kaminy");
        logger.info("这个插件是免费开源的，如果你花了钱，请去骂他");
        try {
            Class.forName("jdk.nashorn.api.scripting.NashornScriptEngine");
            jsSupport = true;
        } catch (ClassNotFoundException e) {
            logger.warning("你的运行Java中没有JS引擎，无法使用JS变量功能噢");
            logger.warning("解决办法：安装一个带有nashornJs引擎的插件或模组/使用Java15-的版本/催更我内置一个JS引擎");
            logger.warning("状态决定类：jdk.nashorn.api.scripting.NashornScriptEngine");
        }
        saveDefaultConfig();
        config = getConfig();
    }

    @Override
    public void onEnable() {
        logger.info("开始加载");
        dataFolder = new File(getDataFolder(), "PlayerData");
        dataFolder.mkdirs();
        expansions.add(new ThisPlaceholder());
        for (PlaceholderExpansion expansion : expansions) {
            if (expansion.canRegister()) expansion.register();
        }
        PluginAPI.loadOnlinePlayerData();
        getCommand("pp").setExecutor(new PlaceholderPlusCommand());
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            while (isEnabled() && autosave) {
                try {
                    Thread.sleep(300000);
                    logger.info("正在自动保存数据");
                    for (PlayerData playerData : PlayerAPI.playerData.values()) {
                        playerData.save();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            autosave = true;
        });
    }

    @Override
    public void onDisable() {
        logger.info("插件卸载");
        PluginAPI.saveAllPlayerData();
        for (PlaceholderExpansion expansion : expansions) {
            expansion.unregister();
        }
    }
}
