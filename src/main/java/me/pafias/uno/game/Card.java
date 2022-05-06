package me.pafias.uno.game;

import me.pafias.uno.Uno;
import me.pafias.uno.utils.MapBuilder;
import me.pafias.uno.utils.RandomUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Card {

    private CardType type;
    private CardColor color;
    private Integer number;

    private ItemStack mapItem;
    private ItemStack invItem;
    private BufferedImage image;

    public Card(@Nullable CardColor color, CardType type, @Nullable Integer number) {
        this.type = type;
        this.color = color;
        this.number = number;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        if (type.equals(CardType.NUMBER))
            return color.getName() + " " + number;
        else if (type.equals(CardType.DRAW2))
            return color.getName() + " draw +2";
        else if (type.equals(CardType.SKIP))
            return color.getName() + " skip card";
        else if (type.equals(CardType.REVERSE))
            return color.getName() + " reverse card";
        else if (type.equals(CardType.WILD))
            return "wild card";
        else if (type.equals(CardType.WILDDRAW4))
            return "wild draw +4";
        else return "unknown card";
    }

    public ItemStack getInventoryItem(boolean useCached) {
        if (invItem == null || !useCached) {
            ItemStack is = new ItemStack(Material.WHITE_WOOL);
            ItemMeta meta = is.getItemMeta();
            switch (type) {
                case DRAW2:
                case REVERSE:
                case SKIP:
                    switch (color) {
                        case RED:
                        case YELLOW:
                            is.setType(Material.valueOf(color.name() + "_WOOL"));
                            break;
                        case BLUE:
                            is.setType(Material.LIGHT_BLUE_WOOL);
                            break;
                        case GREEN:
                            is.setType(Material.LIME_WOOL);
                            break;
                    }
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    break;
                case WILD:
                case WILDDRAW4:
                    is.setType(Material.BLACK_WOOL);
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                    break;
                case NUMBER:
                    switch (color) {
                        case RED:
                        case YELLOW:
                            is.setType(Material.valueOf(color.name() + "_WOOL"));
                            break;
                        case BLUE:
                            is.setType(Material.LIGHT_BLUE_WOOL);
                            break;
                        case GREEN:
                            is.setType(Material.LIME_WOOL);
                            break;
                    }
                    is.setAmount(number == 0 ? 1 : number);
                    break;
            }
            meta.setDisplayName((color != null ? color.getChatColor() : "") + RandomUtils.capitalize(getName()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            is.setItemMeta(meta);
            invItem = is;
        }
        return invItem;
    }

    public ItemStack getMapItem(boolean useCached) {
        if (mapItem == null || !useCached) {
            MapBuilder builder = new MapBuilder();
            builder.setRenderOnce(true);
            builder.setImage(getImage(useCached));
            builder.setDisplayName(getName());
            builder.setItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            mapItem = builder.build();
        }
        return mapItem;
    }

    public BufferedImage getImage(boolean useCached) {
        if (image == null || !useCached) {
            try {
                image = RandomUtils.resize(ImageIO.read(Uno.get().getResource(getImagePath())), 128, 128);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return image;
    }

    public String getImagePath() {
        String path = "cards/%s.png";
        switch (type) {
            case DRAW2:
                path = String.format(path, RandomUtils.capitalize(color.name()) + "_Draw");
                break;
            case NUMBER:
                path = String.format(path, RandomUtils.capitalize(color.name()) + "_" + number);
                break;
            case REVERSE:
                path = String.format(path, RandomUtils.capitalize(color.name()) + "_Reverse");
                break;
            case SKIP:
                path = String.format(path, RandomUtils.capitalize(color.name()) + "_Skip");
                break;
            case WILD:
                path = String.format(path, "Wild");
                break;
            case WILDDRAW4:
                path = String.format(path, "Wild_Draw");
        }
        return path;
    }

}
