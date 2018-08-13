package me.kyllian.translator.listeners;

import me.kyllian.translator.TranslatorPlugin;
import me.kyllian.translator.utils.Language;
import me.kyllian.translator.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener implements Listener {

    private TranslatorPlugin plugin;

    public PlayerLoginListener(TranslatorPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerData(player.getUniqueId());
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
