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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
interface StopButtonPress {
    void pressed(int column);
}
public class Listener implements org.bukkit.event.Listener {



    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("Joined");
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) throws InterruptedException {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase("Slot Machine")) {
            switch (Objects.requireNonNull(event.getCurrentItem()).getType()) {
                case GOLD_INGOT -> {
                    player.closeInventory();
                    Inventory slot = Bukkit.createInventory(player, 54, ChatColor.GOLD + "Slot Machine");
                    ItemStack empty = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                    ItemMeta meta = empty.getItemMeta();
                    assert meta != null;
                    meta.setDisplayName(ChatColor.RESET + "");
                    meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                    empty.setItemMeta(meta);
                    final int[] emptyPattern = {0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 37, 38, 39, 40};
                    for (int i : emptyPattern) {
                        slot.setItem(i, empty);
                    }

                    ItemStack stop = new ItemStack(Material.WARPED_BUTTON);
                    ItemMeta stopMeta = stop.getItemMeta();
                    assert stopMeta != null;

                    for (int i = 1; i < 4; i++) {
                        stopMeta.setDisplayName(ChatColor.RED + "Stop column " + i);
                        stop.setItemMeta(stopMeta);
                        slot.setItem(i + 45, stop);

                    }
                    final int[] pattern = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                    final List<ItemStack> possible = new CasinoItemContainer().getAll();
                    for (int i = 0; i < possible.size(); i++) {
                        slot.setItem(pattern[i], possible.get(i));
                    }
                    player.openInventory(slot);
                    System.out.println("Initialised slots");

                   BukkitTask timer = Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                       System.out.println("Timer!");
                   }, 0, 20);
                    ButtonPress.addListener(column -> {
                        System.out.println("Called");
                        timer.cancel();
                    });
                    System.out.println("Thread finished");

                    for (int i = 28; i <= 30; i++) {
                        slot.clear(i);
                        slot.clear(i - 9);
                        slot.clear(i - 18);
                    }
//                    final List<ItemStack> random = new CasinoItemContainer().getThree();
//                    final List<ItemStack> firstRow = new ArrayList<>(Arrays.asList(slot.getItem(10), slot.getItem(11), slot.getItem(12)));
//                    final List<ItemStack> secondRow = new ArrayList<>(Arrays.asList(slot.getItem(19), slot.getItem(20), slot.getItem(21)));
//
//                    for (int i = 0; i < 3; i++) {
//                        slot.setItem(i + 28, secondRow.get(i));
//                    }
//
//                    for (int i = 0; i < 3; i++) {
//                        slot.setItem(i + 19, firstRow.get(i));
//                    }
//
//                    for (int i = 0; i < 3; i++) {
//                        slot.setItem(i + 10, random.get(i));
//                    }
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    new ButtonPress().addListener(column -> {
//                        System.out.println("Listner callerdstop");
//                        Thread.currentThread().stop();
//                    });
                }
                case BARRIER -> player.closeInventory();

                case WARPED_BUTTON -> {
                    final String displayName = Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName();
                    if (displayName.contains("Stop column")) {
                        System.out.println("Stop");
                        ButtonPress.buttonPressTrigger(123);
                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        System.out.println("inventory close");
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase("Slot Machine")) {
            System.out.println("Attempting the interrupt");
            ButtonPress.buttonPressTrigger(123);
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
        listeners.forEach((listener) -> {
            listener.pressed(column);
        });
    }
}
