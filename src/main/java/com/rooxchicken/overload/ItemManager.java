package com.rooxchicken.overload;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.rooxchicken.overload.Tasks.FreezeEntities;
import com.rooxchicken.overload.Tasks.GolemsAid;

public class ItemManager implements Listener
{
    private Overload plugin;
    
    public ItemStack star;
    public ItemStack speedEnhancement;
    public ItemStack enduranceEnhancement;
    public ItemStack toughnessEnhancement;
    public ItemStack chronoStasis;
    public ItemStack golemsAid;
    public ItemStack vitalSurge;
    public ItemStack pullRain;

    public NamespacedKey chronoStasisCooldownKey;
    public NamespacedKey golemsAidCooldownKey;
    public NamespacedKey vitalSurgeCooldownKey;
    public NamespacedKey pullRainCooldownKey;

    private ShapedRecipe chronoStasisRecipe;
    private ShapedRecipe golemsAidRecipe;
    private ShapedRecipe vitalSurgeRecipe;
    private ShapedRecipe pullRainRecipe;

    @EventHandler
    public void useItem(PlayerInteractEvent event)
    {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if(item != null && item.hasItemMeta())
        {
            boolean used = false;
            PersistentDataContainer data = player.getPersistentDataContainer();
            if(item.getItemMeta().equals(star.getItemMeta()))
            {
                int stars = data.get(Overload.starsKey, PersistentDataType.INTEGER) + 1;
                if(stars <= Overload.STAR_MAX)
                {
                    data.set(Overload.starsKey, PersistentDataType.INTEGER, stars);
                    used = true;
                }
            }

            if(item.getItemMeta().equals(pullRain.getItemMeta()))
            {
                if(!checkSetCooldown(player, pullRainCooldownKey, 180))
                    return;
                for(Object o : Overload.getNearbyEntities(player.getLocation(), 15))
                {
                    if(o != player && o instanceof LivingEntity)
                    {
                        LivingEntity entity = (LivingEntity)o;
                        entity.setVelocity(player.getLocation().clone().subtract(entity.getLocation()).toVector().multiply(0.3));

                        entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 2.0f);
                    }
                }
                used = true;
            }

            if(item.getItemMeta().equals(golemsAid.getItemMeta()))
            {
                if(!checkSetCooldown(player, golemsAidCooldownKey, 180))
                    return;
                Overload.tasks.add(new GolemsAid(plugin, player));
                used = true;
            }

            if(item.getItemMeta().equals(vitalSurge.getItemMeta()))
            {
                if(!checkSetCooldown(player, vitalSurgeCooldownKey, 120))
                    return;
                player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().clone().add(0,1,0), 100, 0.3, 0.5, 0.3, new Particle.DustOptions(Color.RED, 1.0f));
                player.setHealth(Math.min(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue(), player.getHealth() + player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()/2.0));
                used = true;
            }

            if(item.getItemMeta().equals(chronoStasis.getItemMeta()))
            {
                if(!checkSetCooldown(player, chronoStasisCooldownKey, 120))
                    return;
                Overload.tasks.add(new FreezeEntities(plugin, player));
                used = true;
            }

            int upgrade = data.get(Overload.upgradeKey, PersistentDataType.INTEGER);

            if(item.getItemMeta().equals(speedEnhancement.getItemMeta()) && upgrade < 1)
            {
                data.set(Overload.upgradeKey, PersistentDataType.INTEGER, 1);
                used = true;
            }

            if(item.getItemMeta().equals(enduranceEnhancement.getItemMeta()) && upgrade < 2)
            {
                data.set(Overload.upgradeKey, PersistentDataType.INTEGER, 2);
                used = true;
            }

            if(item.getItemMeta().equals(toughnessEnhancement.getItemMeta()) && upgrade < 3)
            {
                data.set(Overload.upgradeKey, PersistentDataType.INTEGER, 3);
                used = true;
            }

            if(used)
            {
                item.setAmount(item.getAmount() - 1);
                player.playSound(player, Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
            }
        }
    }

    public boolean checkSetCooldown(Player player, NamespacedKey key, int cooldown)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(key, PersistentDataType.INTEGER))
            data.set(key, PersistentDataType.INTEGER, 0);

        int time = data.get(key, PersistentDataType.INTEGER);
        if(time <= 0)
        {
            data.set(key, PersistentDataType.INTEGER, cooldown*20);
            return true;
        }

        player.sendMessage("§4This item is on cooldown for " + (time/20) + " seconds!");
        
        return false;
    }

    public void tickPlayer(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(chronoStasisCooldownKey, PersistentDataType.INTEGER))
            data.set(chronoStasisCooldownKey, PersistentDataType.INTEGER, 0);

        int chronoStasisTime = data.get(chronoStasisCooldownKey, PersistentDataType.INTEGER) - 1;
        data.set(chronoStasisCooldownKey, PersistentDataType.INTEGER, chronoStasisTime);

        if(!data.has(golemsAidCooldownKey, PersistentDataType.INTEGER))
            data.set(golemsAidCooldownKey, PersistentDataType.INTEGER, 0);

        int golemsAidTime = data.get(golemsAidCooldownKey, PersistentDataType.INTEGER) - 1;
        data.set(golemsAidCooldownKey, PersistentDataType.INTEGER, golemsAidTime);

        if(!data.has(vitalSurgeCooldownKey, PersistentDataType.INTEGER))
            data.set(vitalSurgeCooldownKey, PersistentDataType.INTEGER, 0);

        int vitalSurgeTime = data.get(vitalSurgeCooldownKey, PersistentDataType.INTEGER) - 1;
        data.set(vitalSurgeCooldownKey, PersistentDataType.INTEGER, vitalSurgeTime);

        if(!data.has(pullRainCooldownKey, PersistentDataType.INTEGER))
            data.set(pullRainCooldownKey, PersistentDataType.INTEGER, 0);

        int pullRainTime = data.get(pullRainCooldownKey, PersistentDataType.INTEGER) - 1;
        data.set(pullRainCooldownKey, PersistentDataType.INTEGER, pullRainTime);
    }

    public void resetCooldown(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(chronoStasisCooldownKey, PersistentDataType.INTEGER, 0);
        data.set(golemsAidCooldownKey, PersistentDataType.INTEGER, 0);
        data.set(vitalSurgeCooldownKey, PersistentDataType.INTEGER, 0);
        data.set(pullRainCooldownKey, PersistentDataType.INTEGER, 0);
    }

    public ItemManager(Overload _plugin)
    {
        plugin = _plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        chronoStasisCooldownKey = new NamespacedKey(plugin, "chronoStasisCooldown");
        golemsAidCooldownKey = new NamespacedKey(plugin, "golemsAidCooldown");
        vitalSurgeCooldownKey = new NamespacedKey(plugin, "vitalSurgeCooldown");
        pullRainCooldownKey = new NamespacedKey(plugin, "pullRainCooldown");

        star = new ItemStack(Material.NETHER_STAR);

        {ItemMeta meta = star.getItemMeta();
        meta.setDisplayName("§e§l§oStar");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        star.setItemMeta(meta);}

        speedEnhancement = new ItemStack(Material.GRAY_DYE);

        {ItemMeta meta = speedEnhancement.getItemMeta();
        meta.setDisplayName("§7§l§oSpeed Enhancement");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        speedEnhancement.setItemMeta(meta);}

        enduranceEnhancement = new ItemStack(Material.LIGHT_BLUE_DYE);

        {ItemMeta meta = enduranceEnhancement.getItemMeta();
        meta.setDisplayName("§b§l§oEndurance Enhancement");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        enduranceEnhancement.setItemMeta(meta);}

        toughnessEnhancement = new ItemStack(Material.YELLOW_DYE);

        {ItemMeta meta = toughnessEnhancement.getItemMeta();
        meta.setDisplayName("§e§l§oToughness Enhancement");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        toughnessEnhancement.setItemMeta(meta);}

        chronoStasis = new ItemStack(Material.CLOCK);

        {ItemMeta meta = chronoStasis.getItemMeta();
        meta.setDisplayName("§e§l§oChrono Stasis");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        chronoStasis.setItemMeta(meta);}

        golemsAid = new ItemStack(Material.IRON_INGOT);

        {ItemMeta meta = golemsAid.getItemMeta();
        meta.setDisplayName("§f§l§oGolem's Aid");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        golemsAid.setItemMeta(meta);}

        vitalSurge = new ItemStack(Material.IRON_NUGGET);

        {ItemMeta meta = vitalSurge.getItemMeta();
        meta.setDisplayName("§c§l§oVital Surge");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        vitalSurge.setItemMeta(meta);}

        pullRain = new ItemStack(Material.STICK);

        {ItemMeta meta = pullRain.getItemMeta();
        meta.setDisplayName("§b§l§oPull Rain");
        meta.setCustomModelData(1);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.DURABILITY, 1, true);
        pullRain.setItemMeta(meta);}
    }

    public void registerRecipes()
    {
        {NamespacedKey key = new NamespacedKey(plugin, "chronoStasisRecipe");
        chronoStasisRecipe = new ShapedRecipe(key, chronoStasis);
        chronoStasisRecipe.shape("SBS", "BCB", "SBS");

        chronoStasisRecipe.setIngredient('S', Material.SOUL_TORCH);
        chronoStasisRecipe.setIngredient('B', Material.BLUE_ICE);
        chronoStasisRecipe.setIngredient('C', Material.CLOCK);

        Bukkit.addRecipe(chronoStasisRecipe);}

        {NamespacedKey key = new NamespacedKey(plugin, "golemsAidRecipe");
        golemsAidRecipe = new ShapedRecipe(key, chronoStasis);
        golemsAidRecipe.shape("III", "GGG", "III");

        golemsAidRecipe.setIngredient('I', Material.IRON_BLOCK);
        golemsAidRecipe.setIngredient('G', Material.GOLDEN_APPLE);

        Bukkit.addRecipe(golemsAidRecipe);}

        {NamespacedKey key = new NamespacedKey(plugin, "vitalSurgeRecipe");
        vitalSurgeRecipe = new ShapedRecipe(key, chronoStasis);
        vitalSurgeRecipe.shape("FGF", "GRG", "FGF");

        vitalSurgeRecipe.setIngredient('F', Material.FERMENTED_SPIDER_EYE);
        vitalSurgeRecipe.setIngredient('G', Material.GOLDEN_APPLE);
        vitalSurgeRecipe.setIngredient('R', Material.REDSTONE_BLOCK);

        Bukkit.addRecipe(vitalSurgeRecipe);}

        // {NamespacedKey key = new NamespacedKey(plugin, "pullRainRecipe");
        // pullRainRecipe = new ShapedRecipe(key, chronoStasis);
        // pullRainRecipe.shape("SBS", "BCB", "SBS");

        // pullRainRecipe.setIngredient('S', Material.SOUL_TORCH);
        // pullRainRecipe.setIngredient('B', Material.BLUE_ICE);
        // pullRainRecipe.setIngredient('C', Material.CLOCK);

        // Bukkit.addRecipe(pullRainRecipe);}
    }
}
