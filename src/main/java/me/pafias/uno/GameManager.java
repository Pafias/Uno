package me.pafias.uno;

import me.pafias.uno.game.Game;
import me.pafias.uno.utils.CC;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class GameManager {

    private final Uno plugin;

    public GameManager(Uno plugin) {
        this.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getSM().getGameManager().getGames().isEmpty())
                    plugin.getSM().getGameManager().createGame();
            }
        }.runTaskTimer(plugin, 40, (5 * 20));
    }

    private Set<Game> games = new HashSet<>();

    public Set<Game> getGames() {
        return games;
    }

    public void createGame() {
        String id = RandomStringUtils.random(5, true, true);
        Game game = new Game(id);
        games.add(game);
        plugin.getServer().getConsoleSender().sendMessage(CC.translate(String.format("&aGame with ID &d%s &acreated.", id)));
    }

    public Game getGame(String ID) {
        return games.stream().filter(game -> game.getGameID().equals(ID)).findAny().orElse(null);
    }

    public Game getGame(User user) {
        return games.stream().filter(game -> game.getPlayers().contains(user)).findAny().orElse(null);
    }

    public Game getRandomGame() {
        return games.stream().findAny().orElse(null);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

}
