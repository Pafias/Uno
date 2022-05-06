package me.pafias.uno.utils;

import me.pafias.uno.Uno;
import org.bukkit.Location;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RandomUtils {

    private static final Uno plugin = Uno.get();

    public static String capitalize(String string) {
        return string.toUpperCase().substring(0, 1) + string.toLowerCase().substring(1, string.length());
    }

    public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
        Image tmp = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static Location parseLocation(String config) {
        Location location;
        String world = config.split(",")[0];
        double x = Double.parseDouble(config.split(",")[1]);
        double y = Double.parseDouble(config.split(",")[2]);
        double z = Double.parseDouble(config.split(",")[3]);
        if (config.split(",").length == 6) {
            float yaw = (float) Double.parseDouble(config.split(",")[4]);
            float pitch = (float) Double.parseDouble(config.split(",")[5]);
            location = new Location(plugin.getServer().getWorld(world), x, y, z, yaw, pitch);
        } else {
            location = new Location(plugin.getServer().getWorld(world), x, y, z);
        }
        return location;
    }

    public static String parseLocation(Location location) {
        return location.getWorld().getName() +
                "," +
                location.getX() +
                "," +
                location.getY() +
                "," +
                location.getZ() +
                "," +
                location.getYaw() +
                "," +
                location.getPitch();
    }

}
