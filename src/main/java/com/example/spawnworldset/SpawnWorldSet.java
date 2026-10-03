package com.example.spawnworldset;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnWorldSet extends JavaPlugin implements CommandExecutor, Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        if (getCommand("spawnworldset") != null) {
            getCommand("spawnworldset").setExecutor(this);
        }
        
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ta komenda może być użyta tylko przez gracza!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("spawnworldset.admin")) {
            player.sendMessage(ChatColor.RED + "Nie masz uprawnień do tej komendy!");
            return true;
        }

        Location loc = player.getLocation();
        
        getConfig().set("first-spawn.world", loc.getWorld().getName());
        getConfig().set("first-spawn.x", loc.getX());
        getConfig().set("first-spawn.y", loc.getY());
        getConfig().set("first-spawn.z", loc.getZ());
        getConfig().set("first-spawn.yaw", (double) loc.getYaw());
        getConfig().set("first-spawn.pitch", (double) loc.getPitch());
        saveConfig();

        player.sendMessage(ChatColor.GREEN + "Pomyślnie ustawiono miejsce pierwszego spawnu dla nowych graczy!");
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            if (getConfig().contains("first-spawn.world")) {
                String worldName = getConfig().getString("first-spawn.world");
                
                if (worldName != null && Bukkit.getWorld(worldName) != null) {
                    double x = getConfig().getDouble("first-spawn.x");
                    double y = getConfig().getDouble("first-spawn.y");
                    double z = getConfig().getDouble("first-spawn.z");
                    float yaw = (float) getConfig().getDouble("first-spawn.yaw");
                    float pitch = (float) getConfig().getDouble("first-spawn.pitch");

                    Location firstSpawn = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
                    player.teleport(firstSpawn);

                    String mainTitle = ChatColor.AQUA + "" + ChatColor.BOLD + "WITAJ NA SERWERZE!";
                    String subTitle = ChatColor.DARK_AQUA + "Życzymy miłej gry!";
                    
                    player.sendTitle(mainTitle, subTitle, 10, 70, 20);
                }
            }
        }
    }
}
