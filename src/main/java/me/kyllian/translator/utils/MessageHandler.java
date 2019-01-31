package me.kyllian.translator.utils;

import me.kyllian.translator.TranslatorPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MessageHandler {

    private TranslatorPlugin plugin;
    private File file;
    private FileConfiguration fileConfiguration;

    public MessageHandler(TranslatorPlugin plugin) {
        this.plugin = plugin;

        file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        fileConfiguration = YamlConfiguration.loadConfiguration(file);
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

    public String getUnknownLanguageMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("UnknownLanguage"));
    }

    public String getNotAPlayerMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("NotAPlayer"));
    }

    public String getLanguageSetMessage(String language) {
        return MessageUtils.colorTranslate(String.format(fileConfiguration.getString("LanguageSet"), language));
    }

    public String getUnknownArgumentMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("UnknownArgument"));
    }

    public String getSetLanguageMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("SetLanguage"));
    }

    public String getDetectedLanguageMessage(Language detectedLanguage) {
        return MessageUtils.colorTranslate(String.format(fileConfiguration.getString("DetectedLanguage"), detectedLanguage.toString()));
    }

    public String getTurnedOnMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("TurnedOn"));
    }

    public String getTurnedOffMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("TurnedOff"));
    }

    public String getCheckingUpdateMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("CheckingUpdate"));
    }

    public String getUpdateFoundMessage(String oldVersion, String newVersion) {
        return MessageUtils.colorTranslate(fileConfiguration.getString(".CheckingUpdate").replace("%oldversion%", oldVersion)
                .replace("%newversion%", newVersion).replace("%url%", plugin.getUpdateChecker().getResourceURL()));
    }

    public String getUpdateNotFoundMessage() {
        return MessageUtils.colorTranslate(fileConfiguration.getString("UpdateNotFound"));
    }
}
