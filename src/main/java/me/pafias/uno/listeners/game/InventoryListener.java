package me.pafias.uno.listeners.game;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.pafias.uno.Uno;
import me.pafias.uno.User;
import net.minecraft.server.v1_16_R3.PacketPlayInSteerVehicle;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryListener implements Listener {

    private final Uno uno;

    public InventoryListener(Uno plugin) {
        this.uno = plugin;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(uno, PacketType.Play.Client.STEER_VEHICLE) {
            public void onPacketReceiving(PacketEvent event) {
                try {
                    User user = uno.getSM().getUserManager().getUser(event.getPlayer());
                    if (!user.isInGame()) return;
                    Entity entity = user.getPlayer().getVehicle();
                    if (entity == null) return;
                    PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) event.getPacket().getHandle();
                    event.setCancelled(true);
                    if (packet.e())
                        if (user.getCardsInventory() == null || (user.getCardsInventory() != null && !user.getCardsInventory().getViewers().contains(user.getPlayer())))
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    user.showHandIngame();
                                }
                            }.runTask(uno);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        User user = uno.getSM().getUserManager().getUser((Player) event.getPlayer());
        if(user == null) return;
        if (!user.isInGame()) return;
        if (!event.getInventory().equals(user.getCardsInventory())) return;
        user.hideHandIngame();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        User user = uno.getSM().getUserManager().getUser((Player) event.getWhoClicked());
        if (!user.isInGame()) return;
        if (!event.getClickedInventory().getHolder().equals(user.getPlayer())) return;
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().equals(Material.AIR)) return;
        if (user.attemptPlay(clicked)) {
            user.getPlayer().closeInventory();
            user.hideHandIngame();
        } else {
            user.getPlayer().playSound(user.getPlayer().getEyeLocation(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);
        }
    }

}
