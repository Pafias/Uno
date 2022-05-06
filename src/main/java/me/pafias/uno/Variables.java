package me.pafias.uno;

import org.bukkit.scheduler.BukkitRunnable;

public class Variables {

    private final Uno plugin;

    public Variables(Uno plugin) {
        this.plugin = plugin;
        reloadConfigs();
    }

    public void reloadConfigs() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!plugin.getDataFolder().exists())
                    plugin.getDataFolder().mkdirs();
                reloadConfigYML();
            }
        }.runTaskAsynchronously(plugin);
    }

    // config.yml
    public int maxPlayers = 4;

    public void reloadConfigYML() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveConfig();
        plugin.reloadConfig();
        maxPlayers = plugin.getConfig().getInt("max_players");
    }

}
