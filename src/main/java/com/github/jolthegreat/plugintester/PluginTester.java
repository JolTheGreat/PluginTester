package com.github.jolthegreat.plugintester;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PluginTester extends JavaPlugin {
    public static PluginTester INSTANCE;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new SlotMachine(), this);
        Objects.requireNonNull(this.getCommand("gui")).setExecutor(new CommandExecutor());
        INSTANCE = this;

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin shutting down");
    }

}
