package org.verdurae.placeholderplus.Util;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.verdurae.placeholderplus.API.Abstract.SubJsPlaceholder;
import org.verdurae.placeholderplus.Object.PlayerData;
import org.verdurae.placeholderplus.PlaceholderPlus;
import org.verdurae.placeholderplus.ThisPlaceholder;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

public class PluginUtil {
    public static FileConfiguration serverData = new YamlConfiguration();

    public static void saveServerData() {
        try {
            serverData.save(new File(PlaceholderPlus.serverDataFolder, "serverholder.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadServerData() {
        File file = new File(PlaceholderPlus.serverDataFolder, "serverholder.yml");
        if (!file.exists()) {
            try {
                serverData.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        serverData = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveAllPlayerData() {
        for (PlayerData playerData : PlayerUtil.playerData.values()) {
            playerData.save();
        }
    }

    public static void loadOnlinePlayerData() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerUtil.getPlayerData(player);
        }
    }

    public static void loadAllHolder() {
        PlaceholderPlus.expansions = new ArrayList<>();
        PlaceholderPlus.expansions.add(new ThisPlaceholder());
        File JsFolder = new File(PlaceholderPlus.dataFolder, "Js");
        JsFolder.mkdirs();
        for (File file : Objects.requireNonNull(JsFolder.listFiles())) {
            if (file.getName().endsWith(".js")) {
                try {
                    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
                    ScriptEngine engine = factory.getScriptEngine();
                    ConfigurationSection value = PlaceholderPlus.config.getConfigurationSection("JsImportPacket");
                    for (String key : value.getKeys(false)) {
                        engine.put(key, Class.forName(value.getString(key)));
                    }
                    InputStreamReader reader = new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
                    char[] buffer = new char[1024];
                    int read;
                    StringBuilder builder = new StringBuilder();
                    while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
                        builder.append(buffer, 0, read);
                    }
                    String scriptContent = builder.toString();
                    engine.eval(scriptContent);
                    String identifier = (String) engine.get("identifier");
                    String author = (String) engine.get("author");
                    String version = (String) engine.get("version");
                    PlaceholderPlus.expansions.add(new SubJsPlaceholder() {
                        @Override
                        public String getIdentifier() {
                            return identifier;
                        }

                        @Override
                        public String getAuthor() {
                            return author;
                        }

                        @Override
                        public String getVersion() {
                            return version;
                        }

                        @Override
                        public ScriptEngine getEngine() {
                            return engine;
                        }
                    });
                } catch (IOException | ScriptException e) {
                    System.err.println("Error processing file " + file.getAbsolutePath() + ": " + e.getMessage());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        for (PlaceholderExpansion expansion : new ArrayList<>(PlaceholderPlus.expansions)) {
            if (expansion.canRegister()) expansion.register();
        }
    }

    public static void unloadAllHolder() {
        for (PlaceholderExpansion expansion : new ArrayList<>(PlaceholderPlus.expansions)) {
            expansion.unregister();
        }
    }

    public static void loadAllPlayerData() {
        File[] files = PlaceholderPlus.playerDataFolder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (!file.getName().endsWith(".yml")) continue;
            String name = file.getName().replace(".yml", "");
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            new PlayerData(name, data);
        }
    }
}