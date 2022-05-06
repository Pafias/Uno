package me.pafias.uno;

public class ServicesManager {

    private final Uno plugin;

    public ServicesManager(Uno plugin) {
        this.plugin = plugin;
        variables = new Variables(plugin);
        userManager = new UserManager();
        gameManager = new GameManager(plugin);
    }

    private final Variables variables;

    public Variables getVariables(){
        return variables;
    }

    private final UserManager userManager;

    public UserManager getUserManager() {
        return userManager;
    }

    private final GameManager gameManager;

    public GameManager getGameManager() {
        return gameManager;
    }
}
