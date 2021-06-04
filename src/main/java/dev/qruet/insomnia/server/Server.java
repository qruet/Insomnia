package dev.qruet.insomnia.server;

import dev.qruet.insomnia.effect.insomnia.InsomniaEffect;
import dev.qruet.insomnia.server.world.World;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class Server {

    private static final Map<UUID, World> WORLD_MAP = new HashMap<>();
    private static boolean a;

    public static void init(JavaPlugin plugin) {
        Validate.isTrue(!a, "Server has already been initialized.");
        a = true;
        org.bukkit.Server server = plugin.getServer();
        for (org.bukkit.World world : server.getWorlds()) {
            WORLD_MAP.put(world.getUID(), new World(world));
        }
    }

    public static InsomniaEffect getInsomniaEffect(Player player) {
        return getWorld(player.getWorld().getUID()).getInsomniaEffect(player);
    }

    public static Stream<World> getWorlds() {
        return WORLD_MAP.values().stream();
    }

    public static World getWorld(UUID id) {
        return WORLD_MAP.getOrDefault(id, null);
    }

}
