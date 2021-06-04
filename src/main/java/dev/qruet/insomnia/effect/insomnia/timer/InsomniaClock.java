package dev.qruet.insomnia.effect.insomnia.timer;

import dev.qruet.insomnia.effect.insomnia.InsomniaEffect;
import dev.qruet.insomnia.effect.insomnia.handler.SleepHandler;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class InsomniaClock extends BukkitRunnable {

    private final InsomniaEffect effect;
    private final SleepHandler handler;
    private final World world;

    public InsomniaClock(InsomniaEffect effect, SleepHandler handler) {
        this.effect = effect;
        this.handler = handler;
        this.world = effect.getTarget().getWorld();
    }

    @Override
    public void run() {

    }
}
