package com.rooxchicken.overload;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import com.google.common.base.Predicate;
import com.rooxchicken.overload.Commands.GiveItems;
import com.rooxchicken.overload.Commands.ResetCooldown;
import com.rooxchicken.overload.Commands.SetStars;
import com.rooxchicken.overload.Commands.Withdraw;
import com.rooxchicken.overload.Events.HandleAbilities;
import com.rooxchicken.overload.Tasks.ShowStars;
import com.rooxchicken.overload.Tasks.Task;

public class Overload extends JavaPlugin implements Listener
{
    public static final int STAR_MAX = 2;
    public static NamespacedKey starsKey;
    public static NamespacedKey overloadLengthKey;
    public static NamespacedKey upgradeKey;
    public static NamespacedKey overloadUpgradeKey;
    public static NamespacedKey launchCooldownKey;
    
    public static ArrayList<Task> tasks;
    public HandleAbilities abilities;

    private ShapedRecipe upgrade1;
    private ShapedRecipe upgrade2;
    private ShapedRecipe upgrade3;
    public ItemManager itemManager;

    private List<String> blockedCommands = new ArrayList<>();

    @Override
    public void onEnable()
    {
        itemManager = new ItemManager(this);
        abilities = new HandleAbilities(this);
        tasks = new ArrayList<Task>();
        tasks.add(new ShowStars(this));

        starsKey = new NamespacedKey(this, "stars");
        overloadLengthKey = new NamespacedKey(this, "overloadTime");
        upgradeKey = new NamespacedKey(this, "upgrade");
        overloadUpgradeKey = new NamespacedKey(this, "overloadUpgrade");
        launchCooldownKey = new NamespacedKey(this, "launchCooldown");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(abilities, this);
        
        this.getCommand("setstars").setExecutor(new SetStars(this));
        this.getCommand("withdraw").setExecutor(new Withdraw(this));
        this.getCommand("giveitems").setExecutor(new GiveItems(this));
        this.getCommand("resetcooldown").setExecutor(new ResetCooldown(this));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
        {
            public void run()
            {
                ArrayList<Task> _tasks = new ArrayList<Task>();
                for(Task t : tasks)
                    _tasks.add(t);
                
                ArrayList<Task> toRemove = new ArrayList<Task>();

                for(Task t : _tasks)
                {
                    t.tick();

                    if(t.cancel)
                        toRemove.add(t);
                }

                for(Task t : toRemove)
                {
                    t.onCancel();
                    tasks.remove(t);
                }
            }
        }, 0, 1);

        Bukkit.resetRecipes();
        addRecipes();

        getLogger().info("Overloading since 1987 (made by roo)");
    }

    private void addRecipes()
    {
        {
        ItemStack item = new ItemStack(Material.GRAY_DYE);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§7§l§oSpeed Enhancement");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(this, "upgrade1");
        upgrade1 = new ShapedRecipe(key, item);
        upgrade1.shape("DDD", "DxD", "DDD");

        upgrade1.setIngredient('D', Material.DIAMOND);
        upgrade1.setIngredient('x', Material.NETHER_STAR);

        Bukkit.addRecipe(upgrade1);
        }

        {
        ItemStack item = new ItemStack(Material.LIGHT_BLUE_DYE);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§l§oEndurance Enhancement");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(this, "upgrade2");
        upgrade2 = new ShapedRecipe(key, item);
        upgrade2.shape("DDD", "BxO", "DDD");

        upgrade2.setIngredient('D', Material.DIAMOND);
        upgrade2.setIngredient('O', Material.OBSIDIAN);
        upgrade2.setIngredient('B', Material.BLAZE_ROD);
        upgrade2.setIngredient('x', Material.NETHER_STAR);

        Bukkit.addRecipe(upgrade2);
        }

        {
        ItemStack item = new ItemStack(Material.YELLOW_DYE);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§l§oToughness Enhancement");
        meta.setCustomModelData(1);
        item.setItemMeta(meta);

        NamespacedKey key = new NamespacedKey(this, "upgrade3");
        upgrade3 = new ShapedRecipe(key, item);
        upgrade3.shape("DND", "BxO", "DDD");

        upgrade3.setIngredient('D', Material.DIAMOND);
        upgrade3.setIngredient('O', Material.OBSIDIAN);
        upgrade3.setIngredient('B', Material.BLAZE_ROD);
        upgrade3.setIngredient('N', Material.NETHERITE_INGOT);
        upgrade3.setIngredient('x', Material.NETHER_STAR);

        Bukkit.addRecipe(upgrade3);
        }
    }

    @EventHandler
    private void starsInCraft(PrepareItemCraftEvent event)
    {
        if(event.getRecipe() == null)
            return;

        ItemStack star = new ItemStack(Material.NETHER_STAR);

        ItemMeta meta = star.getItemMeta();
        meta.setDisplayName("§e§l§oStar");
        star.setItemMeta(meta);
        if(event.getRecipe().getResult().getType() == Material.BEACON)
        {
            ItemStack compare = event.getInventory().getItem(5);

            if(compare.hasItemMeta() || compare.getItemMeta().getDisplayName().equals(star.getItemMeta().getDisplayName()))
                event.getInventory().setResult(new ItemStack(Material.AIR));
        }

        if(!event.getRecipe().getResult().hasItemMeta())
            return;

        if(event.getRecipe().getResult().getItemMeta().getDisplayName().equals(upgrade1.getResult().getItemMeta().getDisplayName()))
        {
            star.setAmount(1);
            ItemStack compare = event.getInventory().getItem(5);

            if(!(compare.hasItemMeta() && compare.getItemMeta().getDisplayName().equals(star.getItemMeta().getDisplayName()) && compare.getAmount() == star.getAmount()))
                event.getInventory().setResult(new ItemStack(Material.AIR));
        }

        if(event.getRecipe().getResult().getItemMeta().getDisplayName().equals(upgrade2.getResult().getItemMeta().getDisplayName()))
        {
            star.setAmount(2);
            ItemStack compare = event.getInventory().getItem(5);

            if(!(compare.hasItemMeta() && compare.getItemMeta().getDisplayName().equals(star.getItemMeta().getDisplayName()) && compare.getAmount() == star.getAmount()))
                event.getInventory().setResult(new ItemStack(Material.AIR));
        }

        if(event.getRecipe().getResult().getItemMeta().getDisplayName().equals(upgrade3.getResult().getItemMeta().getDisplayName()))
        {
            star.setAmount(3);
            ItemStack compare = event.getInventory().getItem(5);

            if(!(compare.hasItemMeta() && compare.getItemMeta().getDisplayName().equals(star.getItemMeta().getDisplayName()) && compare.getAmount() == star.getAmount()))
                event.getInventory().setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    private void removeExtraStars(CraftItemEvent event)
    {
        if(event.getRecipe() == null || !event.getRecipe().getResult().hasItemMeta())
            return;

        String name = event.getRecipe().getResult().getItemMeta().getDisplayName();
        
        if(name.equals(upgrade1.getResult().getItemMeta().getDisplayName()) || name.equals(upgrade2.getResult().getItemMeta().getDisplayName()) || name.equals(upgrade3.getResult().getItemMeta().getDisplayName()))
        {
            event.getInventory().getItem(5).setAmount(0);
        }
    }

    @EventHandler
    private void checkStars(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(!data.has(starsKey, PersistentDataType.INTEGER))
        {
            data.set(starsKey, PersistentDataType.INTEGER, 0);
            data.set(upgradeKey, PersistentDataType.INTEGER, 0);
            data.set(overloadLengthKey, PersistentDataType.INTEGER, 0);
            data.set(launchCooldownKey, PersistentDataType.INTEGER, 0);
        }
    }

    public void setStars(Player player, int stars)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        if(stars > STAR_MAX)
            stars = STAR_MAX;
        data.set(starsKey, PersistentDataType.INTEGER, stars);
    }

    public void addStar(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        int stars = data.get(starsKey, PersistentDataType.INTEGER) + 1;
        if(stars > STAR_MAX)
        {
            giveStar(player, 1);
            player.sendMessage(ChatColor.BLUE + "You were given a star because you are at max!");
        }
        else
            data.set(starsKey, PersistentDataType.INTEGER, stars);
    }

    public void giveStar(Player player, int count)
    {
        for(int i = 0; i < count; i++)
        if(player.getInventory().firstEmpty() != -1)
            player.getInventory().addItem(itemManager.star);
        else
            player.getWorld().dropItemNaturally(player.getLocation(), itemManager.star);
    }

    public boolean isOverloaded(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();

        return (data.get(overloadLengthKey, PersistentDataType.INTEGER) > 0);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event)
    {
        event.getEntity().getPersistentDataContainer().set(overloadLengthKey, PersistentDataType.INTEGER, 0);
        event.getEntity().getPersistentDataContainer().set(launchCooldownKey, PersistentDataType.INTEGER, 0);
        Player killer = event.getEntity().getKiller();
        if(killer != null)
        {
            addStar(killer);
        }
    }

    public int getStars(Player player)
    {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.get(starsKey, PersistentDataType.INTEGER);
    }

    @EventHandler
	private void onPlayerTab(PlayerCommandSendEvent e)
    {
		e.getCommands().removeAll(blockedCommands);
	}

    public static Entity getTarget(Player player, int range)
    {
        Predicate<Entity> p = new Predicate<Entity>() {

            @Override
            public boolean apply(Entity input)
            {
                return(input != player);
            }
            
        };
        RayTraceResult ray = player.getWorld().rayTrace(player.getEyeLocation(), player.getLocation().getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, p);
        
        if(ray != null)
            return ray.getHitEntity();
        else
            return null;
    }

    public static Block getBlock(Player player, int range)
    {
        return getBlock(player, range, player.getLocation().getPitch());
    }

    public static Block getBlock(Player player, int range, float pitch)
    {
        Predicate<Entity> p = new Predicate<Entity>() {

            @Override
            public boolean apply(Entity input)
            {
                return(input != player);
            }
            
        };

        Location dir = player.getLocation().clone();
        dir.setPitch(pitch);

        RayTraceResult ray = player.getWorld().rayTrace(player.getEyeLocation(), dir.getDirection(), range, FluidCollisionMode.NEVER, true, 0.2, p);
        
        if(ray != null)
            return ray.getHitBlock();
        else
            return null;
    }

    public static List<Block> getSphereBlocks(Location location, int radius)
    {
        List<Block> blocks = new ArrayList<>();

        int bx = location.getBlockX();
        int by = location.getBlockY();
        int bz = location.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++)
        {
            for (int y = by - radius; y <= by + radius; y++)
            {
                for (int z = bz - radius; z <= bz + radius; z++)
                {
                    double distance = ((bx - x) * (bx - x) + (bz - z) * (bz - z) + (by - y) * (by - y));
                    if (distance < radius * radius && (distance < (radius - 1) * (radius - 1)))
                    {
                        Block block = new Location(location.getWorld(), x, y, z).getBlock();
                        if(block.getType() != Material.AIR && block.getType() != Material.BEDROCK)
                            blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }

    public static Object[] getNearbyEntities(Location where, int range)
    {
        return where.getWorld().getNearbyEntities(where, range, range, range).toArray();
    }

    public static double ClampD(double v, double min, double max)
    {
        if(v < min)
            return min;
        else if(v > max)
            return max;
        else
            return v;
    }
}
