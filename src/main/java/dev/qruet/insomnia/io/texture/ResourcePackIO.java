package dev.qruet.insomnia.io.texture;

import dev.qruet.insomnia.Insomnia;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Handles updating and references to the resource pack
 */
public class ResourcePackIO {

    public static String getResourcePath() {
        String address = Insomnia.getInsomniaConfig().get("Resource Pack.Download Link", String.class);
        if(address == null || address.isBlank()) {
            return JavaPlugin.getPlugin(Insomnia.class).getDataFolder().getPath() + "/resources/pack.zip";
        } else {
            return address;
        }
    }

}
