package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class playerMoveEventHandler implements Listener {`
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        Location playerLoc = player.getLocation();

        Boolean metACondition = false;

        if (playerLoc.getX() < -69.7) {
            playerLoc.setX(-69.7);
            player.teleport(playerLoc);
            metACondition = true;
        }
        if (playerLoc.getX() > 70.7) {
            playerLoc.setX(70.7);
            player.teleport(playerLoc);
            metACondition = true;
        }
        if (playerLoc.getZ() < -69.7) {
            playerLoc.setZ(-69.7);
            player.teleport(playerLoc);
            metACondition = true;
        }
        if (playerLoc.getZ() > 70.7) {
            playerLoc.setZ(70.7);
            player.teleport(playerLoc);
            metACondition = true;
        }

        if (metACondition) {
            player.sendMessage(ChatColor.RED + "You cannot leave the region!");
        }
    }
}
