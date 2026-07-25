package org.verdurae.placeholderplus;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.verdurae.placeholderplus.Command.PlaceholderPlusCommand;
import org.verdurae.placeholderplus.Object.PlayerData;
import org.verdurae.placeholderplus.Util.MathUtil;
import org.verdurae.placeholderplus.Util.PlayerUtil;
import org.verdurae.placeholderplus.Util.PluginUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.logging.Logger;

public final class PlaceholderPlus extends JavaPlugin {
    public static PlaceholderPlus instance;
    public static Logger logger;
    public static FileConfiguration config;
    public static boolean jsSupport = false;
    public static ArrayList<PlaceholderExpansion> expansions = new ArrayList<>();
    public static File dataFolder;
    public static File serverDataFolder;
    public static File playerDataFolder;
    public static boolean autosave;
    public static BukkitTask timer;
    public static int timeCount = 0;
    private static final String DEPENDENCY_URL = "https://repo1.maven.org/maven2/org/openjdk/nashorn/nashorn-core/15.2/nashorn-core-15.2.jar";
    private static final File LOCAL_JAR = new File("plugins/PlaceholderPlus/lib/nashorn-core.jar");


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
            logger.warning("准备开始自动下载nashorn....");
            loadDependencyIfMissing();
        }
        saveDefaultConfig();
        config = getConfig();
        autosave = config.getBoolean("Task.SaveTask.enable");
    }

    @Override
    public void onEnable() {
        logger.info("开始加载");
        dataFolder = getDataFolder();
        serverDataFolder = new File(dataFolder, "ServerData");
        playerDataFolder = new File(dataFolder, "PlayerData");
        serverDataFolder.mkdirs();
        playerDataFolder.mkdirs();
        PluginUtil.loadAllHolder();
        PluginUtil.loadAllPlayerData();
        PluginUtil.loadServerData();
        getCommand("pp").setExecutor(new PlaceholderPlusCommand());
        getCommand("pp").setTabCompleter(new PlaceholderPlusCommand());
        newTimer();
    }

    @Override
    public void onDisable() {
        logger.info("插件卸载");
        PluginUtil.saveAllPlayerData();
        PluginUtil.saveServerData();
        PluginUtil.unloadAllHolder();
    }

    public static void newTimer() {
        timer = Bukkit.getScheduler().runTaskTimerAsynchronously(PlaceholderPlus.instance, () -> {
            timeCount++;
            if (PlaceholderPlus.config.getBoolean("Task.SaveTask.enable") && timeCount % PlaceholderPlus.config.getInt("Task.SaveTask.period") == 0) {
                logger.info("正在自动保存数据");
                for (PlayerData playerData : PlayerUtil.playerData.values()) {
                    playerData.save();
                }
            }
            ConfigurationSection updates = PlaceholderPlus.config.getConfigurationSection("Placeholders.update");
            for (String update : updates.getKeys(false)) {
                if (timeCount % PlaceholderPlus.config.getInt("Placeholders.update." + update + ".period") == 0) {
                    for (PlayerData playerData : PlayerUtil.playerData.values()) {
                        double max = 0;
                        if (config.isDouble("Placeholders.update." + update + ".max")) {
                            max = config.getDouble("Placeholders.update." + update + ".max");
                        } else {
                            try {
                                max = Double.parseDouble(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(playerData.name), config.getString("Placeholders.update." + update + ".max")));
                            } catch (NumberFormatException e) {
                                max = MathUtil.calculate(PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(playerData.name), config.getString("Placeholders.update." + update + ".max"))).doubleValue();
                            }
                        }
                        if (playerData.data.getInt("update." + update) < max) {
                            FileConfiguration data = playerData.data;
                            data.set("update." + update, data.getDouble("update." + update) + PlaceholderPlus.config.getDouble("Placeholders.update." + update + ".amount"));
                        }
                    }
                }
            }
        }, 1, 20);
    }

    public static void loadDependencyIfMissing() {
        if (!LOCAL_JAR.exists()) {
            LOCAL_JAR.getParentFile().mkdirs();
            downloadFile(DEPENDENCY_URL, LOCAL_JAR);
        }
        addJarToClasspath(LOCAL_JAR.getAbsolutePath());
    }

    private static void downloadFile(String urlStr, File destination) {
        try (InputStream in = new URL(urlStr).openStream()) {
            ReadableByteChannel rbc = Channels.newChannel(in);
            try (FileOutputStream fos = new FileOutputStream(destination)) {
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addJarToClasspath(String jarPath) {
        try {
            URL url = new File(jarPath).toURI().toURL();
            URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(loader, url);
            System.out.println("成功将依赖添加到类路径: " + jarPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
