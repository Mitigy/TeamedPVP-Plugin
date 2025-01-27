package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.mitigy.teamedpvp.customInventories.spectateOrPlayInv;
import com.mitigy.teamedpvp.modeControl.Mode;

public class closeInventoryEventHandler implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getReason().equals(InventoryCloseEvent.Reason.PLUGIN)) return;

        if (e.getInventory().getHolder() instanceof spectateOrPlayInv) {
            Player player = (Player) e.getPlayer();

            Mode.getInstance().spectateMode(player);
            /*Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
                // code
                player.openInventory(e.getInventory());
            }, 1L);*/
        }
    }
}
