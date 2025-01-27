package com.mitigy.teamedpvp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.utils.Utils;
import com.mitigy.teamedpvp.gameLogic.Queue;

public class joinQueueCommand implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (Utils.sendNotPlayerErrorMsg(sender)) return false;
        if (args.length != 0) {
            Utils.coloredUsageText(sender, "/joinqueue");
            return false;
        }

        Player player = (Player) sender;

        // If the command can't be used based on the condition then return
        if (Utils.sendCommandCannotBeUsedNow(sender, GameControl.getInstance().getGameState() == 0)) return false;

        boolean wasAdded = Queue.getInstance().addPlayerToQueue(player);

        if (wasAdded) {
            Utils.sendMsgPluginPrefix(sender, Utils.replaceColorCodes("&aYou have successfully joined the queue!"));
        }else {
            Utils.sendMsgPluginPrefix(sender, Utils.replaceColorCodes("&cYou are already in the queue!"));
        }
        return true;
    }
}
