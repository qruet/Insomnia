package dev.qruet.insomnia.nms.entity;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.misc.ReflectionUtils;
import dev.qruet.insomnia.misc.Tasky;
import dev.qruet.insomnia.nms.entity.controller.PhantomControllerLook;
import dev.qruet.insomnia.nms.entity.controller.PhantomControllerMove;
import dev.qruet.insomnia.nms.entity.pathfinder.*;
import dev.qruet.insomnia.server.Server;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.UUID;

public class EntityInsomniaPhantom extends net.minecraft.server.v1_16_R3.EntityPhantom {

    private static final JavaPlugin PLUGIN = JavaPlugin.getPlugin(Insomnia.class);

    public Vec3D c;
    public BlockPosition d;
    protected PersistentDataContainer container;

    protected AttackPhase phase;

    protected int level;

    protected Player target;

    private EntityInsomniaPhantom(World world) {
        super(EntityTypes.PHANTOM, world);
        this.d = BlockPosition.ZERO;
        this.c = Vec3D.ORIGIN;

        this.moveController = new PhantomControllerMove(this);
        this.lookController = new PhantomControllerLook(this);

        this.goalSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());
        this.targetSelector = new PathfinderGoalSelector(world.getMethodProfilerSupplier());

        this.level = -1;

        this.setPersistent();
    }

    /**
     * Responsible for copying data from {@link Entity} into a new {@link EntityInsomniaPhantom} instance.
     *
     * @param entity
     */
    public EntityInsomniaPhantom(Entity entity) {
        this(entity.world);

        this.setPosition(entity.locX(), entity.locY(), entity.locZ());

        this.container = entity.getBukkitEntity().getPersistentDataContainer();

        NBTTagCompound tag = new NBTTagCompound();
        tag = entity.save(tag); // populate NBTTagCompound with entity NBT data

        loadData(tag);

        //Server.getWorld(world.getWorld().getUID()).addPhantom(this);
    }

    /**
     * This constructor is utilized to instantiate a ghost type {@link EntityInsomniaPhantom} instance
     *
     * @param phase  Initial AttackPhase for ghost
     * @param world  World to initialize entity into
     * @param target Player the phantom "haunts"
     * @param level  Entity difficulty
     */
    public EntityInsomniaPhantom(AttackPhase phase, World world, Player target, int level) {
        this(world);
        this.phase = phase;

        this.target = target;
        this.level = MathHelper.clamp(level, 0, 4);

        if (level <= 0) {
            this.setSize(-4);
        } else {
            this.setSize(-3);
        }

        this.noclip = true; // only ghosts can clip through walls

        this.container = getBukkitEntity().getPersistentDataContainer();

        if (level <= 2) {
            this.goalSelector.a(1, new LightPathfinderGoal(this));
            this.goalSelector.a(2, new GustPathfinderGoal(this));
        }
        this.goalSelector.a(2, new AttackPathfinderGoal(this));
        this.goalSelector.a(3, new CirclePathfinderGoal(this));
        this.targetSelector.a(1, new TargetPathfinderGoal(this));

        if (target == null)
            return;

        setGoalTarget(((CraftPlayer) target).getHandle(), EntityTargetEvent.TargetReason.CUSTOM, false);
    }

    /**
     * This constructor is utilized to instantiate a mortal type {@link EntityInsomniaPhantom} instance
     *
     * @param phase Initial AttackPhase for phantom
     * @param world World to initialize entity into
     */
    public EntityInsomniaPhantom(AttackPhase phase, World world) {
        this(world);
        this.phase = phase;

        this.container = getBukkitEntity().getPersistentDataContainer();
    }

    public EntityInsomniaPhantom(EntityTypes<Entity> entityTypes, World world) {
        this(AttackPhase.CIRCLE, world, null, 0);
    }

    public boolean isGhost() {
        return target != null;
    }

    public Player getTarget() {
        return target;
    }

    @Override
    public void setSize(int i) {
        Tasky.sync(t -> {
            Field b = ReflectionUtils.getField(EntityPhantom.class, "b");
            try {
                datawatcher.set((DataWatcherObject<Integer>) b.get(null), i);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void tick() {
        super.tick();

        if (isNoAI())
            return;

        if (isGhost()) {
            float f = MathHelper.cos((float) (this.getId() * 3 + this.ticksLived) * 0.13F + 3.1415927F);
            float f1 = MathHelper.cos((float) (this.getId() * 3 + this.ticksLived + 1) * 0.13F + 3.1415927F);

            if (f > 0.0F && f1 <= 0.0F) {
                this.world.a(this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getSize();
            float f2 = MathHelper.cos(this.yaw * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f3 = MathHelper.sin(this.yaw * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);

            Location loc = new Location(world.getWorld(), this.locX() + ((-1 * getLookDirection().x)), this.locY(), this.locZ() + ((-1 * getLookDirection().z)));
            Location loc1 = new Location(world.getWorld(), this.locX() + (double) f2 + ((-1 * getLookDirection().x)), this.locY() + (double) f4, this.locZ() + (double) f3 + ((-1 * getLookDirection().z)));
            Location loc2 = new Location(world.getWorld(), this.locX() - (double) f2 + ((-1 * getLookDirection().x)), this.locY() + (double) f4, this.locZ() - (double) f3 + ((-1 * getLookDirection().z)));

            target.spawnParticle(org.bukkit.Particle.SMOKE_NORMAL, loc, 1, 0, 0, 0, 0);
            target.spawnParticle(org.bukkit.Particle.SMOKE_NORMAL, loc1, 1, 0, 0, 0, 0);
            target.spawnParticle(org.bukkit.Particle.SMOKE_NORMAL, loc2, 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean attackEntity(Entity entity) {
        if (!isGhost())
            return super.attackEntity(entity);

        float f = level * 2.5f;
        float f1 = (float) this.b((AttributeBase) GenericAttributes.ATTACK_KNOCKBACK);

        if (entity instanceof EntityLiving) {
            f += EnchantmentManager.a(this.getItemInMainHand(), ((EntityLiving) entity).getMonsterType());
            f1 += (float) EnchantmentManager.b(this);
        }

        int i = EnchantmentManager.getFireAspectEnchantmentLevel(this);
        if (i > 0) {
            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), i * 4);
            Bukkit.getPluginManager().callEvent(combustEvent);
            if (!combustEvent.isCancelled()) {
                entity.setOnFire(combustEvent.getDuration(), false);
            }
        }

        boolean flag = entity.damageEntity(DamageSource.GENERIC, f);
        if (flag) {
            if (f1 > 0.0F && entity instanceof EntityLiving) {
                ((EntityLiving) entity).a(f1 * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double) (-MathHelper.cos(this.yaw * 0.017453292F)));
                this.setMot(this.getMot().d(0.6D, 1.0D, 0.6D));
            }

            this.a((EntityLiving) this, (Entity) entity);
            this.z(entity);
        }

        return flag;
    }

    @Override
    public boolean damageEntity(DamageSource source, float f) {
        if (level <= 2) {
            disappear();
            return true;
        }
        return super.damageEntity(source, f);
    }

    @Override
    public void movementTick() {
        if (isNoAI())
            return;
        super.movementTick();
        this.setFireTicks(0); // disable fire
    }

    public void disappear() {
        if (target != null)
            target.playSound(getBukkitEntity().getLocation(), Sound.ENTITY_PHANTOM_DEATH, 0.7f, 0.7f);
        else
            world.getWorld().playSound(getBukkitEntity().getLocation(), Sound.ENTITY_PHANTOM_DEATH, 0.7f, 0.7f);
        world.getWorld().spawnParticle(org.bukkit.Particle.SMOKE_LARGE, getBukkitEntity().getLocation(), 20, 0, 0, 0, 0.2);
        die();
    }

    @Override
    public void die() {
        super.die();
        //Server.getWorld(world.getWorld().getUID()).removePhantom(uniqueID);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);

        if (container == null)
            container = getBukkitEntity().getPersistentDataContainer();

        container.set(new NamespacedKey(PLUGIN, "Size"), PersistentDataType.INTEGER, getSize());
        container.set(new NamespacedKey(PLUGIN, "Level"), PersistentDataType.INTEGER, level);
        container.set(new NamespacedKey(PLUGIN, "AttackPhase"), PersistentDataType.STRING, getCurrentPhase().name());
        container.set(new NamespacedKey(PLUGIN, "Target"), PersistentDataType.STRING, target != null ? target.getUniqueId().toString() : "");
    }

    @Override
    public void loadData(NBTTagCompound nbtTagCompound) {
        super.loadData(nbtTagCompound);

        JavaPlugin plugin = JavaPlugin.getPlugin(Insomnia.class);

        if (container == null)
            container = getBukkitEntity().getPersistentDataContainer();

        int size = container.getOrDefault(new NamespacedKey(plugin, "Size"), PersistentDataType.INTEGER, 0);
        int level = container.getOrDefault(new NamespacedKey(plugin, "Level"), PersistentDataType.INTEGER, 0);
        AttackPhase phase = AttackPhase.valueOf(container.getOrDefault(new NamespacedKey(plugin, "AttackPhase"), PersistentDataType.STRING, AttackPhase.CIRCLE.name()));
        String targetId = container.getOrDefault(new NamespacedKey(plugin, "Target"), PersistentDataType.STRING, "");
        if (!targetId.isEmpty())
            target = Bukkit.getPlayer(UUID.fromString(targetId));

        setSize(size);

        setFireTicks(0); // disable entity burning

        this.level = MathHelper.clamp(level, 0, 4);

        this.noclip = isGhost(); // only ghosts can clip through walls

        setCurrentPhase(phase);
    }

    public void setCurrentPhase(AttackPhase phase) {
        this.phase = phase;
    }

    public AttackPhase getCurrentPhase() {
        return phase;
    }

    public static enum AttackPhase {
        CIRCLE,
        CHASE,
        SWOOP,
        GUST;

        private AttackPhase() {
        }
    }

}
