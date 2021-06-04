package dev.qruet.insomnia.effect.insomnia;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.effect.insomnia.handler.PenaltyHandler;
import dev.qruet.insomnia.effect.insomnia.handler.SleepHandler;
import dev.qruet.insomnia.effect.insomnia.timer.InsomniaClock;
import dev.qruet.insomnia.effect.render.GhostRenderer;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class InsomniaEffect {

    private final Collection<EntityInsomniaPhantom> phantoms;
    private final Player target;
    private final Random random;

    private int level;
    private Nightmare nE;
    private int penalty_points;

    private InsomniaClock clock;

    private PenaltyHandler pH;
    private SleepHandler sH;

    public InsomniaEffect(Player player, int level) {
        this.target = player;
        this.level = level;
        this.phantoms = new LinkedList<>();
        this.random = new Random();

        this.sH = new SleepHandler(this);
        Bukkit.getPluginManager().registerEvents(sH, JavaPlugin.getPlugin(Insomnia.class));

        this.pH = new PenaltyHandler(this);
        Bukkit.getPluginManager().registerEvents(pH, JavaPlugin.getPlugin(Insomnia.class));

        this.clock = new InsomniaClock(this, sH);
        this.clock.runTaskTimer(JavaPlugin.getPlugin(Insomnia.class), 0L, 1L);
    }

    /**
     * Penalty is a randomly chosen value between 0 and 1 inclusive
     * Penalty points help weigh the chosen value closer to 1
     *
     * @return
     */
    public float penalty() {
        return (random.nextFloat() * 0.35f) + ((float) (Math.pow(penalty_points, 0.75)) / 10f);
    }

    /**
     * Penalize increments the penalty points by 1
     * Penalty points are used in determining the chances of
     * receiving Insomnia again the following night when the player sleeps
     */
    public void penalize() {
        penalty_points++;
    }

    public Player getTarget() {
        return target;
    }

    public void resetPoints() {
        penalty_points = 0;
    }

    public void updateSeverity(int level) {
        this.level = level;
    }

    public int getSeverityLevel() {
        return level;
    }

    public Collection<EntityInsomniaPhantom> phantoms() {
        return phantoms;
    }

    public Nightmare beginNightmare() {
        if (nE != null)
            return nE;
        nE = new Nightmare();
        return nE;
    }

    public void endNightmare() {
        if (nE == null)
            return;
        nE.end();
        nE = null;
    }

    public void end() {
        endNightmare();
        HandlerList.unregisterAll(pH);
        HandlerList.unregisterAll(sH);
    }

    public class Nightmare {
        private Nightmare() {

        }

        private void begin(GhostRenderer renderer) {
            EntityPlayer player = ((CraftPlayer) target).getHandle();
            Location pLoc = target.getLocation();

            Random rnd = new Random();

            int count;
            if (level <= 1) {
                count = 4;
            } else if (level == 2 || level == 3) {
                count = 7;
            } else {
                count = 5;
            }

            int x = (int) pLoc.getX() + (rnd.nextBoolean() ? -1 : 1) * rnd.nextInt(35);
            int y = (int) pLoc.getY() + rnd.nextInt(15) + 25;
            int z = (int) pLoc.getZ() + (rnd.nextBoolean() ? -1 : 1) * rnd.nextInt(35);

            for (int i = 0; i < count; i++) {
                int dx = (rnd.nextBoolean() ? -1 : 1) * rnd.nextInt(6);
                int dy = (rnd.nextBoolean() ? -1 : 1) * rnd.nextInt(4);
                int dz = (rnd.nextBoolean() ? -1 : 1) * rnd.nextInt(6);

                EntityInsomniaPhantom phantom = new EntityInsomniaPhantom(EntityInsomniaPhantom.AttackPhase.CIRCLE, player.world, target, level);
                phantom.setPosition(x + dx, y + dy, z + dz);

                Location location = new Location(pLoc.getWorld(), x + dx, y + dy, z + dz);
                target.playSound(location, Sound.ENTITY_PHANTOM_AMBIENT, 10f, new Random().nextFloat() * 0.35f);
                target.spawnParticle(Particle.SMOKE_LARGE, location, 20, 0, 0, 0, 0.2);

                player.world.addEntity(phantom, CreatureSpawnEvent.SpawnReason.CUSTOM);
                phantoms.add(phantom);

                renderer.setGhost((LivingEntity) phantom.getBukkitEntity(), true);
                renderer.setGhost(target, false);
            }
        }

        private void end() {
            for (EntityInsomniaPhantom phantom : phantoms) {
                phantom.disappear();
            }
        }

        public void destroy(boolean removeEntities) {
            if (removeEntities) {
                for (EntityInsomniaPhantom phantom : phantoms) {
                    phantom.die();
                }
            }
            phantoms.clear();
        }
    }


}
