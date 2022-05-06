package me.pafias.uno.game.events;

import me.pafias.uno.User;
import me.pafias.uno.game.Card;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CardDrawnEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Card cardDrawn;
    private final User user;

    public CardDrawnEvent(Card cardDrawn, User player) {
        super(player.getPlayer());
        this.cardDrawn = cardDrawn;
        user = player;
    }

    public Card getCardDrawn() {
        return cardDrawn;
    }

    public User getWhoDrew() {
        return user;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
