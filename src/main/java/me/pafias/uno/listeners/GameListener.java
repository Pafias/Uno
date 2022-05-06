package me.pafias.uno.listeners;

import me.pafias.uno.Uno;
import me.pafias.uno.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class GameListener implements Listener {

    private final Uno plugin;

    public GameListener(Uno plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user.isInGame()) event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (user.isInGame()) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        User user = plugin.getSM().getUserManager().getUser((Player) event.getEntity());
        if (user.isInGame()) event.setCancelled(true);
    }

    /* ---- Moved this to InventoryListener
    @EventHandler
    public void onLeaveSeat(VehicleExitEvent event) {
        if (!(event.getExited() instanceof Player)) return;
        User user = plugin.getSM().getUserManager().getUser((Player) event.getExited());
        if (user.isInGame()) event.setCancelled(true);
    }
     */

}
