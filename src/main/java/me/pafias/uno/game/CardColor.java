package me.pafias.uno.game;

import me.pafias.uno.utils.CC;
import org.bukkit.ChatColor;

import java.util.Random;

public enum CardColor {

    RED("&cred", ChatColor.RED), BLUE("&9blue", ChatColor.BLUE), YELLOW("&eyellow", ChatColor.YELLOW), GREEN("&agreen", ChatColor.GREEN);

    CardColor(String name, ChatColor chatcolor) {
        this.name = name;
        this.chatcolor = chatcolor;
    }

    public static CardColor random() {
        int pick = new Random().nextInt(values().length);
        return values()[pick];
    }

    private String name;
    private ChatColor chatcolor;

    public String getName() {
        return CC.translate(name);
    }

    public ChatColor getChatColor() {
        return chatcolor;
    }

}
