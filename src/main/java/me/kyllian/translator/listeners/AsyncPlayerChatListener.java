package me.kyllian.translator.listeners;

import me.kyllian.translator.TranslatorPlugin;
import me.kyllian.translator.utils.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class AsyncPlayerChatListener implements Listener {

    private TranslatorPlugin plugin;

    public AsyncPlayerChatListener(TranslatorPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerData(player.getUniqueId());
        new ArrayList<Player>(event.getRecipients()).forEach(recipient -> {
            if (recipient != player) event.getRecipients().remove(recipient);
            if (player != recipient) {
                PlayerData recipientData = plugin.getPlayerData(recipient.getUniqueId());
                try {
                    String translatedMessage = playerData.getLanguage() == recipientData.getLanguage() ?
                            event.getMessage() :
                            plugin.getTranslationHandler().translate(event.getMessage(), playerData.getLanguage(), recipientData.getLanguage());
                    recipient.sendMessage(event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", translatedMessage));
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        });
    }
}
