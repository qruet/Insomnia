package dev.qruet.insomnia.nms.effect;

import dev.qruet.insomnia.Insomnia;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.v1_16_R3.potion.CraftPotionUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Iterator;

public class MobEffects extends net.minecraft.server.v1_16_R3.MobEffects {

    public static final net.minecraft.server.v1_16_R3.MobEffectList INSOMNIA;

    private static net.minecraft.server.v1_16_R3.MobEffectList a(int i, String s, net.minecraft.server.v1_16_R3.MobEffectList mobeffectlist) {
        MobEffectList effect = IRegistry.a(IRegistry.MOB_EFFECT, i, s, mobeffectlist);
        if(effect != null) {
            Insomnia.logger().info("Registering id: " + i);
            PotionEffectType.registerPotionEffectType(new CraftPotionEffectType(effect));
        }
        return effect;
    }

    static {
        INSOMNIA = a(dev.qruet.insomnia.effect.PotionEffectType.INSOMNIA.getId(), "insomnia", new MobEffectInsomnia(MobEffectInfo.HARMFUL, 1245995)); // 33
    }



}
