package com.rooxchicken.overload.Tasks;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.rooxchicken.overload.Overload;

public class GolemsAid extends Task
{
    private Overload plugin;
    private Player player;

    private int t = 0;
    private IronGolem golem;

    public GolemsAid(Overload _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;
        tickThreshold = 1;

        golem = (IronGolem)player.getWorld().spawnEntity(player.getLocation(), EntityType.IRON_GOLEM);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1.0f, 1.0f);
    }

    @Override
    public void run()
    {
        Player lowest = null;

        for(Object o : Overload.getNearbyEntities(player.getLocation(), 20))
        {
            if(o instanceof Player && o != player && o != golem)
            {
                Player p = (Player)o;
                if(lowest == null || p.getHealth() < lowest.getHealth())
                    lowest = p;
            }
        }

        golem.setTarget(lowest);

        if(player.getLocation().distance(golem.getLocation()) > 20)
            golem.teleport(player.getLocation());

        if(++t > 1200)
        {
            golem.setHealth(0);
            cancel = true;
        }
    }
}
