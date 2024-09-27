package org.verdurae.placeholderplus.API.Abstract;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jetbrains.annotations.NotNull;
import org.verdurae.placeholderplus.PlaceholderPlus;

import javax.script.ScriptEngine;

public abstract class SubJsPlaceholder extends PlaceholderExpansion {
    public abstract @NotNull ScriptEngine getEngine();

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