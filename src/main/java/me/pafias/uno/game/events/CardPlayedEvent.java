package me.pafias.uno.game.events;

import me.pafias.uno.User;
import me.pafias.uno.game.Card;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class CardPlayedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final Card cardPlayed;
    private final User user;

    public CardPlayedEvent(Card cardPlayed, User player) {
        super(player.getPlayer());
        this.cardPlayed = cardPlayed;
        user = player;
    }

    public Card getCardPlayed() {
        return cardPlayed;
    }

    public User getWhoPlayed() {
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
