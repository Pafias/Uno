package me.pafias.uno.listeners.game;

import me.pafias.uno.Uno;
import me.pafias.uno.User;
import me.pafias.uno.game.Card;
import me.pafias.uno.game.CardColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WildCardListener implements Listener {

    private final Uno plugin;

    public WildCardListener(Uno plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        User user = plugin.getSM().getUserManager().getUser(event.getPlayer());
        if (!user.isInGame()) return;
        ItemStack item = user.getPlayer().getInventory().getItemInMainHand();
        if (item == null || item.getType().equals(Material.AIR)) return;
        if (!item.getType().name().contains("WOOL")) return;
        Card card = user.getGame().getDiscardPile().getCurrentCard();
        switch (item.getType()) {
            case RED_WOOL:
            case YELLOW_WOOL:
                card.setColor(CardColor.valueOf(item.getType().name().split("_")[0]));
                break;
            case LIGHT_BLUE_WOOL:
                card.setColor(CardColor.BLUE);
                break;
            case LIME_WOOL:
                card.setColor(CardColor.GREEN);
                break;
        }
        user.getGame().broadcast("&6The chosen color is &l" + card.getColor().getName());
        user.getPlayer().getInventory().clear();
        user.getGame().nextPlayer(false);
    }

}
