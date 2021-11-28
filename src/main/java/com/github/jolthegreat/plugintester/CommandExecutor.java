package com.github.jolthegreat.plugintester;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import java.util.List;

public class CommandExecutor implements org.bukkit.command.CommandExecutor, TabCompleter {

    @Nonnull
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Inventory inventory = Bukkit.createInventory(player, 9, "Test");
        ItemStack gold = new ItemStack(Material.GOLD_INGOT);
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta goldMeta = gold.getItemMeta();
        assert goldMeta != null;
        goldMeta.setDisplayName("Casino");
        goldMeta.addEnchant(Enchantment.LUCK, 1, true);
        goldMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        gold.setItemMeta(goldMeta);
        ItemStack[] stacks = {gold, cancel};
        inventory.setContents(stacks);
        player.openInventory(inventory);
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
