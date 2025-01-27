package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.logging.Logger;

import com.mitigy.teamedpvp.TeamedPVP;

public class executeCommandItems implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        // Only listen to right clicks
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK && e.getAction() != Action.RIGHT_CLICK_AIR) return;
        // Do nothing if there is no item
        if(e.getItem() == null) return;

        ItemStack item = e.getItem();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        // Cancel the event so that the item can't be used/placed
        // Disable/cancel the item interaction if requested
        NamespacedKey canInteract = new NamespacedKey(TeamedPVP.getInstance(), "can-interact");
        if (container.has(canInteract, PersistentDataType.STRING) && container.get(canInteract, PersistentDataType.STRING).equals("false")) {
            e.setCancelled(true);
            e.setUseItemInHand(Event.Result.DENY);
        }

        // Only check the item in main hand
        if(e.getHand() != EquipmentSlot.HAND) return;

        NamespacedKey key = new NamespacedKey(TeamedPVP.getInstance(), "interact-execute-command");
        if(container.has(key, PersistentDataType.STRING)) {
            String foundValue = container.get(key, PersistentDataType.STRING);
            e.getPlayer().performCommand(foundValue);
            Logger log = Bukkit.getLogger();
            log.info(e.getPlayer().getName() + " issued server command: /" + foundValue);
        }
    }
}
