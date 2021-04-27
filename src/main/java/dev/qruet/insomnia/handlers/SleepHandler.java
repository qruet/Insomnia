package dev.qruet.insomnia.handlers;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.data.BedTracker;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.misc.Tasky;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;
import net.minecraft.server.v1_16_R3.WorldData;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Bed;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SleepHandler implements Listener {

    private Map<UUID, BedTracker> trackerMap = new HashMap<>(); // testing purposes

    //@EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
        Player player = e.getPlayer();
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        BedTracker tracker = trackerMap.getOrDefault(player.getUniqueId(), null);
        if (tracker == null) {
            tracker = new BedTracker(player.getUniqueId());
        }

        boolean sporadic = tracker.record(e.getBed().getLocation());
        if (sporadic) {
            Bukkit.broadcastMessage("Sporadic!");
            player.addPotionEffect(new PotionEffect(PotionEffectType.INSOMNIA, 20 * 180, 0, true, true, true));
            tracker.resetTracker();
        }

        trackerMap.put(player.getUniqueId(), tracker);
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        Player player = e.getPlayer();
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        if (player.hasPotionEffect(PotionEffectType.INSOMNIA)) {
            Tasky.sync(t -> {
                ((CraftPlayer) player).getHandle().sleepTicks = 0;
            }, 0L, 1L);
        }
    }

}
