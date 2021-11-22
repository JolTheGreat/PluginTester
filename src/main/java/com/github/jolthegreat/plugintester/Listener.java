package com.github.jolthegreat.plugintester;

import com.github.jolthegreat.plugintester.containers.CasinoItemContainer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

interface StopButtonPress {
    void pressed(int column);
}

public class Listener implements org.bukkit.event.Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("Joined");
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine")) {
            switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
                case GOLD_INGOT -> {
                    player.closeInventory();
                    final List<ItemStack> possible = CasinoItemContainer.getAll();
                    if (possible.size() == 9) {
                        Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.GOLD + "Slot Machine");

                        final ItemStack around = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                        ItemMeta aroundMeta = around.getItemMeta();
                        assert aroundMeta != null;
                        aroundMeta.setDisplayName(ChatColor.RESET + "");
                        final int[] aroundPattern = {0,1,2,3,4,9,13,18,22,27,31,36,37,38,39,40};
                        for (int i : aroundPattern) {
                            inventory.setItem(i, around);
                        }

                        final int[] possiblePattern = {10,11,12,19,20,21,28,29,30};
                        for (int i=0; i<possible.size(); i++) {
                            inventory.setItem(possiblePattern[i], possible.get(i));
                        }


                        final int[] stopButtonPattern = {46,47,48};

                        for (int j : stopButtonPattern) {
                            ItemStack stopButton = new ItemStack(Material.WARPED_BUTTON);
                            ItemMeta stopButtonMeta = stopButton.getItemMeta();
                            assert stopButtonMeta != null;
                            stopButtonMeta.setDisplayName(ChatColor.RED + "Stop slot " + (j-45));
                            stopButton.setItemMeta(stopButtonMeta);
                            inventory.setItem(j, stopButton);
                        }
                        player.openInventory(inventory);

                        BukkitTask taskOne = Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                            ItemStack newSlot = CasinoItemContainer.getRandom();
                            inventory.setItem(28, inventory.getItem(19));
                            inventory.setItem(19, inventory.getItem(10));
                            inventory.setItem(10, newSlot);
                            System.out.println("Cycle1");
                        }, 0, 10);

                        BukkitTask taskTwo = Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                            ItemStack newSlot = CasinoItemContainer.getRandom();
                            inventory.setItem(29, inventory.getItem(20));
                            inventory.setItem(20, inventory.getItem(11));
                            inventory.setItem(11, newSlot);
                            System.out.println("Cycle2");
                        }, 0, 10);
                        BukkitTask taskThree = Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                            ItemStack newSlot = CasinoItemContainer.getRandom();
                            inventory.setItem(30, inventory.getItem(21));
                            inventory.setItem(21, inventory.getItem(12));
                            inventory.setItem(12, newSlot);
                            System.out.println("Cycle3");
                        }, 0, 10);


                        ButtonPress.addListener((column) -> {
                            switch (column) {
                                case 1 -> taskOne.cancel();
                                case 2 -> taskTwo.cancel();
                                case 3 -> taskThree.cancel();
                                default -> {
                                    taskOne.cancel();
                                    taskTwo.cancel();
                                    taskThree.cancel();
                                }
                            }
                        });

                    } else {
                        System.out.println(Color.RED + "you can only have 9 items in casino");
                    }

                }
                case BARRIER -> player.closeInventory();

                case WARPED_BUTTON -> {
                    final String displayName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                    if (displayName.contains("Stop slot")) {
                        System.out.println("Stop");
                        ButtonPress.buttonPressTrigger(Integer.parseInt(displayName.split(" ")[2]));
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        System.out.println("inventory close");
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine")) {
            System.out.println("Attempting the interrupt");
            ButtonPress.buttonPressTrigger(-1);
        }
    }
}

class ButtonPress {
    static List<StopButtonPress> listeners = new ArrayList<>();

    public static void addListener(StopButtonPress press) {
        System.out.println(press);
        listeners.add(press);
    }

    public static void buttonPressTrigger(int column) {
        listeners.forEach((listener) -> listener.pressed(column));
    }
}
