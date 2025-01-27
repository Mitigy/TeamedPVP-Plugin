package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import com.mitigy.teamedpvp.modeControl.Mode;

public class playerSwapHandItemsEventHandler implements Listener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        Player player = e.getPlayer();

        // If the player isn't in spectate, play, or super-spectate mode cancel the event
        if (Mode.getInstance().getPlayersMode(player.getUniqueId()).equals(-1)) {
            e.setCancelled(true);
        }
    }
}
