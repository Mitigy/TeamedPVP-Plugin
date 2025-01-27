package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;

import com.mitigy.teamedpvp.modeControl.Mode;

public class onHeldItemChangeEventHandler implements Listener {
    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent e){
        Player player = e.getPlayer();

        // If the player isn't in spectate, play, or super-spectate mode cancel the event
        if (Mode.getInstance().getPlayersMode(player.getUniqueId()).equals(-1)) {
            e.setCancelled(true);
        }
    }
}
