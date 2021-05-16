package dev.qruet.insomnia.nms.entity;

import dev.qruet.insomnia.nms.entity.controller.PhantomControllerLook;
import dev.qruet.insomnia.nms.entity.controller.PhantomControllerMove;
import dev.qruet.insomnia.nms.entity.pathfinder.*;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class EntityInsomniaPhantom extends net.minecraft.server.v1_16_R3.EntityPhantom {

    public Vec3D c;
    public BlockPosition d;
    protected AttackPhase phase;

    protected Player target;

    public EntityInsomniaPhantom(AttackPhase phase, World world, Player target) {
        super(EntityTypes.PHANTOM, world);
        this.phase = phase;
        this.d = BlockPosition.ZERO;
        this.c = Vec3D.ORIGIN;

        this.moveController = new PhantomControllerMove(this);
        this.lookController = new PhantomControllerLook(this);

        this.goalSelector.getTasks().clear();
        this.targetSelector.getTasks().clear();

        //this.goalSelector.a(1, new IdlePathfinderGoal(this));
        this.goalSelector.a(1, new LightPathfinderGoal(this));
        this.goalSelector.a(2, new GustPathfinderGoal(this));
        this.goalSelector.a(2, new AttackPathfinderGoal(this));
        this.goalSelector.a(3, new CirclePathfinderGoal(this));
        this.targetSelector.a(1, new TargetPathfinderGoal(this));

        this.target = target;
        this.noclip = true;//target != null;

        this.setPersistent();
    }

    public EntityInsomniaPhantom(EntityTypes<Entity> entityTypes, World world) {
        this(AttackPhase.CIRCLE, world, null);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            float f = MathHelper.cos((float) (this.getId() * 3 + this.ticksLived) * 0.13F + 3.1415927F);
            float f1 = MathHelper.cos((float) (this.getId() * 3 + this.ticksLived + 1) * 0.13F + 3.1415927F);
            if (f > 0.0F && f1 <= 0.0F) {
                this.world.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }
        }
    }

    @Override
    public void movementTick() {
        super.movementTick();
        this.setFireTicks(0); // disable fire
    }

    public void disappear() {
        if (target != null)
            target.playSound(origin, Sound.ENTITY_PHANTOM_DEATH, 0f, 1f);
        else
            world.getWorld().playSound(origin, Sound.ENTITY_PHANTOM_DEATH, 0f, 1f);
        world.getWorld().spawnParticle(org.bukkit.Particle.CLOUD, getBukkitEntity().getLocation(), 20, 0, 0, 0, 0.2);
        die();
    }

    public void setCurrentPhase(AttackPhase phase) {
        this.phase = phase;
    }

    public AttackPhase getCurrentPhase() {
        return phase;
    }

    public static enum AttackPhase {
        CIRCLE,
        CHASE,
        SWOOP;

        private AttackPhase() {
        }
    }

}
