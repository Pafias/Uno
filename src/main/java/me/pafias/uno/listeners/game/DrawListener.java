package me.pafias.uno.listeners.game;

import me.pafias.uno.Uno;
import me.pafias.uno.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class DrawListener implements Listener {

    private final Uno plugin;

    public DrawListener(Uno plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (!user.isInGame()) return;
        if (!event.hasBlock()) return;
        if (!event.getClickedBlock().getType().name().toLowerCase().contains("button")) return;
        if (!user.getGame().getCurrentPlayer().equals(user) || !user.getPlayer().getInventory().isEmpty()) {
            event.setCancelled(true);
            return;
        }
        user.draw(1, false);
        user.getGame().nextPlayer(false);
    }

}
