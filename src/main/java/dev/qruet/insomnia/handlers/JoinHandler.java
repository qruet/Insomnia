package dev.qruet.insomnia.handlers;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.http.WebServerHandler;
import dev.qruet.insomnia.io.texture.ResourcePackIO;
import dev.qruet.insomnia.misc.Tasky;
import dev.qruet.insomnia.nms.effect.MobEffects;
import net.minecraft.server.v1_16_R3.MobEffect;
import net.minecraft.server.v1_16_R3.MobEffectList;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import static org.bukkit.event.entity.EntityPotionEffectEvent.Cause.PLUGIN;

public class JoinHandler implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        WebServerHandler handler = JavaPlugin.getPlugin(Insomnia.class).getHTTPServer();

        String address = "http://" + handler.getAddress() + ":" + handler.getPort();
        player.sendMessage("Sending your address: " + address);
        player.setResourcePack(address);
        player.sendMessage("Sending you resource pack.");
        Tasky.sync(t -> {
            org.bukkit.potion.PotionEffectType type = org.bukkit.potion.PotionEffectType.getById(MobEffectList.getId(MobEffects.INSOMNIA));
            Bukkit.broadcastMessage("Type: " + (type == null ? "NULL" : "NOT NULL"));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INSOMNIA, 999,1,true,true,true));
            player.sendMessage("Given you Insomnia.");
            Bukkit.broadcastMessage("has Insomnia: " + player.hasPotionEffect(PotionEffectType.INSOMNIA));
        }, 20L);
    }

}
