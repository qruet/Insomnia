package dev.qruet.insomnia.nms.entity.pathfinder;

import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;

import java.util.Random;

public class CirclePathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;

    private float c;
    private float d;
    private float e;
    private float f;

    public CirclePathfinderGoal(EntityInsomniaPhantom phantom) {
        super();
        this.phantom = phantom;
    }

    /**
     * Called to determine whether pathfindergoal should be ticked
     *
     * @return
     */
    public boolean a() {
        return  phantom.getGoalTarget() == null || phantom.getCurrentPhase() == EntityInsomniaPhantom.AttackPhase.CIRCLE;
    }

    public void c() {
        this.d = 80.0F + phantom.getRandom().nextFloat() * 15.0F;
        this.e = -4.0F + phantom.getRandom().nextFloat() * 9.0F;
        this.f = phantom.getRandom().nextBoolean() ? 1.0F : -1.0F;
        this.h();
    }

    public void e() {
        if (phantom.getRandom().nextInt(250) == 0) {
            this.e = -4.0F + phantom.getRandom().nextFloat() * 9.0F;
        }

        if (phantom.getRandom().nextInt(250) == 0) {
            ++this.d;
            if (this.d > 35.0F) {
                this.d = 5.0F;
                this.f = -this.f;
            }
        }

        if (phantom.getRandom().nextInt(450) == 0) {
            this.c = phantom.getRandom().nextFloat() * 2.0F * 3.1415927F;
            this.h();
        }

        if (phantom.c.c(phantom.locX(), phantom.locY(), phantom.locZ()) < 4.0D) {
            this.h();
        }

        if (phantom.c.y < phantom.locY() && !phantom.world.isEmpty(phantom.getChunkCoordinates().down(1))) {
            this.e = Math.max(1.0F, this.e);
            this.h();
        }

        if (phantom.c.y > phantom.locY() && !phantom.world.isEmpty(phantom.getChunkCoordinates().up(1))) {
            this.e = Math.min(-1.0F, this.e);
            this.h();
        }

        if(phantom.locY() < 100) {
            this.e += (float) (100.0f - phantom.locY()) * 0.017453292F;
        }

    }

    private void h() {
        if (BlockPosition.ZERO.equals(phantom.d)) {
            phantom.d = phantom.getChunkCoordinates();
        }

        this.c += this.f * 45.0F * 0.017453292F;
        phantom.c = Vec3D.b(phantom.d).add((double) (this.d * MathHelper.cos(this.c)), (double) (-4.0F + this.e), (double) (this.d * MathHelper.sin(this.c)));
    }

}
