package dev.qruet.insomnia.nms;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.misc.ReflectionUtils;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

/**
 * Pulled from:
 * https://www.spigotmc.org/threads/injecting-entitytypes-for-custom-entities.376361/
 *
 * @author Daskrr
 * @author qruet
 * https://www.spigotmc.org/members/daskrr.686639/
 */
public enum EntityTypeRegistry {

    INSOMNIA_PHANTOM("phantom", EntityType.PHANTOM, EntityInsomniaPhantom.class, EntityInsomniaPhantom::new, EntityInsomniaPhantom::new);

    private String name;
    private EntityType entityType;
    private Class<? extends EntityInsentient> customClass;
    private EntityTypes.b<Entity> b;
    private Function<Entity, Entity> adoptHandler;

    EntityTypeRegistry(String name, EntityType entityType, Class<? extends EntityInsentient> customClass,
                       EntityTypes.b<Entity> b_types, Function<Entity, Entity> adoptHandler) {
        this.name = name;
        this.entityType = entityType;
        this.customClass = customClass;
        this.b = b_types;
        this.adoptHandler = adoptHandler;
    }

    public Entity createEntity(EntityTypes<Entity> types, World world) {
        return b.create(types, world);
    }

    public Entity transformEntity(Entity entity) {
        return adoptHandler.apply(entity);
    }

    public String getName() {
        return name;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Class<? extends EntityInsentient> getCustomClass() {
        return customClass;
    }

}
