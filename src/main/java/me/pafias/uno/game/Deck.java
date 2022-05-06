package me.pafias.uno.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards;

    public Deck() {
        cards = getNewShuffledDeck();
    }

    public Card getNextCardAndDiscard() {
        if (cards.isEmpty())
            cards = getNewShuffledDeck();
        Card next = cards.stream().iterator().next();
        cards.remove(next);
        return next;
    }

    public List<Card> getNewShuffledDeck() {
        return shuffleDeck(getNewDeck());
    }

    public List<Card> getNewDeck() {
        List<Card> cards = new ArrayList<>();
        for (CardColor color : CardColor.values())
            for (int i = 0; i < 10; i++)
                cards.add(new Card(color, CardType.NUMBER, i));
        for (CardColor color : CardColor.values()) {
            cards.add(new Card(color, CardType.SKIP, null));
            cards.add(new Card(color, CardType.REVERSE, null));
            cards.add(new Card(color, CardType.DRAW2, null));
        }
        for (int i = 0; i < 4; i++)
            cards.add(new Card(null, CardType.WILD, null));
        for (int i = 0; i < 4; i++)
            cards.add(new Card(null, CardType.WILDDRAW4, null));
        return cards;
    }

    public List<Card> shuffleDeck() {
        return shuffleDeck(cards);
    }

    public List<Card> shuffleDeck(List<Card> deck) {
        Collections.shuffle(deck);
        return deck;
    }

}
