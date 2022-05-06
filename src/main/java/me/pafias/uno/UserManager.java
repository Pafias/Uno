package me.pafias.uno;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final Set<User> users = new HashSet<>();

    public Set<User> getUsers() {
        return users;
    }

    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public User getUser(String name) {
        return users.stream().filter(user -> user.getName().startsWith(name)).findFirst().orElse(null);
    }

    public User getUser(UUID uuid) {
        return users.stream().filter(user -> user.getUUID().equals(uuid)).findAny().orElse(null);
    }

    public void addUser(Player player) {
        users.add(new User(player));
    }

    public void removeUser(Player player) {
        User user = getUser(player);
        if(user.isInGame())
            user.getGame().removePlayer(user);
        users.remove(user);
    }

}
