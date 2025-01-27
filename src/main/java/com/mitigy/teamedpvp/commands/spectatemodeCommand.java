package com.mitigy.teamedpvp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.modeControl.Mode;
import com.mitigy.teamedpvp.utils.Utils;

public class spectatemodeCommand implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (Utils.sendNotPlayerErrorMsg(sender)) return false;
        if (args.length != 0) {
            Utils.coloredUsageText(sender, "/spectatemode");
            return false;
        }
        // If the command can't be used based on the condition then return
        if (Utils.sendCommandCannotBeUsedNow(sender, GameControl.getInstance().getGameState() == 1)) return false;

        Player player = (Player) sender;

        // Make sure the player isn't in gameplay
        if (Utils.sendCommandInGameplayErrorMsg(player)) return false;

        Mode.getInstance().spectateMode(player);

        Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
            // code
            player.closeInventory();
        }, 1L);
        Utils.sendMsgPluginPrefix(player, "You are now spectating the game!");
        return true;
    }
}
