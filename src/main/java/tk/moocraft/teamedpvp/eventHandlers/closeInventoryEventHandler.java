package tk.moocraft.teamedpvp.eventHandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import  tk.moocraft.teamedpvp.customInventories.spectateOrPlayInv;
import  tk.moocraft.teamedpvp.modeControl.Mode;

public class closeInventoryEventHandler implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getReason().equals(InventoryCloseEvent.Reason.PLUGIN)) return;

        if (e.getInventory().getHolder() instanceof spectateOrPlayInv) {
            Player player = (Player) e.getPlayer();

            // Do your cool stuff
            Mode.getInstance().spectateMode(player);
            /*Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
                // code
                player.openInventory(e.getInventory());
            }, 1L);*/
        }
    }
}
