package com.rooxchicken.overload.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.rooxchicken.overload.Overload;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentPieces.u;

public class ShowStars extends Task
{
    private Overload plugin;
    public ShowStars(Overload _plugin)
    {
        super(_plugin);
        plugin = _plugin;
        tickThreshold = 1;
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
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("★ " + stars + "/" + Overload.STAR_MAX + upg));
            }
        }
    }
}
