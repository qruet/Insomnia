package dev.qruet.insomnia.nms.effect;

import dev.qruet.insomnia.server.Server;
import dev.qruet.insomnia.server.world.World;
import net.minecraft.server.v1_16_R3.AttributeMapBase;
import net.minecraft.server.v1_16_R3.EntityLiving;
import net.minecraft.server.v1_16_R3.MobEffectInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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

        LivingEntity entity = (LivingEntity) entityliving.getBukkitEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            World world = Server.getWorld(player.getWorld().getUID());
            world.removeInsomniaEffect(player);
        }
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
            Server.getWorld(player.getWorld().getUID()).registerNewInsomniaEffect(player, i);
        }
    }

}
