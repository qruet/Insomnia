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
        if (phantom.getCurrentPhase() == EntityInsomniaPhantom.AttackPhase.GUST)
            return false;

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
        return false;
    }

    public void c() {
        phantom.world.getWorld().playSound(phantom.getBukkitEntity().getLocation(), Sound.ENTITY_VEX_DEATH, 1.5f, 0.0f);
        phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.GUST);
    }

}
