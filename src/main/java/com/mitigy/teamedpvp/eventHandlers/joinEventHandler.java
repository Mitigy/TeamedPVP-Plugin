package com.mitigy.teamedpvp.eventHandlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;

import com.mitigy.teamedpvp.utils.FileUtility;
import com.mitigy.teamedpvp.customInventories.spectateOrPlayInv;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.modeControl.Mode;
import com.mitigy.teamedpvp.utils.Board;
import com.mitigy.teamedpvp.utils.ScoreboardManager;
import com.mitigy.teamedpvp.utils.Utils;

public class joinEventHandler implements Listener {
    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        PlayerInventory inventory = player.getInventory();
        inventory.clear();

        player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5));
        player.setBedSpawnLocation(new Location(Bukkit.getWorld("world"), 0.5, 0, 0.5), true);

        ScoreboardManager managerInstance = ScoreboardManager.getInstance();
        Board board = managerInstance.createBoard(player, "TeamedPVP", Component.text("TeamedPVP").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
//        board.addBlankSpace();
//        wrapper.addLine(Utils.replaceColorCodes("&cRed: " + gameControlInstance.getRedTeamList().size() + "/" + queueInstance.getQueueList().size()));
//        wrapper.addLine(Utils.replaceColorCodes("&9Blue: " + gameControlInstance.getBlueTeamList().size() + "/" + queueInstance.getQueueList().size()));
//        wrapper.addBlankSpace();
//        wrapper.addLine(Utils.replaceColorCodes("&elocalhost"));
        board.set(4, ChatColor.BLACK + "                ");
        // The lines 2 and 3 are set depending on if there is a game occurring
        board.set(1, "");
        board.set(0, Utils.replaceColorCodes("&eTeamedPVP"));

        managerInstance.showBoard(player, board);

        // Update the player's level suffix
        FileUtility fileUtility = new FileUtility(player);
        FileConfiguration config = fileUtility.getConfig(false);
        Integer level = 0;

        // If a config was able to be loaded then use it to set the player's suffix
        if (config != null) {
            level = config.getInt("level");
        }
        Utils.updatePlayerLevelSuffix(player, level);

        if (GameControl.getInstance().getGameState() == 1) {
            // Open the 'Spectate or Play' inventory and set a tag on the player, so we know it is open
            Mode.getInstance().spectateMode(player);
            player.openInventory(new spectateOrPlayInv().getInventory());

            // Set the lines to be the team sizes
            Utils.updateScoreboardTeamSizes(player);
        }else {
            Mode.getInstance().prePlayMode(player);
        }
    }
}
