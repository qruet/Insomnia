package dev.qruet.insomnia.effect;

import dev.qruet.insomnia.misc.ReflectionUtils;
import dev.qruet.insomnia.nms.effect.MobEffects;
import net.minecraft.server.v1_16_R3.MobEffectList;
import org.bukkit.Color;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * Custom PotionEffectType class - also responsible for preparing the original PotionEffectType class with the registration of the Insomnia Mob Effect.
 */
public abstract class PotionEffectType extends org.bukkit.potion.PotionEffectType {
    public static final org.bukkit.potion.PotionEffectType INSOMNIA = new InsomniaPotionEffectTypeWrapper(33);

    protected PotionEffectType(int id) {
        super(id);
    }

    public static void setup(JavaPlugin plugin) {
        try {
            Field byIdField = ReflectionUtils.getField(org.bukkit.potion.PotionEffectType.class, "byId");
            org.bukkit.potion.PotionEffectType[] byId = (org.bukkit.potion.PotionEffectType[]) byIdField.get(null);
            if (byId.length >= 34 && byId[33].getName().equals("insomnia")) {
                plugin.getLogger().info("Already registered..");
                byId[33] = null; // reset
            } else {
                byIdField.set(null,  Arrays.copyOf(byId, 34));
            }
            ReflectionUtils.getField(org.bukkit.potion.PotionEffectType.class, "acceptingNew").set(null, true);
            plugin.getLogger().info("Successfully registered custom effect, " + MobEffects.INSOMNIA.c() + ".");
            ReflectionUtils.getField(org.bukkit.potion.PotionEffectType.class, "acceptingNew").set(null, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class InsomniaPotionEffectTypeWrapper extends PotionEffectType {
        protected InsomniaPotionEffectTypeWrapper(int id) {
            super(id);
        }

        public double getDurationModifier() {
            return this.getType().getDurationModifier();
        }

        @NotNull
        public String getName() {
            return this.getType().getName();
        }

        @NotNull
        public org.bukkit.potion.PotionEffectType getType() {
            return PotionEffectType.getById(getId());
        }

        public boolean isInstant() {
            return this.getType().isInstant();
        }

        @NotNull
        public Color getColor() {
            return this.getType().getColor();
        }
    }
}
