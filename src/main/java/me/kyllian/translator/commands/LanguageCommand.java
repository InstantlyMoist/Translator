package me.kyllian.translator.commands;

import me.kyllian.translator.TranslatorPlugin;
import me.kyllian.translator.utils.Language;
import me.kyllian.translator.utils.PlayerData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LanguageCommand implements CommandExecutor {

    private TranslatorPlugin plugin;

    public LanguageCommand(TranslatorPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (args.length == 1) {
            // /language lang
            String language = args[0];
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(plugin.getMessageHandler().getNotAPlayerMessage());
                return true;
            }
            Player player = (Player) commandSender;
            if (Language.valueOf(language) == null) {
                player.sendMessage(plugin.getMessageHandler().getUnknownLanguageMessage());
                return true;
            }
            PlayerData playerData = plugin.getPlayerData(player.getUniqueId());
            plugin.getDataHandler().getData().set(player.getUniqueId().toString(), language);
            playerData.setLanguage(Language.valueOf(language));
            player.sendMessage(plugin.getMessageHandler().getLanguageSetMessage(language));
            return true;
        }
        commandSender.sendMessage(plugin.getMessageHandler().getUnknownArgumentMessage());
        return true;
    }
}
