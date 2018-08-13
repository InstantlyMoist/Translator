package me.kyllian.translator;

import me.kyllian.translator.commands.LanguageCommand;
import me.kyllian.translator.listeners.AsyncPlayerChatListener;
import me.kyllian.translator.listeners.PlayerLoginListener;
import me.kyllian.translator.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class TranslatorPlugin extends JavaPlugin {

    private HashMap<UUID, PlayerData> playerDataHashMap;
    private MessageHandler messageHandler;
    private DataHandler dataHandler;
    private TranslationHandler translationHandler;
    public static TranslatorPlugin plugin;

    public static TranslatorPlugin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        playerDataHashMap = new HashMap<>();

        new PlayerLoginListener(this);
        new AsyncPlayerChatListener(this);

        getCommand("language").setExecutor(new LanguageCommand(this));

        messageHandler = new MessageHandler(this);
        dataHandler = new DataHandler(this);
        translationHandler = new TranslationHandler();

        Bukkit.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = getPlayerData(player.getUniqueId());
            playerData.setLanguage(Language.valueOf(getDataHandler().getData().getString(player.getUniqueId().toString())));
        });
    }

    public void onDisable() {
        getDataHandler().save();
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataHashMap.computeIfAbsent(uuid, f -> new PlayerData(uuid));
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public TranslationHandler getTranslationHandler() {
        return translationHandler;
    }
}
