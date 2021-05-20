package dev.qruet.insomnia.nms.entity.pathfinder;

import dev.qruet.insomnia.effect.block.LightExtinguish;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GustPathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;

    private static final List<Material> EXTINGUISHABLE_BLOCKS = Arrays.asList(Material.TORCH, Material.WALL_TORCH);

    private int c = 0;

    public GustPathfinderGoal(EntityInsomniaPhantom phantom) {
        super();
        this.phantom = phantom;
    }

    /**
     * Called to determine whether PathfinderGoal should be ticked
     *
     * @return
     */
    public boolean a() {
        return phantom.getCurrentPhase() == EntityInsomniaPhantom.AttackPhase.GUST;
    }

    /**
     * Called when {@link #a()} returns true for the first time
     */
    public void c() {
        this.c = 60;

        Location location = phantom.getBukkitEntity().getLocation();
        Vector dir = location.getDirection().normalize();

        Location origin = location.clone().add(dir.getX() * 3, -2, dir.getZ() * 3);
        phantom.world.getWorld().spawnParticle(Particle.CLOUD, location.clone().add(dir.getX() * 1.5, -2, dir.getZ() * 1.5), 35, 1, 0, 1, 0.15);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    org.bukkit.block.Block block = origin.clone().add(dx, dy, dz).getBlock();
                    if (block.getLocation().distance(origin) <= 2) {
                        if (EXTINGUISHABLE_BLOCKS.contains(block.getType())) {
                            LightExtinguish.playEffect(block);
                        }
                    }
                }
            }
        }
    }

    public void e() {
        if (c > 0) {
            c--;
            phantom.c = Vec3D.b(phantom.d).add(phantom.c.x * 0.1, 100D, phantom.c.z * 0.1);
        } else {
            phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.CIRCLE);
        }
    }

}
