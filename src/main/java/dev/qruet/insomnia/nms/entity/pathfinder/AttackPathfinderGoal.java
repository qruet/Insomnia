package dev.qruet.insomnia.nms.entity.pathfinder;

import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.List;

public class AttackPathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;

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
        } else if (!entityliving.isAlive()) {
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
}
