package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import java.util.logging.Logger;

import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.customInventories.spectateOrPlayInv;
import com.mitigy.teamedpvp.modeControl.Mode;

public class inventoryClickEventHandler implements Listener {
    Logger log = Bukkit.getLogger();

    @EventHandler
    public void onInteract(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();

        ItemStack currentItem = e.getCurrentItem();
        if (!currentItem.hasItemMeta()) return; // Don't try to get meta if the item doesn't have it

        ItemMeta itemMeta = currentItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        NamespacedKey clickExecuteCommandKey = new NamespacedKey(TeamedPVP.getInstance(), "click-execute-command");
        if (container.has(clickExecuteCommandKey)) {
            String command = container.get(clickExecuteCommandKey, PersistentDataType.STRING);
            player.performCommand(command);
            log.info(player.getName() + " issued server command: /" + command);
        } // Handle the command to be executed on click

        NamespacedKey canClickKey = new NamespacedKey(TeamedPVP.getInstance(), "can-click");
        if (container.has(canClickKey)) {
            if (container.get(canClickKey, PersistentDataType.STRING).equals("false")) {
                e.setCancelled(true);
            }
        } // Prevent items which shouldn't be clicked from being clicked

        // Handle the 'Spectate or Play' inventory click
        if (e.getInventory().getHolder() instanceof spectateOrPlayInv) {
            e.setCancelled(true);
        }
    }
}
