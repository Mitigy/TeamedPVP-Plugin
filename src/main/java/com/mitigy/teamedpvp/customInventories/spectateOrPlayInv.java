package com.mitigy.teamedpvp.customInventories;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.w3c.dom.Text;
import java.util.Collections;

import com.mitigy.teamedpvp.TeamedPVP;

public class spectateOrPlayInv implements InventoryHolder {
    @Override
    public Inventory getInventory() {
        NamespacedKey key = new NamespacedKey(TeamedPVP.getInstance(), "click-execute-command");
        final TextComponent textComponent = Component.text("Spectate or Play?", NamedTextColor.DARK_AQUA);

        Inventory inventory = Bukkit.createInventory(this, 9, textComponent);

        ItemStack spectate = new ItemStack(Material.ENDER_EYE);
        ItemMeta spectateMeta = spectate.getItemMeta();
        spectateMeta.displayName(Component.text("Spectate", NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));


        spectateMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "spectatemode");
        spectate.setItemMeta(spectateMeta);

        ItemStack play = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta playMeta = play.getItemMeta();
        playMeta.displayName(Component.text("Play", NamedTextColor.DARK_AQUA)
            .decorate(TextDecoration.BOLD)
            .decoration(TextDecoration.ITALIC, false));
        playMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        playMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "playmode");
        play.setItemMeta(playMeta);

        inventory.setItem(2, spectate);
        inventory.setItem(6, play);

        // Fills the empty slots with black stained glass panes
        ItemStack emptySpace = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta emptySpaceMeta = emptySpace.getItemMeta();
        emptySpaceMeta.displayName(Component.text(""));
        emptySpace.setItemMeta(emptySpaceMeta);

        for(int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, emptySpace);
            }
        }

        return inventory;
    }
}
