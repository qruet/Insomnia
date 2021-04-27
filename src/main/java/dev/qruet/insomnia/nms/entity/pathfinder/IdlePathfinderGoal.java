package dev.qruet.insomnia.nms.entity.pathfinder;

import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;

import java.util.Random;

public class IdlePathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;
    private int b;

    public IdlePathfinderGoal(EntityInsomniaPhantom phantom) {
        this.phantom = phantom;
    }

    public boolean a() {
        EntityLiving entityliving = phantom.getGoalTarget();
        return entityliving != null && phantom.a(phantom.getGoalTarget(), PathfinderTargetCondition.a);
    }

    public void c() {
        this.b = 10;
        phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.CIRCLE);
        this.g();
    }

    public void d() {
        phantom.d = phantom.world.getHighestBlockYAt(net.minecraft.server.v1_16_R3.HeightMap.Type.MOTION_BLOCKING, phantom.d).up(10 + phantom.getRandom().nextInt(20));
    }

    public void e() {
        if (phantom.getCurrentPhase() == EntityInsomniaPhantom.AttackPhase.CIRCLE) {
            --this.b;
            if (this.b <= 0) {
                phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.SWOOP);
                this.g();
                this.b = (8 + phantom.getRandom().nextInt(4)) * 20;
                phantom.playSound(SoundEffects.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + phantom.getRandom().nextFloat() * 0.1F);
            }
        }

    }

    private void g() {
        phantom.d = phantom.getGoalTarget().getChunkCoordinates().up(20 + phantom.getRandom().nextInt(20));
        if (phantom.d.getY() < phantom.world.getSeaLevel()) {
            phantom.d = new BlockPosition(phantom.d.getX(), phantom.world.getSeaLevel() + 1, phantom.d.getZ());
        }

    }

}
