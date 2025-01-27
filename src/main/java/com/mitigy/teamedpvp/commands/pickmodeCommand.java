package com.mitigy.teamedpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.customInventories.spectateOrPlayInv;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.utils.Utils;

public class pickmodeCommand implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (Utils.sendNotPlayerErrorMsg(sender)) return false;
        if (args.length != 0) {
            Utils.coloredUsageText(sender, "/pickmode");
            return false;
        }
        // If the command can't be used based on the condition then return
        if (Utils.sendCommandCannotBeUsedNow(sender, GameControl.getInstance().getGameState() == 1)) return false;

        Player player = ((Player) sender);

        // Make sure the player isn't in gameplay
        if (Utils.sendCommandInGameplayErrorMsg(player)) return false;

        player.openInventory(new spectateOrPlayInv().getInventory());
        Utils.sendMsgPluginPrefix(player, "Mode selector opened!");
        return true;
    }
}
