package com.rooxchicken.overload.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.overload.Overload;

public class SetStars implements CommandExecutor
{
    private Overload plugin;

    public SetStars(Overload _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender.isOp())
        {
            Player player = Bukkit.getPlayer(args[0]);
            if(player == null)
            {
                sender.sendMessage(ChatColor.RED + "Invalid playername.");
                return true;
            }
            player.getPersistentDataContainer().set(Overload.overloadLengthKey, PersistentDataType.INTEGER, 0);
            plugin.setStars(player, Integer.parseInt(args[1]));
        }

        return true;
    }

}
