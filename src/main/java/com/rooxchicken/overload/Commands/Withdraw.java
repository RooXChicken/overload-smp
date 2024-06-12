package com.rooxchicken.overload.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.overload.Overload;

public class Withdraw implements CommandExecutor
{
    private Overload plugin;

    public Withdraw(Overload _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        Player player = Bukkit.getPlayer(sender.getName());
        PersistentDataContainer data = player.getPersistentDataContainer();
        int stars = data.get(Overload.starsKey, PersistentDataType.INTEGER);
        if(stars > 0)
        {
            plugin.giveStar(player, 1);
            data.set(Overload.starsKey, PersistentDataType.INTEGER, --stars);
            player.sendMessage(ChatColor.BLUE + "You have withdrawn a star.");
        }
        else
            player.sendMessage(ChatColor.RED + "You have no stars.");

        return true;
    }

}
