package com.mitigy.teamedpvp.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.utils.Utils;

public class setpersistentdataCommand implements CommandExecutor {
    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (Utils.sendNotPlayerErrorMsg(sender)) return false;
        if (Utils.sendPermissionError(sender, "teamedpvp.forcestart")) return false;
        if (args.length < 2) {
            Utils.coloredUsageText(sender, "/setpersistentdata <key> <data>");
            return false;
        }

        Player player = (Player) sender;

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();

        // Get the data's key
        NamespacedKey key = new NamespacedKey(TeamedPVP.getInstance(), args[0]);
        String[] newArgs = (String[]) ArrayUtils.remove(args, 0); // Remove the data's key

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        if (newArgs.length == 0) { // If there is no data then assume the key should be removed
            container.remove(key);
            return true;
        }

        String data = String.join(" ", newArgs);

        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);
        item.setItemMeta(itemMeta);

        // Give the player the modified item
        ((Player) sender).getPlayer().getInventory().setItem(player.getInventory().getHeldItemSlot(), item);
        return true;
    }
}
