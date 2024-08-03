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
            giveItem(player, plugin.itemManager.star);
            giveItem(player, plugin.itemManager.speedEnhancement);
            giveItem(player, plugin.itemManager.enduranceEnhancement);
            giveItem(player, plugin.itemManager.toughnessEnhancement);
            giveItem(player, plugin.itemManager.chronoStasis);
            giveItem(player, plugin.itemManager.golemsAid);
            giveItem(player, plugin.itemManager.vitalSurge);
            giveItem(player, plugin.itemManager.pullRain);
        }

        return true;
    }

    private void giveItem(Player player, ItemStack item)
    {
        if(player.getInventory().firstEmpty() != -1)
            player.getInventory().addItem(item);
        else
            player.getWorld().dropItemNaturally(player.getLocation(), item);
    }

}
