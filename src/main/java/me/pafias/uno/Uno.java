package me.pafias.uno;

import me.pafias.uno.commands.UnoCommand;
import me.pafias.uno.game.Game;
import me.pafias.uno.listeners.GameListener;
import me.pafias.uno.listeners.JoinQuitListener;
import me.pafias.uno.listeners.game.DrawListener;
import me.pafias.uno.listeners.game.InventoryListener;
import me.pafias.uno.listeners.game.UnoChatListener;
import me.pafias.uno.listeners.game.WildCardListener;
import me.pafias.uno.utils.CC;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Uno extends JavaPlugin {

    private static Uno plugin;

    public static Uno get() {
        return plugin;
    }

    private ServicesManager servicesManager;

    public ServicesManager getSM() {
        return servicesManager;
    }

    @Override
    public void onEnable() {
        plugin = this;

        if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            getServer().getConsoleSender().sendMessage(CC.translate("&cProtocolLib not detected. UNO disabled."));
            getServer().getPluginManager().disablePlugin(plugin);
        }

        servicesManager = new ServicesManager(plugin);
        register();
        getServer().getOnlinePlayers().forEach(p -> servicesManager.getUserManager().addUser(p));
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();
        getCommand("uno").setExecutor(new UnoCommand(plugin));
        pm.registerEvents(new JoinQuitListener(plugin), plugin);
        pm.registerEvents(new GameListener(plugin), plugin);
        pm.registerEvents(new InventoryListener(plugin), plugin);
        pm.registerEvents(new WildCardListener(plugin), plugin);
        pm.registerEvents(new UnoChatListener(plugin), plugin);
        pm.registerEvents(new DrawListener(plugin), plugin);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(plugin);
        servicesManager.getGameManager().getGames().forEach(Game::stop);
        plugin = null;
    }
}
