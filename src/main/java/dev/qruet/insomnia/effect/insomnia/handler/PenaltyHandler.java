package dev.qruet.insomnia.effect.insomnia.handler;

import dev.qruet.insomnia.effect.insomnia.InsomniaEffect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PenaltyHandler implements Listener {

    private final InsomniaEffect effect;

    public PenaltyHandler(InsomniaEffect effect) {
        this.effect = effect;
    }

    @EventHandler
    public void onBite(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity attacker = e.getDamager();

        if (!(entity.getUniqueId().equals(effect.getTarget().getUniqueId()))) {
            return;
        }

        if (!(attacker instanceof Phantom)) {
            return;
        }

        effect.penalize();
    }

}
