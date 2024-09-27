package org.verdurae.placeholderplus.API;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
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
import java.util.Map;
import java.util.Objects;

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

    public static void loadAllHolder() {
        PlaceholderPlus.expansions.add(new ThisPlaceholder());
        File JsFolder = new File(PlaceholderPlus.instance.getDataFolder(), "Js");
        JsFolder.mkdirs();
        for (File file : Objects.requireNonNull(JsFolder.listFiles())) {
            if (file.getName().endsWith(".js")) {
                try {
                    NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
                    ScriptEngine engine = factory.getScriptEngine();
                    Map<String, Object> value = PlaceholderPlus.config.getValues(true);
                    for (String key : value.keySet()) {
                        if (key.startsWith("JsImportPacket.")) {
                            engine.put(key.replace("JsImportPacket.", ""), Class.forName(key));
                        }
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
                        public @NotNull String getIdentifier() {
                            return identifier;
                        }

                        @Override
                        public @NotNull String getAuthor() {
                            return author;
                        }

                        @Override
                        public @NotNull String getVersion() {
                            return version;
                        }

                        @Override
                        public @NotNull ScriptEngine getEngine() {
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
        for (PlaceholderExpansion expansion : PlaceholderPlus.expansions) {
            if (expansion.canRegister()) expansion.register();
        }
    }

    public static void unloadAllHolder() {
        for (PlaceholderExpansion expansion : PlaceholderPlus.expansions) {
            expansion.unregister();
        }
    }
}
