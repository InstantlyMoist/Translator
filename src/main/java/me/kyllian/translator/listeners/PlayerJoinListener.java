package me.kyllian.translator.listeners;

import me.kyllian.translator.TranslatorPlugin;
import me.kyllian.translator.utils.Language;
import me.kyllian.translator.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private TranslatorPlugin plugin;

    public PlayerJoinListener(TranslatorPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerData(player.getUniqueId());
        if (!player.hasPlayedBefore()) {
            String playerLanguage = playerData.getMinecraftLanguage();
            if (playerLanguage == null || Language.valueOf(playerLanguage) == null) {
                player.sendMessage(plugin.getMessageHandler().getSetLanguageMessage());
                return;
            }
            playerData.setLanguage(Language.valueOf(playerLanguage));
            plugin.getDataHandler().getData().set(player.getUniqueId().toString(), playerLanguage);
            player.sendMessage(plugin.getMessageHandler().getDetectedLanguageMessage(Language.valueOf(playerLanguage)));
            return;
        }
        if (plugin.getDataHandler().getData().get(player.getUniqueId().toString()) == null) {
            player.sendMessage(plugin.getMessageHandler().getSetLanguageMessage());
            plugin.getDataHandler().getData().set(player.getUniqueId().toString(), "unknown");
            return;
        }
        String language = plugin.getDataHandler().getData().getString(player.getUniqueId().toString());
        if (language.equalsIgnoreCase("unknown")) {
            player.sendMessage(plugin.getMessageHandler().getSetLanguageMessage());
            return;
        }
        playerData.setLanguage(Language.valueOf(language));
    }
}