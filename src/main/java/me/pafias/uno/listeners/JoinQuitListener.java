package me.pafias.uno.listeners;

import me.pafias.uno.Uno;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private final Uno plugin;

    public JoinQuitListener(Uno plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        plugin.getSM().getUserManager().addUser(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        plugin.getSM().getUserManager().removeUser(event.getPlayer());
    }

}
