package me.pafias.uno.game;

import java.util.ArrayList;
import java.util.List;

public class DiscardPile {

    private Game game;
    private List<Card> cards;

    public DiscardPile(Game game, Card startCard) {
        this.game = game;
        cards = new ArrayList<>();
        addToPile(startCard);
    }

    public List<Card> getDiscardPile() {
        return cards;
    }

    public void addToPile(Card card) {
        cards.add(card);
        game.getPlayers().forEach(player -> {
            if (player.getItemFrame() != null)
                player.getItemFrame().setItem(card.getMapItem(true), false);
        });
    }

    public Card getCurrentCard() {
        return cards.get(cards.size() - 1);
    }

    public Game getGame() {
        return game;
    }

}
