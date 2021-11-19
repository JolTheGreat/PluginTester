package com.github.jolthegreat.plugintester;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Listener implements org.bukkit.event.Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("Joined");
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        switch (event.getCurrentItem().getType()) {
            case GOLD_INGOT -> {

                Inventory slot = Bukkit.createInventory(player, 36, ChatColor.GOLD + "Slot Machine");
                ItemStack[] possible = {new ItemBuilder(Material.DIRT, )};
            }
            case BARRIER -> {
                player.closeInventory();
            }
        }
        event.setCancelled(true);


    }

}
