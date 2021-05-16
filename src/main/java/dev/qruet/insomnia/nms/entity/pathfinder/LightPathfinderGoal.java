package dev.qruet.insomnia.nms.entity.pathfinder;

import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.MathHelper;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Arrays;
import java.util.List;

public class LightPathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;

    private static final List<Material> LIGHT_EMITTING_BLOCKS = Arrays.asList(Material.TORCH, Material.WALL_TORCH);

    private boolean lE;

    public LightPathfinderGoal(EntityInsomniaPhantom phantom) {
        super();
        this.phantom = phantom;
    }

    /**
     * Called to determine whether pathfindergoal should be ticked
     *
     * @return
     */
    public boolean a() {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    org.bukkit.block.Block block = phantom.getBukkitEntity().getLocation().add(dx, dy, dz).getBlock();
                    if (LIGHT_EMITTING_BLOCKS.contains(block.getType())) {
                        return true;
                    }
                }
            }
        }
        lE = false;
        return false;
    }

    public void e() {
        phantom.c = Vec3D.b(phantom.d).add(phantom.c.x * 0.25, 1000D, phantom.c.z * 0.25);
        if (!lE) {
            phantom.world.getWorld().playSound(phantom.getBukkitEntity().getLocation(), Sound.ENTITY_VEX_DEATH, 1.5f, 0.0f);
            phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.CIRCLE);
            lE = true;
        }
    }

}
