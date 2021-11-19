package com.github.jolthegreat.plugintester;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;

public class ItemBuilder {
    private Material material;
    private Collection<String> lore;
    private ItemMeta meta;
    private boolean shiny;

    public ItemBuilder(Material material, Collection<String> lore, ItemMeta meta, boolean shiny) {
        this.material = material;
        this.lore = lore;
        this.meta = meta;
        this.shiny = shiny;
        meta.setLore((List<String>) this.lore);
        if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }



    public ItemStack getAll() {
        ItemStack stack = new ItemStack(material);
        stack.setItemMeta(meta);
        if (shiny) stack.addEnchantment(Enchantment.LUCK, 1);
        return stack;
    }
}
