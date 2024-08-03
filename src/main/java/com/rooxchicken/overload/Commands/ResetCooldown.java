package com.rooxchicken.overload.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.overload.Overload;

public class ResetCooldown implements CommandExecutor
{
    private Overload plugin;

    public ResetCooldown(Overload _plugin)
    {
        plugin = _plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if(sender.isOp())
        {
            Player player = Bukkit.getPlayer(sender.getName());
            plugin.itemManager.resetCooldown(player);
        }

        return true;
    }

}
