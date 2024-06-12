package com.rooxchicken.overload.Events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import com.rooxchicken.overload.Overload;

public class HandleAbilities implements Listener
{
    private Overload plugin;
    public ArrayList<Player> jumps;

    public HandleAbilities(Overload _plugin)
    {
        plugin = _plugin;
        jumps = new ArrayList<Player>();
    }

    @EventHandler
    public void useOverload(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        if(player.isSneaking() && plugin.getStars(player) == Overload.STAR_MAX)
        {
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().clone().add(0, 1, 0), 150, 0.2, 1, 0.2);
            setOverload(player);
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.set(Overload.overloadUpgradeKey, PersistentDataType.INTEGER, data.get(Overload.upgradeKey, PersistentDataType.INTEGER));
            data.set(Overload.upgradeKey, PersistentDataType.INTEGER, 0);
            data.set(Overload.launchCooldownKey, PersistentDataType.INTEGER, 0);
            event.setCancelled(true);
        }
    }

    public void setOverload(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        plugin.setStars(player, 0);

        data.set(Overload.overloadLengthKey, PersistentDataType.INTEGER, 10*60*20);
        player.setAllowFlight(true);
    }

    @EventHandler
    public void cancelFallIfJump(EntityDamageEvent event)
    {
        if(event.getCause() != DamageCause.FALL || event.getEntityType() != EntityType.PLAYER || !plugin.isOverloaded((Player)event.getEntity()))
            return;
        
        if(jumps.contains(event.getEntity()))
        {
            event.setCancelled(true);
            jumps.remove(event.getEntity());
        }
    }

    @EventHandler
    public void activateDoubleJump(PlayerToggleFlightEvent event)
    {
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        player.setFlying(false);
        player.setAllowFlight(false);

        if(!plugin.isOverloaded(player) || jumps.contains(player))
            return;

        jumps.add(player);
        player.getPersistentDataContainer().set(Overload.launchCooldownKey, PersistentDataType.INTEGER, 30*20);

        Vector direction = player.getLocation().getDirection().multiply(2);
        player.setVelocity(player.getVelocity().add(direction));

        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 150, 0.5f, 0.2f, 0.5f, new Particle.DustOptions(Color.TEAL, 1f));
        player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 150, 0.5f, 0.2f, 0.5f, new Particle.DustOptions(Color.BLUE, 1f));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BIG_DRIPLEAF_FALL, 1, 1);
    }
}
