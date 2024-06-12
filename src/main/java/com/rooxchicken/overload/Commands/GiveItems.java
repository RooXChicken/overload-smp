package com.rooxchicken.overload.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.rooxchicken.overload.Overload;

public class GiveItems implements CommandExecutor
{
    private Overload plugin;

    public GiveItems(Overload _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender.isOp())
        {
            Player player = Bukkit.getPlayer(sender.getName());
            {ItemStack item = new ItemStack(Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§7§l§oSpeed Enhancement");
            meta.setCustomModelData(1);
            item.setItemMeta(meta);

            player.getInventory().addItem(item);}

            {ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE);

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§b§l§oEndurance Enhancement");
            meta.setCustomModelData(1);
            item.setItemMeta(meta);

            player.getInventory().addItem(item);}

            {ItemStack item = new ItemStack(Material.YELLOW_DYE);

            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e§l§oToughness Enhancement");
            meta.setCustomModelData(1);
            item.setItemMeta(meta);

            player.getInventory().addItem(item);}

            plugin.giveStar(player, 1);
        }

        return true;
    }

}
