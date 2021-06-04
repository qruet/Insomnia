package dev.qruet.insomnia.effect.insomnia.handler;

import dev.qruet.insomnia.effect.insomnia.InsomniaEffect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

public class SleepHandler implements Listener {

    private final InsomniaEffect effect;
    private int sleepDelay;

    public SleepHandler(InsomniaEffect effect) {
        this.effect = effect;
    }

    public void updateSleepDelay(int ticks) {
        this.sleepDelay = ticks;
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent e) {
        
    }

}
