package com.github.jolthegreat.plugintester;

import com.github.jolthegreat.plugintester.containers.CasinoContainer;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Pattern;

enum SlotType {
    ONE, TWO, THREE, ALL
}

public class Listener implements org.bukkit.event.Listener {
    final private static ArrayList<BukkitTask> tasks = new ArrayList<>();
    final static SlotListeners listeners = new SlotListeners();
    static Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Slot Machine");
    static boolean lockStart = false;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        System.out.println("inventory close");
        lockStart = false;
        if (event.getView().getTitle().equalsIgnoreCase("Test") || event.getView().getTitle().equalsIgnoreCase(ChatColor.GOLD + "Slot Machine")) {
            System.out.println("Attempting the interrupt");
            listeners.buttonPressTrigger(SlotType.ALL);
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
                        listeners.buttonPressTrigger(intToSlotType(Integer.parseInt(displayName.split(" ")[2])));
                    }
                }
                case GREEN_STAINED_GLASS_PANE -> {
                    tasks.add(Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                        ItemStack newSlot = CasinoContainer.getRandom();
                        inventory.setItem(28, inventory.getItem(19));
                        inventory.setItem(19, inventory.getItem(10));
                        inventory.setItem(10, newSlot);
                        System.out.println("Cycle1");
                    }, 0, 2));

                    tasks.add(Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                        ItemStack newSlot = CasinoContainer.getRandom();
                        inventory.setItem(29, inventory.getItem(20));
                        inventory.setItem(20, inventory.getItem(11));
                        inventory.setItem(11, newSlot);
                        System.out.println("Cycle2");
                    }, 0, 2));
                    tasks.add(Bukkit.getScheduler().runTaskTimer(PluginTester.INSTANCE, () -> {
                        ItemStack newSlot = CasinoContainer.getRandom();
                        inventory.setItem(30, inventory.getItem(21));
                        inventory.setItem(21, inventory.getItem(12));
                        inventory.setItem(12, newSlot);
                        System.out.println("Cycle3");
                    }, 0, 2));

                    final ItemStack stopAllButton = new ItemStack(Material.RED_STAINED_GLASS_PANE);
                    final ItemMeta stopAllButtonMeta = stopAllButton.getItemMeta();
                    assert stopAllButtonMeta != null;
                    stopAllButtonMeta.setDisplayName(ChatColor.RED + "Stop");
                    stopAllButton.setItemMeta(stopAllButtonMeta);
                    inventory.setItem(24, stopAllButton);
                }
                case RED_STAINED_GLASS_PANE -> {
                    tasks.forEach(BukkitTask::cancel);
                    listeners.slotFinishTrigger(SlotListeners.StopMethod.ALL);
                    inventory.remove(Material.RED_STAINED_GLASS_PANE);
                }
            }
            event.setCancelled(true);
        }
    }

    public static BiFunction<Player, String, AnvilGUI.Response> getSlotFunction() {
        return (player, s) -> {
            listeners.clearButtonPressListeners();
            listeners.clearSlotFinishListeners();
            tasks.clear();
            Pattern input = Pattern.compile("-?\\d+(\\.\\d+)?");
            if (input.matcher(s).matches()) {
                final int bet = Integer.parseInt(s);
                final List<ItemStack> possible = CasinoContainer.getAll();
                if (possible.size() == 9) {
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

                    listeners.addSlotFinishListener((stopMethod) -> {
                        System.out.println("All stopped");
                        lockStart = true;
                        //10,11,12
                        List<Material> pattern = getPattern();

                    });

                    listeners.addButtonPressListener((slotType) -> {
                        tasks.forEach((bukkitTask) -> {
                            System.out.println(bukkitTask.isCancelled());
                        });
                        switch (slotType) {
                            case ONE -> {
                                tasks.get(0).cancel();
                                System.out.println("Canceled first task");
                            }
                            case TWO -> {
                                tasks.get(1).cancel();
                                System.out.println("Canceled second task");
                            }
                            case THREE -> {
                                tasks.get(2).cancel();
                                System.out.println("Canceled third task");
                            }
                            case ALL -> {
                                tasks.forEach(BukkitTask::cancel);
                            }
                        }

                        if (tasks.get(0).isCancelled() && tasks.get(1).isCancelled() && tasks.get(2).isCancelled()) {
                            System.out.println("Triggering slot finish");
                            listeners.slotFinishTrigger(SlotListeners.StopMethod.INDIVIDUAL);
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

    public static List<Material> getPattern() {
        return Arrays.asList(Objects.requireNonNull(inventory.getItem(10)).getType(),
                Objects.requireNonNull(inventory.getItem(11)).getType(),
                Objects.requireNonNull(inventory.getItem(12)).getType(),
                Objects.requireNonNull(inventory.getItem(19)).getType(),
                Objects.requireNonNull(inventory.getItem(20)).getType(),
                Objects.requireNonNull(inventory.getItem(21)).getType(),
                Objects.requireNonNull(inventory.getItem(22)).getType(),
                Objects.requireNonNull(inventory.getItem(28)).getType(),
                Objects.requireNonNull(inventory.getItem(29)).getType(),
                Objects.requireNonNull(inventory.getItem(30)).getType()
        );
    }

    public static double getWinMoney(List<Material> pattern, SlotListeners.StopMethod method, int bet) {
        double winMoney = bet;
        final FileConfiguration configuration = PluginTester.INSTANCE.getConfig();
        if ((pattern.get(0) == pattern.get(4) && pattern.get(0) == pattern.get(8)) || (pattern.get(2) == pattern.get(4) && pattern.get(2) == pattern.get(6))) {
            switch (pattern.get(4)) {
                case BIRCH_WOOD -> winMoney = bet * configuration.getInt("casino.diagonal.birchWood");
                case APPLE -> winMoney = bet * configuration.getInt("casino.diagonal.apple");
                case BREAD -> winMoney = bet * configuration.getInt("casino.diagonal.bread");
                case IRON_INGOT -> winMoney = bet * configuration.getInt("casino.diagonal.iron");
                case GOLD_INGOT -> winMoney = bet * configuration.getInt("casino.diagonal.gold");
                case DIAMOND -> winMoney = bet * configuration.getInt("casino.diagonal.diamond");
                case NETHERITE_INGOT -> winMoney = bet * configuration.getInt("casino.diagonal.netherite");
                case DRAGON_HEAD -> winMoney = bet * configuration.getInt("casino.diagonal.dragonHead");
                case PLAYER_HEAD -> winMoney = bet * configuration.getInt("casino.diagonal.head");
            }

            if ((pattern.get(0) == pattern.get(4) && pattern.get(0) == pattern.get(8)) && (pattern.get(2) == pattern.get(4) && pattern.get(2) == pattern.get(6)))
                winMoney = winMoney * 2;
        } else if ((pattern.get(3) == pattern.get(4)) && (pattern.get(3) == pattern.get(5))){
            switch (pattern.get(4)) {
                case BIRCH_WOOD -> winMoney = bet * configuration.getInt("casino.horizontal.middle.birchWood");
                case APPLE -> winMoney = bet * configuration.getInt("casino.horizontal.middle.apple");
                case BREAD -> winMoney = bet * configuration.getInt("casino.horizontal.middle.bread");
                case IRON_INGOT -> winMoney = bet * configuration.getInt("casino.horizontal.middle.iron");
                case GOLD_INGOT -> winMoney = bet * configuration.getInt("casino.horizontal.middle.gold");
                case DIAMOND -> winMoney = bet * configuration.getInt("casino.horizontal.middle.diamond");
                case NETHERITE_INGOT -> winMoney = bet * configuration.getInt("casino.horizontal.middle.netherite");
                case DRAGON_HEAD -> winMoney = bet * configuration.getInt("casino.horizontal.middle.dragonHead");
                case PLAYER_HEAD -> winMoney = bet * configuration.getInt("casino.horizontal.middle.head");
            }
        }


        if (method == SlotListeners.StopMethod.ALL) {
            winMoney = winMoney * 2;
        }

        System.out.print("The money is " + winMoney);

        return winMoney;
    }

    static class SlotListeners {

        enum StopMethod {
            INDIVIDUAL, ALL
        }

        private final List<Consumer<SlotType>> buttonPressListeners = new ArrayList<>();
        private final List<Consumer<StopMethod>> slotFinishListeners = new ArrayList<>();

        public void addButtonPressListener(Consumer<SlotType> press) {
            buttonPressListeners.add(press);
        }

        public void buttonPressTrigger(SlotType slotType) {
            buttonPressListeners.forEach((listener) -> listener.accept(slotType));
        }

        public void addSlotFinishListener(Consumer<StopMethod> consumer) {
            slotFinishListeners.add(consumer);
        }

        public void slotFinishTrigger(StopMethod stopMethod) {
            slotFinishListeners.forEach((listener) -> listener.accept(stopMethod));
        }

        public void clearButtonPressListeners() {
            buttonPressListeners.clear();
        }

        public void clearSlotFinishListeners() {
            slotFinishListeners.clear();
        }
    }
}

