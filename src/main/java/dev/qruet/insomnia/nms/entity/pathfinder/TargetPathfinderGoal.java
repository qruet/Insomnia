package dev.qruet.insomnia.nms.entity.pathfinder;

import com.google.common.collect.Lists;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.util.BoundingBox;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class TargetPathfinderGoal extends PathfinderGoal {

    private final EntityInsomniaPhantom phantom;

    private final PathfinderTargetCondition b;
    private int c;

    public TargetPathfinderGoal(EntityInsomniaPhantom phantom) {
        this.phantom = phantom;

        this.b = (new PathfinderTargetCondition()).a(128.0D);
        this.c = phantom.getRandom().nextInt(450) + 150;
    }

    public boolean a() {
        if (this.c > 0) {
            --this.c;
            return false;
        } else {
            this.c = phantom.getRandom().nextInt(220) + 80;

            if (phantom.isGhost()) {
                EntityPlayer player = ((CraftPlayer) phantom.getTarget()).getHandle();
                if (phantom.a(player, PathfinderTargetCondition.a)) {
                    phantom.setGoalTarget(player, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
                    phantom.setCurrentPhase(EntityInsomniaPhantom.AttackPhase.SWOOP);
                    return true;
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
