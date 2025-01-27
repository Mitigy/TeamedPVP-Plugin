package com.mitigy.teamedpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.utils.FileUtility;
import com.mitigy.teamedpvp.utils.Utils;

public class setlevelCommand implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        /* if (!(sender instanceof Player)) { // Check if the sender was a player
            sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
            return false;
        } */
        // Handle permissions
        if (Utils.sendPermissionError(sender, "teamedpvp.setlevel")) return false;

        // Handle incorrect usage
        if (args.length < 2) {
            Utils.coloredUsageText(sender, "/setlevel <target> <number>");
            return false;
        }

        // Handle grabbing the target player
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return false;
        }

        // Check the kill count
        int level = (int) Math.floor(Float.parseFloat(args[1]));
        if (level < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid level!");
            return false;
        }

        FileUtility fileUtility = new FileUtility(targetPlayer);
        if (fileUtility.getConfig(false) == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return false;
        }

        fileUtility.set("level", level, true);

        // We can only set a player's suffix if they are online
        if (targetPlayer.isOnline()) {
            Utils.updatePlayerLevelSuffix((Player) targetPlayer, level);
        }

        sender.sendMessage(ChatColor.GREEN + "Level for " + targetPlayer.getName() + " set to " + level + " successfully!");
        return true;
    }
}
