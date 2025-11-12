package org.verdurae.placeholderplus.API;

import org.verdurae.placeholderplus.Util.PlayerAPI;

/**
 * @author Kaminy
 * @date 2025/11/12 22:39
 * @since 1.0
 */
public class PlaceholderPlusApi {
    /**
     * 获取玩家占位符对应的数据值
     *
     * @param player      玩家名
     * @param placeholder 占位符名称，格式为"xxx_yyy"，不需要带%
     * @return 对应的玩家插件变量值
     */
    public static String getPlaceholder(String player, String placeholder) {
        String[] perms = placeholder.split("_");
        return PlayerAPI.getPlayerData(player).getString(perms[0] + "." + perms[1]);
    }

    /**
     * 设置玩家数据中的占位符
     *
     * @param player      玩家名
     * @param placeholder 占位符名称，格式为"xxx_yyy"，不需要带%
     * @param value       要设置的值
     */
    public static void setPlaceholder(String player, String placeholder, String value) {
        String[] perms = placeholder.split("_");
        PlayerAPI.getPlayerData(player).set(perms[0] + "." + perms[1], value);
    }
}
