package dev.qruet.insomnia;

import com.google.common.base.Preconditions;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.handlers.JoinHandler;
import dev.qruet.insomnia.handlers.SleepHandler;
import dev.qruet.insomnia.http.WebServerHandler;
import dev.qruet.insomnia.io.config.ConfigIO;
import dev.qruet.insomnia.misc.Tasky;
import dev.qruet.insomnia.nms.EntityTypeRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Insomnia extends JavaPlugin {

    private static Logger logger;
    private static ConfigIO config;

    private WebServerHandler httpHandler;

    public void onEnable() {
        logger = getLogger();

        Tasky.setPlugin(this);

        EntityTypeRegistry.registerEntities();

        config = ConfigIO.setup(this);
        config.deserialize();

        if(config.get("Resource Pack.Server.Enabled", Boolean.class)) {
            this.httpHandler = new WebServerHandler(this);
            this.httpHandler.start();
        }

        PotionEffectType.setup(this);

        Bukkit.getPluginManager().registerEvents(new JoinHandler(), this);
        Bukkit.getPluginManager().registerEvents(new SleepHandler(), this);
        getLogger().info("Enabled Insomnia.");
    }

    public void onDisable() {
        config.serialize();
        if (this.httpHandler != null)
            this.httpHandler.stop();

        EntityTypeRegistry.unregisterEntities();
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
