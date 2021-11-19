package com.github.jolthegreat.plugintester;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public final class PluginTester extends JavaPlugin {
    public static String test = "hi";

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Listener(), this);
        Objects.requireNonNull(this.getCommand("gui")).setExecutor(new CommandExecutor());
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin shutting down");
    }

}
