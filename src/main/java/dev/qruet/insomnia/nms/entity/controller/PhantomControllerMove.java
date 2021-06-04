package dev.qruet.insomnia.nms.entity.controller;

import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Particle;

public class PhantomControllerMove extends ControllerMove {

    private final EntityInsomniaPhantom phantom;
    private float j = 0.1F;

    public PhantomControllerMove(EntityInsomniaPhantom phantom) {
        super(phantom);
        this.phantom = phantom;
    }

    public void a() {
        if (phantom.positionChanged) {
            EntityPhantom var10000 = phantom;
            var10000.yaw += 90.0F;
            this.j = 0.1F;
        }

        // the displacement will determine the speed that the phantom will travel
        float f = (float) (phantom.c.x - phantom.locX()); // dx
        float f1 = (float) (phantom.c.y - phantom.locY()); // dy
        float f2 = (float) (phantom.c.z - phantom.locZ()); // dz

        double d0 = (double) MathHelper.c(f * f + f2 * f2);
        double d1 = 1.0D - (double) MathHelper.e(f1 * 0.7F) / d0;

        f = (float) ((double) f * d1);
        f2 = (float) ((double) f2 * d1);
        d0 = (double) MathHelper.c(f * f + f2 * f2); // distance -> sqrt(f^2 + f2^2) or sqrt(x^2 + z^2)

        double d2 = (double) MathHelper.c(f * f + f2 * f2 + f1 * f1); // 3D distance -> sqrt(x^2 + z^2 + y^2)

        float f3 = phantom.yaw;
        float f4 = (float) MathHelper.d((double) f2, (double) f);

        float f5 = MathHelper.g(phantom.yaw + 90.0F);
        float f6 = MathHelper.g(f4 * 57.295776F);

        phantom.yaw = MathHelper.d(f5, f6, 4.0F) - 90.0F;
        phantom.aA = phantom.yaw;

        this.j = Math.max(this.j, 0.5f);

        if (MathHelper.d(f3, phantom.yaw) < 3.0F) {
            this.j = MathHelper.c(this.j, 1.8F, 0.005F * (1.8F / this.j));
        } else {
            this.j = MathHelper.c(this.j, 0.2F, 0.025F);
        }

        float f7 = (float) (-(MathHelper.d((double) (-f1), d0) * 57.2957763671875D));
        phantom.pitch = f7;
        float f8 = phantom.yaw + 75.0F; // 90.0

        double d3 = (double) (this.j * MathHelper.cos(f8 * 0.017453292F)) * Math.abs((double) f / d2);
        double d4 = (double) (this.j * MathHelper.sin(f7 * 0.017453292F)) * Math.abs((double) f1 / d2);
        double d5 = (double) (this.j * MathHelper.sin(f8 * 0.017453292F)) * Math.abs((double) f2 / d2);

        double sF = 0.03D; // speed factor

        Vec3D vec3d = phantom.getMot(); // current motion vector
        phantom.setMot(vec3d.e((new Vec3D(d3, d4, d5)).d(vec3d).a(sF)));
    }

}
