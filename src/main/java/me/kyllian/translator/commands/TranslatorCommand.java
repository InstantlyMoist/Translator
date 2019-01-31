package me.kyllian.translator.commands;

import me.kyllian.translator.TranslatorPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TranslatorCommand implements CommandExecutor {

    private TranslatorPlugin plugin;

    public TranslatorCommand(TranslatorPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (args.length == 1) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(plugin.getMessageHandler().getNotAPlayerMessage());
                return true;
            }
            Player player = (Player) commandSender;
            if (args[0].equalsIgnoreCase("off")) {
                plugin.getDataHandler().getData().set(player.getUniqueId().toString() + ".enabled", false);
                player.sendMessage(plugin.getMessageHandler().getTurnedOffMessage());
                return true;
            }
            if (args[0].equalsIgnoreCase("on")) {
                plugin.getDataHandler().getData().set(player.getUniqueId().toString() + ".enabled", true);
                player.sendMessage(plugin.getMessageHandler().getTurnedOnMessage());
                return true;
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (commandSender.hasPermission("autocast.update")) {
                    commandSender.sendMessage(plugin.getMessageHandler().getCheckingUpdateMessage());
                    plugin.getUpdateChecker().check();
                    commandSender.sendMessage(plugin.getUpdateChecker().getUpdateMessage());
                    return true;
                }
                return true;
            }
        }
        commandSender.sendMessage(plugin.getMessageHandler().getUnknownArgumentMessage());
        return true;
    }
}
