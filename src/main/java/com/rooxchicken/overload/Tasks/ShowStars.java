package com.rooxchicken.overload.Tasks;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.overload.Overload;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ShowStars extends Task implements Listener
{
    private Overload plugin;

    private HashMap<Player, Integer> playerLeftclickMap;
    private HashMap<Player, Integer> playerRightclickMap;

    public ShowStars(Overload _plugin)
    {
        super(_plugin);
        plugin = _plugin;
        tickThreshold = 1;
        Bukkit.getPluginManager().registerEvents(this, _plugin);

        playerLeftclickMap = new HashMap<Player, Integer>();
        playerRightclickMap = new HashMap<Player, Integer>();
    }

    @Override
    public void run()
    {
        for(Player player : Bukkit.getOnlinePlayers())
        {
            plugin.itemManager.tickPlayer(player);
            PersistentDataContainer data = player.getPersistentDataContainer();
            int stars = data.get(Overload.starsKey, PersistentDataType.INTEGER);
            int overloadTime = data.get(Overload.overloadLengthKey, PersistentDataType.INTEGER);
            int jumpCooldown = data.get(Overload.launchCooldownKey, PersistentDataType.INTEGER);
            int upgrade = data.get(Overload.upgradeKey, PersistentDataType.INTEGER);
            if(overloadTime > 0)
            {
                upgrade = data.get(Overload.overloadUpgradeKey, PersistentDataType.INTEGER);
                data.set(Overload.overloadLengthKey, PersistentDataType.INTEGER, --overloadTime);
                if(jumpCooldown > 0)
                    data.set(Overload.launchCooldownKey, PersistentDataType.INTEGER, --jumpCooldown);
                
                String jump = "READY";
                if(jumpCooldown > 0)
                    jump = (jumpCooldown/20) + "s / 30s";

                if(data.has(Overload.bloodLossActiveKey, PersistentDataType.BOOLEAN) && data.get(Overload.bloodLossActiveKey, PersistentDataType.BOOLEAN))
                {
                    if(!playerLeftclickMap.containsKey(player))
                        playerLeftclickMap.put(player, 0);
                    if(!playerRightclickMap.containsKey(player))
                        playerRightclickMap.put(player, 0);

                    playerLeftclickMap.replace(player, Math.max(playerLeftclickMap.get(player) - 1, 0));
                    playerRightclickMap.replace(player, Math.max(playerRightclickMap.get(player) - 1, 0));

                    if(!data.has(Overload.bloodLossCooldownKey, PersistentDataType.INTEGER))
                        data.set(Overload.bloodLossCooldownKey, PersistentDataType.INTEGER, 0);

                    jump += " |" + ChatColor.RED + " ❤ ";
                    int bloodLossCooldown = data.get(Overload.bloodLossCooldownKey, PersistentDataType.INTEGER);
                    if(bloodLossCooldown > 0)
                    {
                        data.set(Overload.bloodLossCooldownKey, PersistentDataType.INTEGER, --bloodLossCooldown);
                        jump += (bloodLossCooldown/20) + "s / 30s";
                    }
                    else
                    {
                        jump += "READY";

                        if(player.isSneaking() && playerLeftclickMap.get(player) > 0 && playerRightclickMap.get(player) > 0)
                        {
                            data.set(Overload.bloodLossCooldownKey, PersistentDataType.INTEGER, 30*20);
                            player.setHealth(player.getHealth() - 4);
                            player.damage(0.0001);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
                            player.getWorld().spawnParticle(Particle.BLOCK_DUST, player.getLocation().clone().add(0,1,0), 100, 0, 0, 0, Material.REDSTONE_BLOCK.createBlockData());
                            for(Object o : Overload.getNearbyEntities(player.getLocation(), 10))
                            {
                                if(o instanceof Player && o != player)
                                {
                                    Player p = (Player)o;
                                    p.setHealth(p.getHealth() - 10);
                                    p.damage(0.0001);
                                    p.getWorld().spawnParticle(Particle.BLOCK_DUST, p.getLocation().clone().add(0,1,0), 50, 0, 0, 0, Material.REDSTONE_BLOCK.createBlockData());

                                    double distance = Math.max(1, Math.min(p.getLocation().distance(player.getLocation()), 3));
                                    p.setVelocity(p.getLocation().clone().subtract(player.getLocation().clone().subtract(0,1,0)).toVector().multiply(1/distance));
                                }
                            }
                        }
                    }
                }

                if(data.has(Overload.abyssActiveKey, PersistentDataType.BOOLEAN) && data.get(Overload.abyssActiveKey, PersistentDataType.BOOLEAN))
                {
                    if(!data.has(Overload.abyssCooldownKey, PersistentDataType.INTEGER))
                        data.set(Overload.abyssCooldownKey, PersistentDataType.INTEGER, 0);
                    jump += " |" + ChatColor.DARK_GRAY + " ☄ ";
                    int abyssCooldown = data.get(Overload.abyssCooldownKey, PersistentDataType.INTEGER);
                    if(abyssCooldown > 0)
                    {
                        data.set(Overload.abyssCooldownKey, PersistentDataType.INTEGER, --abyssCooldown);
                        jump += (abyssCooldown/20) + "s / 180s";
                    }
                    else
                        jump += "READY";
                }


                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.BLUE + "✴ " + (overloadTime/20) + "s / 600s " + ChatColor.WHITE + "| ☁ " + jump));

                switch(upgrade)
                {
                    case 0:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 21, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, 0));
                    break;

                    case 1:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 21, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, 0));
                    break;

                    case 2:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 21, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, 1));
                    break;

                    case 3:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 21, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, 0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 21, 0));
                    break;
                }

                player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0, 1, 0), 1*(upgrade+1), 0.4f, 0.5f, 0.4f, new Particle.DustOptions(Color.BLUE, 1f));

                if(!player.getLocation().clone().subtract(0, 0.4, 0).getBlock().isPassable())
                    player.setAllowFlight(false);

                if(player.isOnGround())
                {
                    if(jumpCooldown == 0)
                        player.setAllowFlight(true);
                    if(plugin.abilities.jumps.contains(player))
                        plugin.abilities.jumps.remove(player);
                }

            }
            else
            {
                String upg = "";
                if(upgrade > 0)
                {
                    upg = ChatColor.BLUE + " | ";
                    for(int i = 0; i < upgrade; i++)
                    {
                        upg += "★";
                    }
                }
                if(data.has(Overload.bloodLossKey, PersistentDataType.BOOLEAN) && data.get(Overload.bloodLossKey, PersistentDataType.BOOLEAN))
                    upg += ChatColor.RED + " ❤";
                if(data.has(Overload.abyssKey, PersistentDataType.BOOLEAN) && data.get(Overload.abyssKey, PersistentDataType.BOOLEAN))
                    upg += ChatColor.DARK_GRAY + " ☄";
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("★ " + stars + "/" + Overload.STAR_MAX + upg));
            }
        }
    }

    @EventHandler
    public void addToClick(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();

        if(!playerLeftclickMap.containsKey(player))
            playerLeftclickMap.put(player, 0);
        if(!playerRightclickMap.containsKey(player))
            playerRightclickMap.put(player, 0);

        switch(event.getAction())
        {
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                playerLeftclickMap.replace(player, playerLeftclickMap.get(player) + 3);
            break;

            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                playerRightclickMap.replace(player, playerRightclickMap.get(player) + 3);
            break;

            default: return;
        }

        return;
    }

    @EventHandler
    public void useAbyss(PlayerSwapHandItemsEvent event)
    {
        Player player = event.getPlayer();
        PersistentDataContainer data = player.getPersistentDataContainer();
        
        if(player.isSneaking() && data.get(Overload.overloadLengthKey, PersistentDataType.INTEGER) > 0 && data.get(Overload.abyssActiveKey, PersistentDataType.BOOLEAN) && data.get(Overload.abyssCooldownKey, PersistentDataType.INTEGER) <= 0)
        {
            Overload.tasks.add(new Abyss(plugin, player));
            data.set(Overload.abyssCooldownKey, PersistentDataType.INTEGER, 180*20);
            event.setCancelled(true);
        }
    }
}
