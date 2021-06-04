package dev.qruet.insomnia.effect.render;

import com.google.common.collect.Lists;
import dev.qruet.insomnia.misc.Tasky;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class GhostRenderer {

    /**
     * Team of ghosts and people who can see ghosts.
     */
    private static final String GHOST_TEAM_NAME = "Ghosts";
    private static final long UPDATE_DELAY = 20L;

    private Team ghostTeam;

    // Task that must be cleaned up
    private BukkitRunnable task;
    private boolean closed;

    // Players that are actually ghosts
    private Set<String> ghosts = new HashSet<>();

    public GhostRenderer(JavaPlugin plugin) {
        // Initialize
        createTask(plugin);
        createGetTeam();
    }

    private void createGetTeam() {
        Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        ghostTeam = board.getTeam(GHOST_TEAM_NAME);

        // Create a new ghost team if needed
        if (ghostTeam == null) {
            ghostTeam = board.registerNewTeam(GHOST_TEAM_NAME);
        }
        ghostTeam.setCanSeeFriendlyInvisibles(true);
    }

    private void createTask(JavaPlugin plugin) {
        task = Tasky.sync((t) -> {
            for (Entity member : getMembers()) {
                if (member instanceof Player) {
                    Player player = (Player) member;
                    // Update invisibility effect
                    setGhost(player, isGhost(player));
                }
            }
        }, UPDATE_DELAY, UPDATE_DELAY);
    }

    /**
     * Remove all existing player members and ghosts.
     */
    public void clearMembers() {
        if (ghostTeam != null) {
            for (String entry : ghostTeam.getEntries()) {
                ghostTeam.removeEntry(entry);
            }
        }
    }

    /**
     * Add the given player to this ghost manager. This ensures that it can see ghosts, and later become one.
     */
    public void addEntity(LivingEntity entity) {
        validateState();

        if (entity instanceof Player) {
            ghostTeam.addEntry(((Player) entity).getName());
        } else {
            ghostTeam.addEntry(entity.getUniqueId().toString());
        }
    }

    /**
     * Determine if the given player is tracked by this ghost manager and is a ghost.
     *
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean isGhost(LivingEntity entity) {
        return entity != null && hasEntity(entity) && ghosts.contains(entity.getUniqueId().toString());
    }

    /**
     * Determine if the current player is tracked by this ghost manager, or is a ghost.
     *
     * @return TRUE if it is, FALSE otherwise.
     */
    public boolean hasEntity(LivingEntity entity) {
        validateState();
        if (entity instanceof Player) {
            return ghostTeam.hasEntry(((Player) entity).getName());
        } else {
            return ghostTeam.hasEntry(entity.getUniqueId().toString());
        }
    }

    /**
     * Set wheter or not a given player is a ghost.
     *
     * @param isGhost - TRUE to make the given player into a ghost, FALSE otherwise.
     */
    public void setGhost(LivingEntity entity, boolean isGhost) {
        // Make sure the player is tracked by this manager
        if (!hasEntity(entity))
            addEntity(entity);

        if (isGhost) {
            ghosts.add(entity.getUniqueId().toString());
            entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        } else if (!isGhost) {
            ghosts.remove(entity.getUniqueId().toString());
            entity.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }

    public void remove(LivingEntity entity) {
        ghosts.remove(entity.getUniqueId().toString());
        if (entity instanceof Player) {
            ghostTeam.removeEntry(((Player) entity).getName());
        } else {
            ghostTeam.removeEntry(entity.getUniqueId().toString());
        }
    }

    /**
     * Retrieve every ghost and every player that can see ghosts.
     *
     * @return Every ghost or every observer.
     */
    public List<Entity> getMembers() {
        validateState();
        List<Entity> entityList = Lists.newLinkedList();
        for (String entry : ghostTeam.getEntries()) {
            try {
                UUID id = UUID.fromString(entry);
                entityList.add(Bukkit.getEntity(id));
            } catch (IllegalArgumentException e) {
                entityList.add(Bukkit.getPlayer(entry));
            }
        }
        return entityList;
    }

    public void close() {
        if (!closed) {
            task.cancel();
            ghostTeam.unregister();
            closed = true;
        }
    }

    public boolean isClosed() {
        return closed;
    }

    private void validateState() {
        if (closed) {
            throw new IllegalStateException("Ghost factory has closed. Cannot reuse instances.");
        }
    }

}
