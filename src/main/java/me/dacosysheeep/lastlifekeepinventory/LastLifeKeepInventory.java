package me.dacosysheeep.lastlifekeepinventory;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class LastLifeKeepInventory extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(this, this);
            getLogger().info("LastLifeKeepInventory has started.");
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required for us to access the amount of lives each player has.");
            Bukkit.getPluginManager().disablePlugin(this);
        }


    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String placeholderValue=PlaceholderAPI.setPlaceholders(e.getPlayer(), "%lastlife_life%");
        try {
            char liveschar=placeholderValue.charAt(5);
            int playerlives=Character.getNumericValue(liveschar);
            String joinText="%lastlife_player% §aJoined the server! §c they have %lastlife_color%"+playerlives+" lives.";
            joinText=PlaceholderAPI.setPlaceholders(e.getPlayer(), joinText);
            getLogger().info(PlaceholderAPI.setPlaceholders(e.getPlayer(), " %lastlife_player% joined %lastlife_life%"));
            e.setJoinMessage(joinText);
        } catch (NumberFormatException error) {
            System.err.println("Error converting PlaceholderAPI value to integer.");
        } catch (StringIndexOutOfBoundsException error) {
            //getLogger().warning("There was an error generating the join message. This is probably because the player hasn't joined before.");
            getLogger().warning(String.format("Unable to determine lives for %s. This is probably because they haven't joined before.", e.getPlayer().getName()));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        getLogger().info("LastLifeKeepInventory detected a player death.");
        try {
            int lives=Character.getNumericValue(PlaceholderAPI.setPlaceholders(e.getEntity(), "%lastlife_life%").charAt(5));
            e.getDrops().clear();
            if (lives==1){
                getServer().broadcastMessage("§c"+e.getEntity().getName()+" lost their final life. They are now out of the series unless someone revives them.");
                for (ItemStack item : e.getEntity().getInventory().getContents()) {
                    if (item != null) {
                        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
                    }
                    e.getEntity().getInventory().clear();
                }

            }
            //e.setDeathMessage(PlaceholderAPI.setPlaceholders(e.getEntity(), ChatColor.RED+""+e.getEntity().getName()+" died. They now have "));
        } catch (NumberFormatException error) {
            getLogger().warning("Error converting PlaceholderAPI value to integer.");
        } catch (Exception error) {
            getLogger().warning("Error calculating how many lives player had.");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
