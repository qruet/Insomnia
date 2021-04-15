package dev.qruet.insomnia.io.texture;

import dev.qruet.insomnia.Insomnia;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Handles updating and references to the resource pack
 */
public class ResourcePackIO {

    public static File getResourcePath() {
        return new File(JavaPlugin.getPlugin(Insomnia.class).getDataFolder().getPath() + "/resources/pack.zip");
    }

}
