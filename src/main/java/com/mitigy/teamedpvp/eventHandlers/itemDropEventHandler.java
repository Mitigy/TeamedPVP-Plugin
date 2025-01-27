package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.mitigy.teamedpvp.TeamedPVP;

public class itemDropEventHandler implements Listener {
    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        ItemStack droppedItem = e.getItemDrop().getItemStack();

        if (!droppedItem.hasItemMeta()) return

        ItemMeta droppedMeta = droppedItem.getItemMeta();
        PersistentDataContainer container = droppedMeta.getPersistentDataContainer();

        NamespacedKey canDropKey = new NamespacedKey(TeamedPVP.getInstance(), "can-drop");
        if (container.has(canDropKey)) {
            if (container.get(canDropKey, PersistentDataType.STRING).equals("false")) {
                e.setCancelled(true);
            }
        }
    }
}
