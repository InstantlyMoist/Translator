package me.kyllian.translator.utils;

import javafx.scene.transform.Translate;
import me.kyllian.translator.TranslatorPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class DataHandler {

    private TranslatorPlugin plugin;
    private File file;
    private FileConfiguration fileConfiguration;

    public DataHandler(TranslatorPlugin plugin) {
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            plugin.saveResource("data.yml", false);
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);

        new BukkitRunnable() {
            public void run() {
                save();
            }
        }.runTaskTimer(plugin, 20 * 60, 20 * 60);
    }

    public FileConfiguration getData() {
        return fileConfiguration;
    }

    public void save() {
        try {
            fileConfiguration.save(file);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public void reload() {
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }
}
