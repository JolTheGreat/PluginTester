package com.github.jolthegreat.plugintester;

import org.bukkit.entity.Player;

public class ChatUtil {
    public static void success(Player player, String message) {
        player.sendMessage(message);
    }

    public static void error(Player player, String message) {
        player.sendMessage(message);
    }

    public static void warning(Player player, String message) {
        player.sendMessage(message);
    }
}
