package dev.qruet.insomnia.nms.entity;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class EntityInsomniaPhantom extends net.minecraft.server.v1_16_R3.EntityPhantom {

    public EntityInsomniaPhantom(EntityTypes<Entity> entityTypes, World world) {
        super(EntityTypes.PHANTOM, world);
        Bukkit.broadcastMessage("Custom");
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
        this.setFireTicks(0); // disable fire\
    }

}
