package dev.qruet.insomnia.nms.effect;

import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import dev.qruet.insomnia.server.Server;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.MobEffects;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class MobEffectInsomnia extends net.minecraft.server.v1_16_R3.MobEffectList {

    /**
     * @param mobeffectinfo
     * @param color         Hexadecimal color code in decimal form
     */
    public MobEffectInsomnia(MobEffectInfo mobeffectinfo, int color) {
        super(mobeffectinfo, color);
    }

    // This function gets called when effect is removed
    @Override
    public void a(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        super.a(entityliving, attributemapbase, i);
    }

    // This function gets called when effect is added
    @Override
    public void b(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        super.b(entityliving, attributemapbase, i);
        if (entityliving == null)
            return;

        LivingEntity entity = (LivingEntity) entityliving.getBukkitEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Server.getWorld(player.getWorld().getUID()).spawnInsomniaPhantom(player, i);
        }
    }

}
