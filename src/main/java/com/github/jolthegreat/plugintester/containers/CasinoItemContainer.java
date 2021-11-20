package com.github.jolthegreat.plugintester.containers;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CasinoItemContainer {
    private ItemStack wood = new ItemStack(Material.BIRCH_WOOD);
    private ItemStack netherite = new ItemStack(Material.NETHERITE_INGOT);
    private ItemStack apple = new ItemStack(Material.APPLE);
    private ItemStack bread = new ItemStack(Material.BREAD);
    private ItemStack diamond = new ItemStack(Material.DIAMOND);
    private ItemStack iron = new ItemStack(Material.IRON_INGOT);
    private ItemStack gold = new ItemStack(Material.GOLD_INGOT);
    private ItemStack dragonHead = new ItemStack(Material.DRAGON_HEAD);
    private ItemStack head = new ItemStack(Material.PLAYER_HEAD);

    public List<ItemStack> getAll() {
        SkullMeta meta = (SkullMeta) this.head.getItemMeta();
        assert meta != null;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("inutanuking"));
        this.head.setItemMeta(meta);
        ItemStack[] stacks = {wood, netherite, apple, bread, diamond, iron, gold, dragonHead, head};
        return Arrays.stream(stacks).toList();
    }

    public List<ItemStack> getThree() {
        List<ItemStack> selection = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            selection.add(getAll().get(ThreadLocalRandom.current().nextInt(0, 5)));
        }
        return selection;
    }
}
