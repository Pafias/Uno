package me.pafias.uno.commands;

import me.pafias.uno.Uno;
import me.pafias.uno.User;
import me.pafias.uno.game.Game;
import me.pafias.uno.utils.CC;
import me.pafias.uno.utils.MapBuilder;
import me.pafias.uno.utils.RandomUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class UnoCommand implements CommandExecutor {

    private final Uno plugin;

    public UnoCommand(Uno plugin) {
        this.plugin = plugin;
    }

    private boolean help(CommandSender sender, String label) {
        sender.sendMessage(CC.translate("&f--------------- &aMinecraft &cUNO &f---------------"));
        sender.sendMessage(CC.translate("&7/" + label + " games &f- Show available games"));
        sender.sendMessage(CC.translate("&7/" + label + " join [id] &f- Join a game"));
        sender.sendMessage(CC.translate("&7/" + label + " start &f- Start the game you're in"));
        sender.sendMessage(CC.translate("&7/" + label + " hand &f- See your cards (shift in-game)"));
        if (sender.hasPermission("uno.admin")) {
            sender.sendMessage(CC.translate("&7/" + label + " reload &f- Reload configs"));
            sender.sendMessage(CC.translate("&7/" + label + " card <card> &f- Get card"));
            sender.sendMessage(CC.translate("&7/" + label + " icoords &f- Get coords of itemframe you're looking at"));
            sender.sendMessage(CC.translate("&7/" + label + " setiframe &f- Save coords of itemframe you're looking at"));
            sender.sendMessage(CC.translate("&7/" + label + " setseat &f- Save coords of seat you're standing on"));
            sender.sendMessage(CC.translate("&7/" + label + " create &f- Create a game"));
            sender.sendMessage(CC.translate("&7/" + label + " auto &f- Toggle automatic game creation"));
            sender.sendMessage(CC.translate("&7/" + label + " stop &f- Stop the game you're in"));
            sender.sendMessage(CC.translate("&7/" + label + " addplayer <player/*> &f- Add player/everyone to random game"));
            sender.sendMessage(CC.translate("&7/" + label + " removeplayer <player/*> &f- Remove player/everyone from their game"));
        }
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) return help(sender, label);
        if (args[0].equalsIgnoreCase("games")) {
            if (plugin.getSM().getGameManager().getGames().isEmpty()) {
                sender.sendMessage(CC.translate("&cThere are currently no games available."));
                return true;
            }
            sender.sendMessage(CC.translate("&6Games:"));
            plugin.getSM().getGameManager().getGames().forEach(game -> {
                sender.sendMessage("");
                BaseComponent[] msg = new ComponentBuilder(CC.translate(String.format("&8- &6ID: &d%s &7- &6Players: &b%d", game.getGameID(), game.getPlayers().size())))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join this game").create()))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/uno join %s", game.getGameID())))
                        .create();
                sender.sendMessage(msg);
            });
        } else if (args[0].equalsIgnoreCase("join")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            Game game;
            if (args.length >= 2) {
                game = plugin.getSM().getGameManager().getGame(args[1]);
                if (game == null) {
                    sender.sendMessage(CC.translate("&cGame not found."));
                    return true;
                }
            } else {
                game = plugin.getSM().getGameManager().getRandomGame();
                if (game == null) {
                    sender.sendMessage(CC.translate("&cNo games available."));
                    return true;
                }
            }
            if (game.getPlayers().size() >= game.maxPlayers) {
                sender.sendMessage(CC.translate("&cThe game you tried to join is full."));
                return true;
            }
            if (game.started) {
                sender.sendMessage(CC.translate("&cThe game you tried to join already started."));
                return true;
            }
            User user = plugin.getSM().getUserManager().getUser((Player) sender);
            if (user.getGame() != null) {
                sender.sendMessage(CC.translate("&cYou are already in a game."));
                return true;
            }
            game.addPlayer(user);
        } else if (args[0].equalsIgnoreCase("start")) {
            // if (!sender.hasPermission("uno.start")) return true;
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            User user = plugin.getSM().getUserManager().getUser((Player) sender);
            if (user.getGame() == null) {
                sender.sendMessage(CC.translate("&cYou are not in a game."));
                return true;
            }
            if (user.getGame().started) {
                sender.sendMessage(CC.translate("&cGame already started."));
                return true;
            }
            user.getGame().start();
        } else if (args[0].equalsIgnoreCase("hand")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            User user = plugin.getSM().getUserManager().getUser((Player) sender);
            if (user.getGame() == null) {
                sender.sendMessage(CC.translate("&cYou are not in a game."));
                return true;
            }
            if (!user.getGame().started) {
                sender.sendMessage(CC.translate("&cGame not started."));
                return true;
            }
            user.showHandIngame();
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("uno.reload")) return true;
            plugin.getSM().getVariables().reloadConfigs();
            sender.sendMessage(CC.translate("&aConfigs reloaded"));
        } else if (args[0].equalsIgnoreCase("card") && args.length >= 2) {
            if (!sender.hasPermission("uno.card")) return true;
            try {
                BufferedImage image = ImageIO.read(plugin.getResource(String.format("cards/%s.png", args[1])));
                image = RandomUtils.resize(image, 128, 128);
                ItemStack card = new MapBuilder().setRenderOnce(true).setImage(image).build();
                ((Player) sender).getInventory().addItem(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args[0].equalsIgnoreCase("icoords")) {
            if (!sender.hasPermission("uno.icoords")) return true;
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            Player player = (Player) sender;
            Entity entity = player.getTargetEntity(3);
            if (!(entity instanceof ItemFrame)) return true;
            player.sendMessage(String.format("x: %f y: %f z: %f", entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()));
        } else if (args[0].equalsIgnoreCase("setiframe") && args.length >= 2) {
            if (!sender.hasPermission("uno.setiframe")) return true;
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            Player player = (Player) sender;
            Entity entity = player.getTargetEntity(3);
            if (!(entity instanceof ItemFrame)) {
                sender.sendMessage(CC.translate("&cYou're not looking (close enough) at an ItemFrame"));
                return true;
            }
            int i;
            try {
                i = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(CC.translate("&cInvalid player number"));
                return true;
            }
            plugin.getConfig().set("player" + i + "_itemframe", RandomUtils.parseLocation(entity.getLocation()));
            plugin.saveConfig();
            sender.sendMessage(CC.translate("&aSaved"));
        } else if (args[0].equalsIgnoreCase("setseat") && args.length >= 2) {
            if (!sender.hasPermission("uno.setseat")) return true;
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            Player player = (Player) sender;
            int i;
            try {
                i = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(CC.translate("&cInvalid player number"));
                return true;
            }
            plugin.getConfig().set("player" + i + "_seat", RandomUtils.parseLocation(player.getLocation()));
            plugin.saveConfig();
            sender.sendMessage(CC.translate("&aSaved"));
        } else if (args[0].equalsIgnoreCase("create")) {
            if (!sender.hasPermission("uno.create")) return true;
            plugin.getSM().getGameManager().createGame();
            sender.sendMessage(CC.translate("&aGame created!"));
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (!sender.hasPermission("uno.stop")) return true;
            if (!(sender instanceof Player)) {
                sender.sendMessage(CC.translate("&cOnly players!"));
                return true;
            }
            User user = plugin.getSM().getUserManager().getUser((Player) sender);
            if (user.getGame() == null) {
                sender.sendMessage(CC.translate("&cYou are not in a game."));
                return true;
            }
            if (!user.getGame().started) {
                sender.sendMessage(CC.translate("&cGame not started."));
                return true;
            }
            user.getGame().stop();
        } else if (args[0].equalsIgnoreCase("addplayer") && args.length >= 2) {
            if (!sender.hasPermission("uno.addplayer")) return true;
            Game game = plugin.getSM().getGameManager().getRandomGame();
            if (game == null) {
                sender.sendMessage(CC.translate("&cNo games available."));
                return true;
            }
            if (game.getPlayers().size() >= game.maxPlayers) {
                sender.sendMessage(CC.translate("&cThe game you tried to join is full."));
                return true;
            }
            if (args[1].equalsIgnoreCase("*")) {
                plugin.getSM().getUserManager().getUsers().stream().filter(user -> !user.isInGame()).forEach(game::addPlayer);
                sender.sendMessage(CC.translate("&aPlayers added."));
                return true;
            }
            User user = plugin.getSM().getUserManager().getUser(args[1]);
            if (user.isInGame()) {
                sender.sendMessage(CC.translate("&cThis user is already in-game."));
                return true;
            }
            game.addPlayer(user);
            sender.sendMessage(CC.translate("&aUser added."));
        } else if (args[0].equalsIgnoreCase("removeplayer") && args.length >= 2) {
            if (!sender.hasPermission("uno.removeplayer")) return true;
            if (args[1].equalsIgnoreCase("*")) {
                plugin.getSM().getUserManager().getUsers().stream().filter(User::isInGame).forEach(user -> user.getGame().removePlayer(user));
                sender.sendMessage(CC.translate("&aPlayers removed."));
                return true;
            }
            User user = plugin.getSM().getUserManager().getUser(args[1]);
            if (!user.isInGame()) {
                sender.sendMessage(CC.translate("&cThis user is not in-game."));
                return true;
            }
            user.getGame().removePlayer(user);
            sender.sendMessage(CC.translate("&aUser removed."));
        } else if (args[0].equalsIgnoreCase("auto")) {
            if (!sender.hasPermission("uno.auto")) return true;
            plugin.getSM().getGameManager().auto = !plugin.getSM().getGameManager().auto;
            sender.sendMessage(CC.translate("&6Automatic game creation: " + (plugin.getSM().getGameManager().auto ? "&aON" : "&cOFF")));
        }
        return true;
    }

}
