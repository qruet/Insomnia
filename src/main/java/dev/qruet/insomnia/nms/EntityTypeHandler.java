package dev.qruet.insomnia.nms;

import dev.qruet.insomnia.Insomnia;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import javax.xml.validation.Validator;
import java.util.Arrays;

/**
 * @author qruet
 */
public class EntityTypeHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoad(WorldLoadEvent e) {
        org.bukkit.World world = e.getWorld();
        for (org.bukkit.entity.Entity ent : world.getEntities()) {
            Entity entity = ((CraftEntity) ent).getHandle();
            attemptEntityReplace(entity);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunk(ChunkLoadEvent e) {
        for (org.bukkit.entity.Entity ent : e.getChunk().getEntities()) {
            Entity entity = ((CraftEntity) ent).getHandle();
            attemptEntityReplace(entity);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSpawn(CreatureSpawnEvent e) {
        LivingEntity entity = e.getEntity();

        EntityLiving living = ((CraftLivingEntity) entity).getHandle();
        e.setCancelled(attemptEntityReplace(living));

    }

    private boolean attemptEntityReplace(Entity entity) {
        EntityTypeRegistry registry = Arrays.stream(EntityTypeRegistry.values())
                .filter(r -> r.getEntityType() == entity.getBukkitEntity().getType()).findAny().orElse(null);

        if (registry == null)
            return false;

        if (entity.getClass() == registry.getCustomClass())
            return false;

        Entity custom = registry.transformEntity(entity);
        Validate.notNull(custom, "Failed to transform entity for " + registry.getCustomClass().getName());

        entity.die();

        custom.world.addEntity(custom, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return true;
    }
}
