package com.github.jolthegreat.plugintester;

import com.github.jolthegreat.plugintester.containers.CasinoContainer;
import net.wesjd.anvilgui.AnvilGUI;
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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;

enum SlotType {
    ONE, TWO, THREE, ALL
}

public class Listener implements org.bukkit.event.Listener {
    final private static ArrayList<BukkitTask> tasks = new ArrayList<>();


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("Joined");
    }

    public static BiFunction<Player, String, AnvilGUI.Response> getSlotFunction() {
        return (player, s) -> {
            System.out.println(s);
            Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
            if (pattern.matcher(s).matches()) {
                final List<ItemStack> possible = CasinoContainer.getAll();
                if (possible.size() == 9) {
                    Inventory inventory = Bukkit.createInventory(player, 54, ChatColor.GOLD + "Slot Machine");

                    final ItemStack around = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                    ItemMeta aroundMeta = around.getItemMeta();
                    assert aroundMeta != null;
                    aroundMeta.setDisplayName(ChatColor.RESET + "");
                    final int[] aroundPattern = {0, 1, 2, 3, 4, 9, 13, 18, 22, 27, 31, 36, 37, 38, 39, 40};
                    for (int i : aroundPattern) {
                        inventory.setItem(i, around);
                    }

                    final int[] possiblePattern = {10, 11, 12, 19, 20, 21, 28, 29, 30};
                    for (int i = 0; i < possible.size(); i++) {
                        inventory.setItem(possiblePattern[i], possible.get(i));
                    }

                    final ItemStack startButton = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
                    final ItemMeta startButtonMeta = startButton.getItemMeta();
                    assert startButtonMeta != null;
                    startButtonMeta.setDisplayName(ChatColor.GREEN + "Start");
                    startButton.setItemMeta(startButtonMeta);
                    inventory.setItem(24, startButton);


                    final int[] stopButtonPattern = {46, 47, 48};

                    for (int j : stopButtonPattern) {
                        ItemStack stopButton = new ItemStack(Material.WARPED_BUTTON);
                        ItemMeta stopButtonMeta = stopButton.getItemMeta();
                        assert stopButtonMeta != null;
                        stopButtonMeta.setDisplayName(ChatColor.RED + "Stop slot " + (j - 45));
                        stopButton.setItemMeta(stopButtonMeta);
                        inventory.setItem(j, stopButton);
                    }
                    player.openInventory(inventory);


                    tasks.add(Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                        ItemStack newSlot = CasinoContainer.getRandom();
                        inventory.setItem(28, inventory.getItem(19));
                        inventory.setItem(19, inventory.getItem(10));
                        inventory.setItem(10, newSlot);
                        System.out.println("Cycle1");
                    }, 0, 5));

                    tasks.add(Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                        ItemStack newSlot = CasinoContainer.getRandom();
                        inventory.setItem(29, inventory.getItem(20));
                        inventory.setItem(20, inventory.getItem(11));
                        inventory.setItem(11, newSlot);
                        System.out.println("Cycle2");
                    }, 0, 5));
                    tasks.add(Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                        ItemStack newSlot = CasinoContainer.getRandom();
                        inventory.setItem(30, inventory.getItem(21));
                        inventory.setItem(21, inventory.getItem(12));
                        inventory.setItem(12, newSlot);
                        System.out.println("Cycle3");
                    }, 0, 5));


                    ButtonPress.addListener((slotType) -> {
                        switch (slotType) {
                            case ONE -> tasks.get(0).cancel();
                            case TWO -> tasks.get(1).cancel();
                            case THREE -> tasks.get(2).cancel();
                            case ALL -> tasks.forEach(BukkitTask::cancel);
                            default -> System.out.println("Pls select proper stuff");
                        }
                    });

                } else {
                    System.out.println(Color.RED + "you can only have 9 items in casino");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Please enter proper number as an input");
            }
            return AnvilGUI.Response.close();
        };
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        System.out.println("inventory close");
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine")) {
            System.out.println("Attempting the interrupt");
            ButtonPress.buttonPressTrigger(SlotType.ALL);

        }
    }

    public SlotType intToSlotType(int i) {
        switch (i) {
            case 1 -> {
                return SlotType.ONE;
            }
            case 2 -> {
                return SlotType.TWO;
            }
            case 3 -> {
                return SlotType.THREE;
            }
            default -> {
                return SlotType.ALL;
            }
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        ItemStack eventStack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine")) {
            switch (Objects.requireNonNull(eventStack).getType()) {
                case GOLD_INGOT -> {
                    player.closeInventory();

                    new AnvilGUI.Builder()
                            .title("Test")
                            .itemLeft(new ItemStack(Material.PAPER))
                            .plugin(PluginTester.INSTANCE)
                            .onComplete(getSlotFunction())
                            .open(player);
                }
                case BARRIER -> player.closeInventory();

                case WARPED_BUTTON -> {
                    final String displayName = Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta()).getDisplayName();
                    if (displayName.contains("Stop slot")) {
                        System.out.println("Stop");
                        ButtonPress.buttonPressTrigger(intToSlotType(Integer.parseInt(displayName.split(" ")[2])));
                    }
                }
            }
            event.setCancelled(true);
        }
    }
}

class ButtonPress {
    static List<Consumer<SlotType>> listeners = new ArrayList<>();

    public static void addListener(Consumer<SlotType> press) {
        System.out.println(press);
        listeners.add(press);
    }

    public static void buttonPressTrigger(SlotType slotType) {
        listeners.forEach((listener) -> listener.accept(slotType));
    }
}
