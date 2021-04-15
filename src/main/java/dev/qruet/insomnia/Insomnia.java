package dev.qruet.insomnia;

import com.google.common.base.Preconditions;
import dev.qruet.insomnia.effect.PotionEffectType;
import dev.qruet.insomnia.handlers.JoinHandler;
import dev.qruet.insomnia.http.WebServerHandler;
import dev.qruet.insomnia.io.config.ConfigIO;
import dev.qruet.insomnia.misc.Tasky;
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

        config = ConfigIO.setup(this);
        config.deserialize();

        this.httpHandler = new WebServerHandler(this);
        this.httpHandler.start();

        PotionEffectType.setup(this);

        Bukkit.getPluginManager().registerEvents(new JoinHandler(), this);
        getLogger().info("Enabled Insomnia.");
    }

    public void onDisable() {
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
