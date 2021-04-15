package dev.qruet.insomnia.nms.effect;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;

public class MobEffectInsomnia extends net.minecraft.server.v1_16_R3.MobEffectList {

    /**
     * @param mobeffectinfo
     * @param color Hexadecimal color code in decimal form
     */
    public MobEffectInsomnia(MobEffectInfo mobeffectinfo, int color) {
        super(mobeffectinfo, color);
    }

    // this function never seems to get called which is a bit odd
    @Override
    public void tick(EntityLiving entityliving, int i) {
        if (this == MobEffects.INSOMNIA) {
            Bukkit.broadcastMessage("Entity, " + entityliving.getName() + " has insomnia effect!");
        } else {
            super.tick(entityliving, i);
        }

    }

}
