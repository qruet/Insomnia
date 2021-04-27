package dev.qruet.insomnia.handlers;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.http.WebServerHandler;
import dev.qruet.insomnia.io.texture.ResourcePackIO;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;


public class JoinHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        WebServerHandler handler = JavaPlugin.getPlugin(Insomnia.class).getHTTPServer();
        String address = ResourcePackIO.getResourcePath();
        if (handler != null) {
            address = "http://" + handler.getAddress() + ":" + handler.getPort();
        }

        player.setResourcePack(address);
    }

}
