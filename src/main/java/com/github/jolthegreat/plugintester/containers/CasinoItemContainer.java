package com.github.jolthegreat.plugintester.containers;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CasinoItemContainer {
    private static final ItemStack wood = new ItemStack(Material.BIRCH_WOOD);
    private static final ItemStack netherite = new ItemStack(Material.NETHERITE_INGOT);
    private static final ItemStack apple = new ItemStack(Material.APPLE);
    private static final ItemStack bread = new ItemStack(Material.BREAD);
    private static final ItemStack diamond = new ItemStack(Material.DIAMOND);
    private static final ItemStack iron = new ItemStack(Material.IRON_INGOT);
    private static final ItemStack gold = new ItemStack(Material.GOLD_INGOT);
    private static final ItemStack dragonHead = new ItemStack(Material.DRAGON_HEAD);
    private static final ItemStack head = new ItemStack(Material.PLAYER_HEAD);

    public static List<ItemStack> getAll() {
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("inutanuking"));
        head.setItemMeta(meta);
        ItemStack[] stacks = {wood, netherite, apple, bread, diamond, iron, gold, dragonHead, head};
        return Arrays.stream(stacks).toList();
    }

    public static ItemStack getRandom() {
        return getAll().get(ThreadLocalRandom.current().nextInt(0, 9));
    }
}
