package me.pafias.uno.listeners.game;

import me.pafias.uno.Uno;
import me.pafias.uno.User;
import me.pafias.uno.game.events.CardDrawnEvent;
import me.pafias.uno.utils.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class UnoChatListener implements Listener {

    private final Uno plugin;

    public UnoChatListener(Uno plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (!user.isInGame()) return;
        if (user.getHand().size() != 1 && user.getHand().size() != 2) return;
        if (event.getMessage().toLowerCase().startsWith("uno")) {
            event.setCancelled(true);
            if (user.getUNO()) {
                user.getPlayer().sendMessage(CC.translate("&cYou already said UNO"));
                return;
            }
            if (user.getHand().size() > 2) {
                user.getPlayer().sendMessage(CC.translate("&cYou have too many cards to say UNO"));
                return;
            }
            user.setUNO(true);
            user.getGame().getPlayers().forEach(player -> player.getPlayer().sendTitle(CC.translate("&cUNO!"), CC.translate(String.format("&cby &b%s", user.getName())), 10, 10, 10));
        }
    }

    @EventHandler
    public void onCardDrawn(CardDrawnEvent event) {
        if (!event.getWhoDrew().isInGame()) return;
        if (event.getWhoDrew().getHand().size() > 2)
            event.getWhoDrew().setUNO(false);
    }

}
