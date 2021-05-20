package dev.qruet.insomnia;

import com.google.common.base.Preconditions;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.handlers.JoinHandler;
import dev.qruet.insomnia.handlers.SleepHandler;
import dev.qruet.insomnia.http.WebServerHandler;
import dev.qruet.insomnia.io.config.ConfigIO;
import dev.qruet.insomnia.misc.Tasky;
import dev.qruet.insomnia.nms.EntityTypeHandler;
import dev.qruet.insomnia.nms.EntityTypeRegistry;
import dev.qruet.insomnia.nms.packet.PacketHandler;
import dev.qruet.insomnia.server.Server;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Insomnia extends JavaPlugin {

    private static Logger logger;
    private static ConfigIO config;

    private WebServerHandler httpHandler;
    private EntityTypeHandler entityHandler;
    private PacketHandler packetHandler;

    public void onEnable() {
        logger = getLogger();

        Tasky.setPlugin(this);

        config = ConfigIO.setup(this);
        config.deserialize();

        if (config.get("Resource Pack.Server.Enabled", Boolean.class)) {
            this.httpHandler = new WebServerHandler(this);
            this.httpHandler.start();
        }

        Server.init(this);

        entityHandler = new EntityTypeHandler();
        packetHandler = new PacketHandler();

        Bukkit.getPluginManager().registerEvents(entityHandler, this);
        Bukkit.getPluginManager().registerEvents(packetHandler, this);

        PotionEffectType.setup(this);

        Bukkit.getPluginManager().registerEvents(new JoinHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SleepHandler(), this);
        getLogger().info("Enabled Insomnia.");
    }

    public void onDisable() {
        HandlerList.unregisterAll(entityHandler);
        HandlerList.unregisterAll(packetHandler);

        packetHandler.disable();
        config.serialize();
        if (this.httpHandler != null)
            this.httpHandler.stop();
    }

    public WebServerHandler getHTTPServer() {
        return httpHandler;
    }

    public static ConfigIO getInsomniaConfig() {
        Preconditions.checkArgument(config != null, JavaPlugin.getPlugin(Insomnia.class).getDescription().getName() + " is not yet enabled.");
        return config;
    }

    /**
     * Easy access to plugin's logger
     *
     * @return
     */
    public static Logger logger() {
        Preconditions.checkArgument(logger != null, JavaPlugin.getPlugin(Insomnia.class).getDescription().getName() + " is not yet enabled.");
        return logger;
    }

}
