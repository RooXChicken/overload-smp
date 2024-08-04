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
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import com.rooxchicken.overload.Overload;

public class Abyss extends Task
{
    private Overload plugin;
    private Player player;

    private Location start;
    private int t = 0;

    public Abyss(Overload _plugin, Player _player)
    {
        super(_plugin);
        plugin = _plugin;
        player = _player;

        start = player.getLocation().clone();

        tickThreshold = 1;
        start.getWorld().playSound(start, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 2.0f);
    }

    @Override
    public void run()
    {
        start.getWorld().spawnParticle(Particle.REDSTONE, start, 300, 15/5.0, 0.4, 15/5.0, new Particle.DustOptions(Color.BLACK, 1.0f));
        start.getWorld().spawnParticle(Particle.REDSTONE, start, 300, 1, 1, 1, new Particle.DustOptions(Color.BLACK, 1.0f));

        for(Object o : Overload.getNearbyEntities(start, 15))
        {
            if(o != player && o instanceof LivingEntity)
            {
                LivingEntity entity = (LivingEntity)o;
                entity.setVelocity(start.clone().subtract(entity.getLocation()).toVector().multiply(0.05));
                entity.damage(6.0);
            }

            if(o instanceof Projectile)
            {
                ((Projectile)o).setVelocity(start.clone().subtract(0,0.3,0).subtract(((Projectile)o).getLocation()).toVector().multiply(0.4));
            }
        }

        if(++t > 30*20)
        {
            cancel = true;
        }
    }
}