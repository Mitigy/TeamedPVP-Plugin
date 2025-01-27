package com.mitigy.teamedpvp.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.atomic.AtomicReference;

import com.mitigy.teamedpvp.utils.FileUtility;
import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.utils.Utils;

public class statsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (Utils.sendNotPlayerErrorMsg(sender)) return false;

        if (args.length > 1) {
            Utils.coloredUsageText(sender, "/stats <player name>");
            return false;
        }

        // Get player object from player name
        OfflinePlayer queriedPlayer;
        if (args.length == 0) {
            queriedPlayer = (Player) sender;
        }else {
            queriedPlayer = Bukkit.getOfflinePlayer(args[0]);
        }

        // Check if the queried player is not real
        if (queriedPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return false;
        }

        // Set up the config
        FileUtility fileUtility = new FileUtility(queriedPlayer);
        FileConfiguration config = fileUtility.getConfig(false);

        // If the file for the player didn't exist then return an error
        if (config == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return false;
        }

        Integer kills = config.getInt("kills");
        Integer deaths = config.getInt("deaths");
        Double kdr = (deaths == 0 ? kills : Math.floor((kills/deaths) * 100)/100);

        Component horizontalLine = Component.text("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", NamedTextColor.BLUE, TextDecoration.BOLD);
        Component component =
            horizontalLine.append(newLine())
            .append(Component.text(Utils.createCenteredMsg("Stats for " + queriedPlayer.getName()), NamedTextColor.YELLOW).decoration(TextDecoration.BOLD, false)).append(newLine())
            .append(newLine())
            .append(Component.text(Utils.createCenteredMsg("Kills: " + kills), NamedTextColor.GOLD).decoration(TextDecoration.BOLD, false)).append(newLine())
            .append(Component.text(Utils.createCenteredMsg("Deaths: " + deaths), NamedTextColor.GOLD).decoration(TextDecoration.BOLD, false)).append(newLine())
            .append(Component.text(Utils.createCenteredMsg("K/D: " + kdr), NamedTextColor.GOLD).decoration(TextDecoration.BOLD, false)).append(newLine())
            .append(newLine())
            .append(horizontalLine);

        sender.sendMessage(component);
        return true;
    }

    public Component newLine() {
        return Component.newline();
    }
}

