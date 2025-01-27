package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import com.mitigy.teamedpvp.modeControl.Mode;

public class foodLevelChangeEventHandler implements Listener {
    @EventHandler
    void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();

        // If the player is NOT in a game then don't let their food deplete
        if (Mode.getInstance().getPlayersMode(player.getUniqueId()) != 1) {
            e.setCancelled(true);
            player.setFoodLevel(20);
        }
    }
}
