package dev.qruet.insomnia.nms.entity.pathfinder;

import dev.qruet.insomnia.effect.block.LightExtinguish;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.*;
import java.util.stream.Collectors;

public class AttackPathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;
    private int tB;
    private int pL;

    public AttackPathfinderGoal(EntityInsomniaPhantom phantom) {
        super();
        this.phantom = phantom;
    }

    public boolean a() {
        return phantom.getGoalTarget() != null && phantom.getCurrentPhase() == EntityInsomniaPhantom.AttackPhase.SWOOP;
    }

    public boolean b() {
        EntityLiving entityliving = phantom.getGoalTarget();
        if (entityliving == null) {
            return false;
        } else if (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).isCreative())) {
            return false;
        } else if (!this.a()) {
            return false;
        } else {
            if (phantom.ticksLived % 20 == 0) {
                List<EntityCat> list = phantom.world.a(EntityCat.class, phantom.getBoundingBox().g(16.0D), IEntitySelector.a);
                if (!list.isEmpty()) {
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityCat entitycat = (EntityCat) iterator.next();
                        entitycat.eZ();
                    }

                    return false;
                }
            }

            return true;
        }
    }

    public void c() {
        tB = phantom.ticksLived;
        pL = phantom.getRandom().nextInt(300) + 200;
    }

    public void d() {
        //phantom.disappear();
    }

    public void e() {
        if (phantom.ticksLived - tB > pL) {
            phantom.setGoalTarget(null, EntityTargetEvent.TargetReason.FORGOT_TARGET, true);
            phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.CIRCLE);
            return;
        }

        EntityLiving entityliving = phantom.getGoalTarget();
        phantom.c = new Vec3D(entityliving.locX(), entityliving.e(0.5D), entityliving.locZ());
        if (phantom.getBoundingBox().g(0.20000000298023224D).c(entityliving.getBoundingBox())) {
            phantom.world.getWorld().playSound(phantom.getBukkitEntity().getLocation(), Sound.ENTITY_PHANTOM_BITE, 1.0f, 0.8f);
            phantom.attackEntity(entityliving);
            phantom.disappear();
        } else if (phantom.positionChanged || phantom.hurtTicks > 0) {
            //phantom.disappear();
        }
    }
}
