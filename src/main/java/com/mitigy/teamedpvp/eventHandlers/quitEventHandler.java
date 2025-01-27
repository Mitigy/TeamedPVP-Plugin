package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.logging.Logger;

import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.gameLogic.Queue;

public class quitEventHandler implements Listener {
    Logger log = Bukkit.getLogger();

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) {
        playerDisconnectEvent(e.getPlayer());
    }

    @EventHandler
    public void PlayerKickEvent(PlayerKickEvent e) {
        playerDisconnectEvent(e.getPlayer());
    }

    private void playerDisconnectEvent(Player player) {
        // Removes the player from all teams
        GameControl gameControlInstance = GameControl.getInstance();
        gameControlInstance.movePlayerToTeam(player, "", false, true);

        Queue queueInstance = Queue.getInstance();
        queueInstance.removePlayerFromQueue(player);

//        GameControl.getInstance().getKillsHashMap().remove(player);
        log.info(player.getName() + " was removed from teams and the queue due to disconnect!");
    }
}
