package dev.qruet.insomnia.handlers;

import dev.qruet.insomnia.data.BedTracker;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.effect.insomnia.InsomniaEffect;
import dev.qruet.insomnia.server.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class SleepHandler implements Listener {

    private Map<UUID, BedTracker> trackerMap = new HashMap<>(); // testing purposes

    @EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
        Player player = e.getPlayer();
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK)
            return;

        if (player.hasPotionEffect(PotionEffectType.INSOMNIA))
            return;

        BedTracker tracker = trackerMap.getOrDefault(player.getUniqueId(), new BedTracker(player.getUniqueId()));

        boolean sporadic = tracker.record(e.getBed().getLocation());
        if (sporadic) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.INSOMNIA, 999999, 0, true, true, true));
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
            InsomniaEffect effect = Server.getInsomniaEffect(player);

            Random rnd = new Random();

            if (rnd.nextFloat() < (1.0f - effect.penalty())) {
                player.removePotionEffect(PotionEffectType.INSOMNIA);
                return;
            }

            if (rnd.nextFloat() < effect.penalty()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INSOMNIA, 999999, effect.getSeverityLevel() + 1));
            }
            /*Tasky.sync(t -> {
                ((CraftPlayer) player).getHandle().sleepTicks = 0;
            }, 0L, 1L);*/
        }
    }

}
