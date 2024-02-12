package tk.moocraft.teamedpvp.eventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import  tk.moocraft.teamedpvp.gameLogic.GameControl;

public class entityDamageEventHandler implements Listener {

    @EventHandler
    public void onDamageTaken(EntityDamageEvent e) {
        if (GameControl.getInstance().getGameState() != 1) {
            e.setCancelled(true);
        }
    }
}
