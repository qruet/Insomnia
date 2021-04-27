package dev.qruet.insomnia.nms.entity.pathfinder;

import com.google.common.collect.Lists;
import dev.qruet.insomnia.effect.PotionEffectType;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class TargetPathfinderGoal extends PathfinderGoal {

    private final EntityPhantom phantom;

    private final PathfinderTargetCondition b;
    private int c;

    public TargetPathfinderGoal(EntityPhantom phantom) {
        this.phantom = phantom;

        this.b = (new PathfinderTargetCondition()).a(64.0D);
        this.c = 20;
    }

    public boolean a() {
        if (this.c > 0) {
            --this.c;
            return false;
        } else {
            this.c = 60;

            Collection<org.bukkit.entity.Entity> entities = phantom.world.getWorld().getNearbyEntities(
                    phantom.getBukkitEntity().getBoundingBox().expand(16.0D, 64.0D, 16.0D), entity -> {
                        if (entity instanceof LivingEntity) {
                            LivingEntity living = (LivingEntity) entity;
                            return living.hasPotionEffect(PotionEffectType.INSOMNIA);
                        }
                        return false;
                    });

            List<org.bukkit.entity.Entity> list = Lists.newLinkedList();
            list.addAll(entities);

            if (!list.isEmpty()) {
                list.sort(Comparator.comparing(e -> ((org.bukkit.entity.Entity) e).getLocation().getY()).reversed());
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityLiving entity = ((CraftLivingEntity) (LivingEntity) iterator.next()).getHandle();
                    if (phantom.a(entity, PathfinderTargetCondition.a)) {
                        // debug
                        Bukkit.broadcastMessage("Attacking " + entity.getEntityType() + " @ " + entity.locX() + ", " + entity.locY() + ", " + entity.locZ());
                        phantom.setGoalTarget(entity, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean b() {
        EntityLiving entityliving = phantom.getGoalTarget();
        return entityliving != null && phantom.a(entityliving, PathfinderTargetCondition.a);
    }

}
