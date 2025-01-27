package com.mitigy.teamedpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.utils.Utils;

public class pingCommand implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /* if (!(sender instanceof Player)) { // Check if the sender was a player
            sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
            return false;
        } */
        if (args.length != 0) {
            Utils.coloredUsageText(sender, "/ping");
            return false;
        }

        Utils.sendMsgPluginPrefix(sender, Utils.replaceColorCodes("&6&lPONG!"));
        return true;
    }
}
