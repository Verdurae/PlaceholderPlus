package org.verdurae.placeholderplus.API.Abstract;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.verdurae.placeholderplus.PlaceholderPlus;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

public abstract class SubJsPlaceholder extends PlaceholderExpansion {
    public abstract ScriptEngine getEngine();

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        Invocable inv = (Invocable) getEngine();
        try {
            return inv.invokeFunction("onRequest", player, params).toString();
        } catch (NoSuchMethodException ignored) {
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean register() {
        if (PlaceholderPlus.jsSupport) {
            PlaceholderPlus.expansions.add(this);
            return super.register();
        }
        PlaceholderPlus.logger.warning("你的运行Java中没有JS引擎，" + getIdentifier() + "变量终止注册");
        return false;
    }
}