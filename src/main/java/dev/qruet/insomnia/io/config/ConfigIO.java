package dev.qruet.insomnia.io.config;

import dev.qruet.insomnia.Insomnia;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;

/**
 * I won't lie, I mostly copied and pasted the code from my LanguageIO class
 * (if it ain't broke, don't fix it)
 */
public class ConfigIO {

    private static final String fileName = "config.yml";
    private final File rootDir;
    private final File rootFile;

    private final Map<String, Object> OBJECT_MAP = new HashMap<>();

    private final JavaPlugin plugin;

    public static ConfigIO setup(JavaPlugin plugin) {
        return new ConfigIO(plugin);
    }

    private ConfigIO(JavaPlugin plugin) {
        this.plugin = plugin;
        this.rootDir = new File(plugin.getDataFolder().getPath());
        this.rootFile = new File(rootDir, fileName);
    }

    //TODO clean this up
    public void reload(Consumer<String> tracker) {
        if (!rootFile.exists())
            serialize();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(rootFile);
        List<String> updatedKeys = new LinkedList<>();
        for (String key : config.getKeys(true)) {
            Object val = config.get(key);
            updatedKeys.add(key);


            if (val instanceof MemorySection)
                continue;

            Object original = OBJECT_MAP.get(key);
            if (val instanceof ArrayList) {
                List<String> list = new ArrayList<>((ArrayList<String>) val);
                List<String> oldList = original == null ? new ArrayList<>() : (ArrayList<String>) original;

                if (!oldList.equals(list)) {
                    if (OBJECT_MAP.containsKey(key))
                        tracker.accept("&eUpdating - &6" + key + "&8:");
                    else
                        tracker.accept("&aAdding - &6" + key + "&8:");
                    for (int i = 0; i < list.size(); i++) {
                        String o_ln = i < oldList.size() ? oldList.get(i) : "";
                        String n_ln = list.get(i);

                        if (o_ln.equalsIgnoreCase(n_ln))
                            continue;

                        tracker.accept("    &7" + (i + 1) + " | &4" + o_ln + " &7-> &c" + n_ln);
                    }
                }
            } else if (original == null || !original.equals(val)) {
                if (original == null)
                    tracker.accept("&a* Adding - &7" + key + "&8: &4" + original + " &7-> &c" + val);
                else
                    tracker.accept("&e* Updating - &7" + key + "&8: &4" + original + " &7-> &c" + val);
            }

            OBJECT_MAP.put(key, val);
        }

        for (String key : new HashSet<>(OBJECT_MAP.keySet())) {
            if (updatedKeys.contains(key))
                continue;

            Object val = OBJECT_MAP.remove(key);

            if (val instanceof ArrayList) {
                List<String> list = new ArrayList<>((ArrayList<String>) val);

                tracker.accept("&cRemoving - &7" + key + "&8:");
                for (int i = 0; i < list.size(); i++) {
                    String ln = list.get(i);

                    tracker.accept("    &7" + (i + 1) + " | &4" + ln);
                }
            } else {
                tracker.accept("&c* Removing - &7" + key + "&8: &4" + val);
            }
        }

    }


    /**
     * Read and store values into config from memory
     */
    public void serialize() {
        if (!rootFile.exists()) {
            JavaPlugin.getPlugin(Insomnia.class).saveResource(fileName, false);
        }
    }

    /**
     * Read and load values in config into memory
     */
    public void deserialize() {
        if (!rootFile.exists())
            serialize();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(rootFile);
        for (String key : config.getKeys(true)) {
            OBJECT_MAP.put(key, config.get(key));
        }
    }

    /**
     * Check if value exists in memory
     * @param path
     * @return
     */
    public boolean contains(String path) {
        return OBJECT_MAP.containsKey(path);
    }

    /**
     * @param path      Path to value in config
     * @param valueType Value type from config
     * @param <T>
     * @return
     */
    public <T> T get(String path, Class<T> valueType) {
        return get(path, valueType, null, false);
    }

    /**
     * @param path         Path to value in config
     * @param valueType    Value type from config
     * @param defaultValue Returns this by default if value from path can not be retrieve (avoid NullPointers)
     * @param <T>
     * @return
     */
    public <T> T get(String path, Class<T> valueType, T defaultValue) {
        return get(path, valueType, defaultValue, false);
    }

    /**
     * @param path         Path to value in config
     * @param valueType    Value type from config
     * @param defaultValue Returns this by default if value from path can not be retrieve (avoid NullPointers)
     * @param mute         Disabled warnings
     * @param <T>
     * @return
     */
    public <T> T get(String path, Class<T> valueType, T defaultValue, boolean mute) {
        if (OBJECT_MAP.containsKey(path)) {
            Object val = OBJECT_MAP.get(path);
            if (valueType.isAssignableFrom(val.getClass())) {
                return valueType.cast(val);
            } else {
                if (!mute)
                    plugin.getLogger().severe("Expected " + valueType.getName() + " but got " + val.getClass().getName() + " from " + path + " in config!");
                return defaultValue;
            }
        }
        return defaultValue;
    }

}
