package me.pafias.uno.game;

import me.pafias.uno.Uno;
import me.pafias.uno.User;
import me.pafias.uno.utils.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.text.SimpleDateFormat;
import java.util.*;

public class Game {

    private final Uno plugin = Uno.get();

    // Game vars
    private List<User> players;
    private Deck deck;
    private DiscardPile discardPile;


    // vars
    private String gameID;
    public int maxPlayers;
    public boolean started = false;
    private Long startTime = -1L;
    private ListIterator<User> playerIterator;
    private Set<BukkitTask> tasks = new HashSet<>();
    private int turnTime = 20;

    // temp vars
    private User currentPlayer;
    private int direction = 1;
    private int timeLeft = turnTime;

    // Minecraft
    private Scoreboard gameScoreboard;
    private Objective sbObjective;

    public Game(String gameID) {
        this.gameID = gameID;
        maxPlayers = plugin.getSM().getVariables().maxPlayers;
        players = new ArrayList<>();
        handleGameScoreboard();
    }

    public void start() {
        if (players.size() < 2) return;
        deck = new Deck();
        handlePlayers();
        discardPile = new DiscardPile(this, deck.getNextCardAndDiscard());
        broadcast("&6Game started!");
        playerIterator = players.listIterator(0);
        nextPlayer(false);
        broadcast(String.format("&6It's &b%s&6's turn.", currentPlayer.getName()));
        handleTurnTimer();
        handleDirectionPointer();
        startTime = System.currentTimeMillis();
        started = true;
    }

    public void handleSpecialCard(User user, Card card) {
        User next = getNextPlayer();
        switch (card.getType()) {
            case DRAW2:
                next.draw(2, true);
                broadcast(String.format("&b%s &6drew &l+2", next.getName()));
                nextPlayer(false);
                break;
            case SKIP:
                nextPlayer(true);
                broadcast(String.format("&b%s&6's turn has been skipped", next.getName()));
                nextPlayer(false);
                break;
            case REVERSE:
                direction *= -1;
                Collections.reverse(players);
                playerIterator = players.listIterator(0);
                nextPlayer(false);
                break;
            case WILD:
                int i = 1;
                for (CardColor color : CardColor.values()) {
                    Card temp = new Card(color, CardType.NUMBER, 1);
                    user.getPlayer().getInventory().setItem(i, temp.getInventoryItem(true));
                    i += 2;
                }
                break;
            case WILDDRAW4:
                int ii = 1;
                for (CardColor color : CardColor.values()) {
                    Card temp = new Card(color, CardType.NUMBER, 1);
                    user.getPlayer().getInventory().setItem(ii, temp.getInventoryItem(true));
                    ii += 2;
                }
                next.draw(4, true);
                broadcast(String.format("&b%s &6drew &l+4", next.getName()));
                break;
        }
    }

    public void nextPlayer(boolean silent) {
        if (currentPlayer != null) {
            currentPlayer.getPlayer().setLevel(0);
            currentPlayer.getPlayer().setExp(0);
        }
        if (!playerIterator.hasNext())
            playerIterator = players.listIterator(0);
        timeLeft = turnTime;
        currentPlayer = playerIterator.next();
        if (!silent)
            currentPlayer.getPlayer().sendTitle(CC.translate("&d&lYour turn"), "", 10, 10, 10);
    }

    public void handlePlayers() {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setSeat(i + 1);
            players.get(i).draw(Rules.BEGINNING_CARD_COUNT, true);
        }
    }

    private void handleGameScoreboard() {
        gameScoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
        sbObjective = gameScoreboard.registerNewObjective("game", "dummy");
        sbObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        sbObjective.setDisplayName(CC.translate("&a&lMinecraft &c&lUNO"));
        Team timeLeftTeam = gameScoreboard.registerNewTeam("sb_time");
        timeLeftTeam.addEntry(CC.translate("&a"));
        Team currentPlayerTeam = gameScoreboard.registerNewTeam("sb_currentplayer");
        currentPlayerTeam.addEntry(CC.translate("&b"));
        Team timeElapsedTeam = gameScoreboard.registerNewTeam("sb_timeelapsed");
        timeElapsedTeam.addEntry(CC.translate("&c"));
        tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                timeLeftTeam.setPrefix(CC.translate(String.format("&a%d", timeLeft)));
                currentPlayerTeam.setPrefix(CC.translate(String.format("&a%s", currentPlayer != null ? currentPlayer.getName() : "Nobody")));
                timeElapsedTeam.setPrefix(CC.translate(String.format("&a%s", getGameTimer())));

                sbObjective.getScore(CC.translate("   ")).setScore(10);
                sbObjective.getScore(CC.translate("&cTime elapsed:")).setScore(9);
                sbObjective.getScore(CC.translate("&c")).setScore(8);
                sbObjective.getScore(CC.translate("  ")).setScore(7);
                sbObjective.getScore(CC.translate("&cCurrent player:")).setScore(6);
                sbObjective.getScore(CC.translate("&b")).setScore(5);
                sbObjective.getScore(CC.translate(" ")).setScore(4);
                sbObjective.getScore(CC.translate("&cRemaining time to play:")).setScore(3);
                sbObjective.getScore(CC.translate("&a")).setScore(2);
                sbObjective.getScore(CC.translate("")).setScore(1);
                sbObjective.getScore(CC.translate("&7pafias.tk")).setScore(0);

                players.forEach(player -> player.getPlayer().setScoreboard(gameScoreboard));
            }
        }.runTaskTimerAsynchronously(plugin, 20, 20));
    }

    private void handleTurnTimer() {
        tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                if (timeLeft <= 0) {
                    currentPlayer.getPlayer().sendTitle(CC.translate("&cYou lost your turn"), CC.translate("&cYou took too long to play"), 10, 20, 10);
                    currentPlayer.draw(1, true);
                    currentPlayer.getPlayer().getInventory().clear();
                    broadcast(String.format("&b%s&6's turn got skipped as he ran out of time to play", currentPlayer.getName()));
                    nextPlayer(false);
                }
                if (timeLeft > 0 && timeLeft <= 5)
                    currentPlayer.getPlayer().playSound(currentPlayer.getPlayer().getEyeLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.1f);
                currentPlayer.getPlayer().setExp((float) timeLeft / turnTime);
                currentPlayer.getPlayer().setLevel(timeLeft);
                timeLeft--;
            }
        }.runTaskTimer(plugin, 20, 20));
    }

    private String getGameTimer() {
        if (startTime == -1) return "Not started";
        return new SimpleDateFormat("mm:ss").format(new Date(System.currentTimeMillis() - startTime));
    }

    public void handleDirectionPointer() {
        tasks.add(new BukkitRunnable() {
            @Override
            public void run() {
                CardColor color = discardPile.getCurrentCard().getColor();
                ChatColor cc = color != null ? color.getChatColor() : ChatColor.BLACK;
                players.forEach(player -> player.getPlayer().sendActionBar(cc + (direction == 1 ? "->->->->->->" : "<-<-<-<-<-<-")));
            }
        }.runTaskTimer(plugin, 20, 40));
    }

    public void handleWin(User winner) {
        broadcast("");
        broadcast(CC.translate(String.format("&b%s &6won the game", winner.getName())));
        broadcast("");
        stop();
    }

    public void stop() {
        tasks.forEach(BukkitTask::cancel);
        started = false;
        plugin.getSM().getGameManager().removeGame(this);
        sbObjective.unregister();
        gameScoreboard.clearSlot(DisplaySlot.SIDEBAR);
        gameScoreboard.getTeams().forEach(Team::unregister);
        players.forEach(player -> {
            player.setGame(null);
            player.setHand(null);
            player.getPlayer().eject();
            player.getSeatAS().remove();
            player.getPlayer().setLevel(0);
            player.getPlayer().setExp(0);
            player.getPlayer().teleport(player.getLastLocation());
            player.getItemFrame().setItem(new ItemStack(Material.AIR), false);
        });
    }

    public List<User> getPlayers() {
        return players;
    }

    public void addPlayer(User user) {
        players.add(user);
        user.setGame(this);
        user.getPlayer().getInventory().clear();
        user.getPlayer().sendMessage(CC.translate("&aYou have been added to the game"));
    }

    public void removePlayer(User user) {
        players.remove(user);
        if (players.size() <= 1) {
            stop();
            broadcast("Game stopped because there were not enough players left");
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public DiscardPile getDiscardPile() {
        return discardPile;
    }

    public User getCurrentPlayer() {
        return currentPlayer;
    }

    public User getNextPlayer() {
        if (!playerIterator.hasNext())
            playerIterator = players.listIterator(0);
        return players.get(playerIterator.nextIndex());
    }

    public void broadcast(String message) {
        players.forEach(player -> player.getPlayer().sendMessage(CC.translate(message)));
    }

    public String getGameID() {
        return gameID;
    }

}
