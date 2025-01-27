package com.mitigy.teamedpvp.eventHandlers;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class cancelAdvancementCriterionGrant implements Listener {
    @EventHandler
    public void PlayerAdvancementCriterionGrantEvent(PlayerAdvancementCriterionGrantEvent e) {
        e.setCancelled(true);
    }
}
