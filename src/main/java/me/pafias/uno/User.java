package me.pafias.uno;

import me.pafias.uno.game.Card;
import me.pafias.uno.game.CardType;
import me.pafias.uno.game.Game;
import me.pafias.uno.game.events.CardDrawnEvent;
import me.pafias.uno.game.events.CardPlayedEvent;
import me.pafias.uno.utils.CC;
import me.pafias.uno.utils.RandomUtils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private static final Uno plugin = Uno.get();

    private final Player player;
    private Location lastLocation;
    private List<Card> hand;
    private Game game;
    private ArmorStand seatAS;
    private Location seatLoc;
    private ItemFrame itemFrame;
    private Location frameLoc;
    private boolean uno;
    private Inventory inventory;

    public User(Player player) {
        this.player = player;
        hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addToHand(Card card) {
        hand.add(card);
        if (inventory != null && inventory.getViewers().contains(player)) {
            int firstFreeSlot = inventory.firstEmpty();
            inventory.setItem(firstFreeSlot, card.getInventoryItem(true));
            player.updateInventory();
        }
    }

    public void removeFromHand(Card card) {
        hand.remove(card);
        if (inventory != null && inventory.getViewers().contains(player)) {
            inventory.removeItem(card.getInventoryItem(true));
            player.updateInventory();
        }
    }

    public boolean attemptPlay(ItemStack inventoryItem) {
        Card card;
        card = hand.stream().filter(c -> c.getInventoryItem(true).equals(inventoryItem)).findAny().orElse(null);
        if (card != null)
            return attemptPlay(card);
        else return false;
    }

    public boolean attemptPlay(Card card) {
        if (!game.getCurrentPlayer().equals(this)) return false;
        Card onPile = game.getDiscardPile().getCurrentCard();
        if (onPile.getColor() == null) {
            play(card);
            return true;
        }
        if (card.getType().equals(CardType.WILD) || card.getType().equals(CardType.WILDDRAW4)) {
            play(card);
            return true;
        } else if ((card.getType().equals(CardType.SKIP) || card.getType().equals(CardType.REVERSE) || card.getType().equals(CardType.DRAW2)) && (card.getColor().equals(onPile.getColor()) || card.getType().equals(onPile.getType()))) {
            play(card);
            return true;
        } else if (card.getType().equals(CardType.NUMBER)) {
            if (card.getColor().equals(onPile.getColor()) || (onPile.getNumber() != null && card.getNumber().equals(onPile.getNumber()))) {
                play(card);
                return true;
            }
        }
        return false;
    }

    public void play(Card card) {
        removeFromHand(card);
        game.getDiscardPile().addToPile(card);
        plugin.getServer().getPluginManager().callEvent(new CardPlayedEvent(card, this));
        game.broadcast(String.format("&b%s &6played a %s", getName(), card.getName()));
        if (hand.isEmpty()) {
            if (!uno) {
                draw(4, true);
                game.broadcast(String.format("&b%s &6was going to win, but has to draw &l+4 &r&6instead as he didn't say UNO", getName()));
            } else {
                game.handleWin(this);
                return;
            }
        }
        if (!card.getType().equals(CardType.NUMBER))
            game.handleSpecialCard(this, card);
        else
            game.nextPlayer(false);
    }

    public void draw(int amount, boolean silent) {
        for (int i = 0; i < amount; i++) {
            Card card = game.getDeck().getNextCardAndDiscard();
            addToHand(card);
            plugin.getServer().getPluginManager().callEvent(new CardDrawnEvent(card, this));
        }
        if (!silent)
            game.broadcast(String.format("&b%s &6drew &7%d &6%s", getName(), amount, amount == 1 ? "card" : "cards"));
    }

    public void showHandIngame() {
        inventory = plugin.getServer().createInventory(player, 54, CC.translate("&c&lYour hand"));
        hand.forEach(card -> {
            int firstFreeSlot = inventory.firstEmpty();
            inventory.setItem(firstFreeSlot, card.getInventoryItem(true));
        });
        player.getInventory().setItemInOffHand(game.getDiscardPile().getCurrentCard().getMapItem(true)); // todo update when someone plays and inv is open
        player.openInventory(inventory);
    }

    public void hideHandIngame() {
        player.getInventory().setItemInOffHand(null);
    }

    public void setSeat(int playerNumber) {
        seatLoc = RandomUtils.parseLocation(plugin.getConfig().getString("player" + playerNumber + "_seat"));
        lastLocation = player.getLocation();
        player.teleport(seatLoc);
        frameLoc = RandomUtils.parseLocation(plugin.getConfig().getString("player" + playerNumber + "_itemframe"));
        seatAS = (ArmorStand) seatLoc.getWorld().spawnEntity(seatLoc.add(0, -1.5, 0), EntityType.ARMOR_STAND);
        seatAS.setArms(false);
        seatAS.setBasePlate(false);
        seatAS.setGravity(false);
        seatAS.setVisible(false);
        seatAS.setPassenger(player);
        Entity closest = null;
        double lastDistance = Double.MAX_VALUE;
        for (Entity e : frameLoc.getChunk().getEntities()) {
            double distance = frameLoc.distance(e.getLocation());
            if (distance < lastDistance) {
                lastDistance = distance;
                closest = e;
            }
        }
        itemFrame = (ItemFrame) closest;
        itemFrame.setFixed(true);
    }


    // May come in handy for the 7-0 rule
    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUUID() {
        return player.getUniqueId();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isInGame() {
        return game != null;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ArmorStand getSeatAS() {
        return seatAS;
    }

    public ItemFrame getItemFrame() {
        if (itemFrame == null)
            itemFrame = (ItemFrame) frameLoc.getWorld().spawnEntity(frameLoc, EntityType.ITEM_FRAME, CreatureSpawnEvent.SpawnReason.CUSTOM, frame -> {
                ((ItemFrame) frame).setItem(game.getDiscardPile().getCurrentCard().getMapItem(true), false);
                ((ItemFrame) frame).setFixed(true);
            });
        return itemFrame;
    }

    public boolean getUNO() {
        return uno;
    }

    public void setUNO(boolean uno) {
        this.uno = uno;
    }

    public Inventory getCardsInventory() {
        return inventory;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

}
