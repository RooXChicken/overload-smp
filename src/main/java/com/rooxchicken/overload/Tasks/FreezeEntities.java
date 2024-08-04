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
import org.bukkit.entity.Player;

import com.rooxchicken.overload.Overload;

public class FreezeEntities extends Task
{
    private Overload plugin;
    private Player player;

    private HashMap<Entity, Location> entityLocationMap;
    private int t = 0;

    public FreezeEntities(Overload _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;
        tickThreshold = 1;

        entityLocationMap = new HashMap<Entity, Location>();

        for(World world : Bukkit.getWorlds())
        {
            for(Entity entity : world.getEntities())
            {
                if(entity instanceof Player)
                    ((Player)entity).playSound(entity.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0f, 0.9f);
                if(entity == player)
                    continue;
                entityLocationMap.put(entity, entity.getLocation().clone());
            }
        }
    }

    @Override
    public void run()
    {
        for(Entry<Entity, Location> data : entityLocationMap.entrySet())
        {
            data.getKey().teleport(data.getValue());
            data.getKey().getWorld().spawnParticle(Particle.REDSTONE, data.getValue(), 25, 0.2, 0.2, 0.2, new Particle.DustOptions(Color.BLACK, 1.0f));
        }

        if(++t > 100)
        {
            for(Entry<Entity, Location> data : entityLocationMap.entrySet())
            {
                data.getKey().getWorld().playSound(data.getValue(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
            }
            cancel = true;
        }
    }
}