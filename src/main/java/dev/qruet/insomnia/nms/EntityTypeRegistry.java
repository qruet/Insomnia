package dev.qruet.insomnia.nms;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.*;

import java.util.Map;

/**
 * Pulled from:
 * https://www.spigotmc.org/threads/injecting-entitytypes-for-custom-entities.376361/
 *
 * @author Daskrr
 * @author qruet
 * https://www.spigotmc.org/members/daskrr.686639/
 */
public enum EntityTypeRegistry {

    INSOMNIA_PHANTOM("ghost", EntityTypes.PHANTOM, EntityInsomniaPhantom.class, EntityInsomniaPhantom::new);

    private String name;
    private EntityTypes<? extends EntityInsentient> entityType;
    private Class<? extends EntityInsentient> customClass;
    private EntityTypes.b<?> b;

    EntityTypeRegistry(String name, EntityTypes<? extends EntityInsentient> entityType,
                       Class<? extends EntityInsentient> customClass,
                       EntityTypes.b<?> b_types) {
        this.name = name;
        this.entityType = entityType;
        this.customClass = customClass;
        this.b = b_types;
    }

    public String getName() {
        return name;
    }

    public EntityTypes<?> getEntityType() {
        return entityType;
    }

    public Class<? extends EntityInsentient> getCustomClass() {
        return customClass;
    }

    public static void registerEntities() {
        for (EntityTypeRegistry entity : values())
            registerCustomEntity(entity);
    }

    @SuppressWarnings("unchecked")
    public static void unregisterEntities() {
        for (EntityTypeRegistry entity : values()) {
            unregisterEntity(entity.name);
        }
    }

    public static void unregisterEntity(String name) {
        MinecraftKey minecraftKey = MinecraftKey.a(name);

        Map<Object, Type<?>> typeMap = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY)
                .types();
        typeMap.remove(minecraftKey);

        Insomnia.logger().info("Unregistered Entity with name \"" + name + "\" and key \"" + minecraftKey + "\"");
    }

    @SuppressWarnings("unchecked")
    private static void registerCustomEntity(EntityTypeRegistry type) {
        MinecraftKey minecraftKey = MinecraftKey.a(type.name);
        Map<Object, Type<?>> typeMap = (Map<Object, Type<?>>) DataConverterRegistry.a()
                .getSchema(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))
                .findChoiceType(DataConverterTypes.ENTITY)
                .types();
        String k = "minecraft:" + EntityTypes.getName(type.entityType).getKey();
        typeMap.put(minecraftKey, typeMap.get(k));
        EntityTypes.Builder<Entity> entityBuilder = EntityTypes.Builder.a(type.b, EnumCreatureType.CREATURE);

        IRegistry.a(IRegistry.ENTITY_TYPE, type.name, entityBuilder.a(type.name));

        Insomnia.logger().info("Registered Entity with name \"" + type.getName() + "\" and key \"" + minecraftKey + "\"");
    }
}
