package dev.qruet.insomnia.server.world;

import dev.qruet.insomnia.Insomnia;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.effect.insomnia.InsomniaEffect;
import dev.qruet.insomnia.effect.render.GhostRenderer;
import dev.qruet.insomnia.nms.entity.EntityInsomniaPhantom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class World {

    private final UUID worldId;
    private final Map<UUID, InsomniaEffect> effectMap = new HashMap<>();

    private final GhostRenderer renderer;

    public World(org.bukkit.World world) {
        this.worldId = world.getUID();
        this.renderer = new GhostRenderer(JavaPlugin.getPlugin(Insomnia.class));
    }

    public EntityInsomniaPhantom getPhantom(UUID id) {
        for (InsomniaEffect effect : effectMap.values()) {
            EntityInsomniaPhantom phantom = effect.phantoms().stream().filter(e -> e.getUniqueID().equals(id)).findAny().orElse(null);
            if (phantom != null)
                return phantom;
        }

        return null;
    }

    public void spawnMortalPhantom(Player target) {
        throw new UnsupportedOperationException("Not yet implemented.");
        /*EntityPlayer player = ((CraftPlayer) target).getHandle();
        Location pLoc = target.getLocation();

        EntityInsomniaPhantom phantom = new EntityInsomniaPhantom(EntityInsomniaPhantom.AttackPhase.CHASE, player.world);
        phantom.setPosition(pLoc.getX(), pLoc.getY() + 15, pLoc.getZ());
        phantom.setGoalTarget(player);

        phantomMap.put(phantom.getUniqueID(), phantom);

        player.world.addEntity(phantom, CreatureSpawnEvent.SpawnReason.CUSTOM);*/
    }

    public void addInsomniaEffect(Player player, int level) {
        /** this will indirectly call {@link World#registerNewInsomniaEffect(Player player, InsomniaEffect effect) } **/
        player.addPotionEffect(new PotionEffect(PotionEffectType.INSOMNIA, 99999, level, true, true));
    }

    public void removeInsomniaEffect(Player player) {
        InsomniaEffect effect = getInsomniaEffect(player);
        if (effect == null)
            return;

        effect.end();
        effectMap.remove(player.getUniqueId());
    }

    public void registerNewInsomniaEffect(Player player, int i) {
        InsomniaEffect effect = getInsomniaEffect(player);
        if (effect != null) {
            effect.updateSeverity(i);
            return;
        }
        effect = new InsomniaEffect(player, i);
        effectMap.put(player.getUniqueId(), effect);
    }

    public InsomniaEffect getInsomniaEffect(Player player) {
        return effectMap.get(player.getUniqueId());
    }

    public org.bukkit.World getBukkitWorld() {
        return Bukkit.getWorld(worldId);
    }

}
