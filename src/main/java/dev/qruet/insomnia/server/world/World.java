package dev.qruet.insomnia.server.world;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class World {

    private final UUID worldId;
    private final Map<UUID, EntityInsomniaPhantom> phantomMap = new HashMap<>();

    public World(org.bukkit.World world) {
        this.worldId = world.getUID();
    }

    public EntityInsomniaPhantom getPhantom(UUID id) {
        return phantomMap.getOrDefault(id, null);
    }

    public void spawnMortalPhantom(Player target) {
        EntityPlayer player = ((CraftPlayer) target).getHandle();
        Location pLoc = target.getLocation();

        EntityInsomniaPhantom phantom = new EntityInsomniaPhantom(EntityInsomniaPhantom.AttackPhase.CHASE, player.world);
        phantom.setPosition(pLoc.getX(), pLoc.getY() + 15, pLoc.getZ());
        phantom.setGoalTarget(player);

        phantomMap.put(phantom.getUniqueID(), phantom);

        player.world.addEntity(phantom, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public void spawnInsomniaPhantom(Player target, int level) {
        EntityPlayer player = ((CraftPlayer) target).getHandle();
        Location pLoc = target.getLocation();

        Random rnd = new Random();

        EntityInsomniaPhantom phantom = new EntityInsomniaPhantom(EntityInsomniaPhantom.AttackPhase.SWOOP, player.world, target, level);
        phantom.setPosition(pLoc.getX() + rnd.nextInt(50), pLoc.getY() + rnd.nextInt(35) + 25, pLoc.getZ() + rnd.nextInt(50));

        Insomnia.logger().info("Spawned phantom with id " + phantom.getUniqueID());
        phantomMap.put(phantom.getUniqueID(), phantom);

        player.world.addEntity(phantom, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    public org.bukkit.World getBukkitWorld() {
        return Bukkit.getWorld(worldId);
    }

    public void addPhantom(EntityInsomniaPhantom phantom) {
        phantomMap.put(phantom.getUniqueID(), phantom);
    }

    public void removePhantom(UUID id) {
        phantomMap.remove(id);
    }

}
