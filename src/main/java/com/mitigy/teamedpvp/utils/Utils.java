package com.mitigy.teamedpvp.utils;

import com.earth2me.essentials.Essentials;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.stream.Collectors;

import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.modeControl.Mode;
import com.mitigy.teamedpvp.gameLogic.Queue;

public final class Utils {
    public static boolean arrayContainsValue(@NotNull String[] arry, @NotNull String toCheckFor) {
        return Arrays.stream(arry).anyMatch(toCheckFor::equals);
    }

    public static void coloredUsageText(@NotNull Player player, @NotNull String inputText) {
        player.sendMessage(ChatColor.RED + "Correct Usage: " + inputText);
    }

    public static void coloredUsageText(@NotNull CommandSender sender, @NotNull String inputText) {
        sender.sendMessage(ChatColor.RED + "Correct Usage: " + inputText);
    }

    public static String replaceColorCodes(@NotNull String inputStr) {
        return ChatColor.translateAlternateColorCodes('&', inputStr);
    }

    public static void sendMsgPluginPrefix(@NotNull CommandSender sender, @NotNull String msg) {
        sender.sendMessage(replaceColorCodes("&7[&6TeamedPVP&7] &r") + msg);
    }

    public static void sendMsgPluginPrefix(@NotNull Player player, @NotNull String msg) {
        player.sendMessage(replaceColorCodes("&7[&6TeamedPVP&7] &r") + msg);
    }

    public static void sendMsgToAllOnlinePlayers(@NotNull String msg, @NotNull Boolean addPluginPrefix) {
        Bukkit.getOnlinePlayers().parallelStream().forEach((@NotNull Player player) -> {
            if (addPluginPrefix) {
                sendMsgPluginPrefix(player, msg);
            }else {
                player.sendMessage(msg);
            }
        });
    }

    public static boolean sendNotPlayerErrorMsg(@NotNull CommandSender sender) {
        if (!(sender instanceof Player)) { // Check if the sender was a player
            sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
            return true;
        }

        return false;
    }

    public static boolean sendCommandCannotBeUsedNow(@NotNull CommandSender sender, @NotNull Boolean check) {
        if (!check) {
            sendMsgPluginPrefix(sender, ChatColor.RED + "That command cannot be used right now!");
            return true;
        }

        return false;
    }

    public static boolean sendPermissionError(@NotNull CommandSender sender, @NotNull String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
            return true;
        }

        return false;
    }

    public static boolean sendCommandInGameplayErrorMsg(@NotNull Player player) {
        if (Mode.getInstance().getPlayersMode(player.getUniqueId()).equals(1)) {
            sendMsgPluginPrefix(player, ChatColor.RED + "You cannot use that command during gameplay!");
            return true;
        }

        return false;
    }

    public static HashMap<UUID, Integer> setValueInHashMap(@NotNull HashMap<UUID, Integer> hashMap, @NotNull UUID key, @NotNull Integer value) {
        if (!hashMap.containsKey(key)) {
            hashMap.put(key, value);
        }else {
            hashMap.replace(key, value);
        }

        return hashMap;
    }

    private final static int CENTER_PX = 154;

    public static void sendCenteredMessage(@NotNull Player player, @NotNull String message){
        player.sendMessage(createCenteredMsg(message));
    }

    public static String createCenteredMsg(@NotNull String message) {
        message = Utils.replaceColorCodes(message);
        return createCenteringSpaces(message) + message;
    }

    public static String createCenteringSpaces(@NotNull String message) {
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for(char c : message.toCharArray()){
            if(c == '§'){
                previousCode = true;
                continue;
            }else if(previousCode == true){
                previousCode = false;
                if(c == 'l' || c == 'L'){
                    isBold = true;
                    continue;
                }else isBold = false;
            }else{
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb.toString();
    }

    public static String getPlayerNameWithModifiers(@NotNull Player player) {
        TeamedPVP instance = TeamedPVP.getInstance();
        LuckPerms luckPerms = instance.getLuckPermsApi();
        Essentials essentials = instance.getEssentialsApi();

        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        CachedMetaData metaData = user.getCachedData().getMetaData();

        SortedMap<Integer, String> prefixesMap = metaData.getPrefixes();
        String prefixes = prefixesMap.keySet().stream().map(key -> prefixesMap.get(key)).collect(Collectors.joining(""));

        SortedMap<Integer, String> suffixesMap = metaData.getSuffixes();
        String suffixes = suffixesMap.keySet().stream().map(key -> suffixesMap.get(key)).collect(Collectors.joining(""));

        return Utils.replaceColorCodes((prefixes == null ? "" : prefixes) +  essentials.getUser(player).getDisplayName() + (suffixes == null ? "" : suffixes));
    }

    public static void updatePlayerLevelSuffix(@NotNull Player player, @NotNull Integer lvl) {
        TeamedPVP.getInstance().getChat().setPlayerSuffix(player, String.format(Utils.replaceColorCodes(" &e[%d✫]"), lvl));
    }

    public static void updateScoreboardTeamSizes(@NotNull Player player) {
        ScoreboardManager managerInstance = ScoreboardManager.getInstance();
        Board board = managerInstance.getBoardByPlayer(player);

        GameControl gameControlInstance = GameControl.getInstance();
        Queue queueInstance = Queue.getInstance();

        board.set(3, Utils.replaceColorCodes("&cRed: " + gameControlInstance.getRedTeamList().size() + "/" + queueInstance.getQueueList().size()));
        board.set(2, Utils.replaceColorCodes("&9Blue: " + gameControlInstance.getBlueTeamList().size() + "/" + queueInstance.getQueueList().size()));
    }

    public static void updateAllPlayersScoreboardTeamSizes() {
        Bukkit.getOnlinePlayers().parallelStream().forEach((@NotNull Player player) -> updateScoreboardTeamSizes(player));
    }

    public static void givePlayerKitAndFillFood(@NotNull Player player) {
        //<editor-fold desc="Give player a simple PVP kit">
        PlayerInventory inventory = player.getInventory();

        ItemStack DIAMOND_SWORD = new ItemStack(Material.DIAMOND_SWORD);
        ItemStack IRON_HELMET = new ItemStack(Material.IRON_HELMET);
        ItemStack IRON_CHESTPLATE = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack IRON_LEGGINGS = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack IRON_BOOTS = new ItemStack(Material.IRON_BOOTS);

        // Add protection 2 to all the armor.
        IRON_HELMET.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        IRON_CHESTPLATE.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        IRON_LEGGINGS.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        IRON_BOOTS.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

        inventory.setItem(0, DIAMOND_SWORD);
        inventory.setItem(1, new ItemStack(Material.COOKED_BEEF).asQuantity(64));
        inventory.setItem(40 /* Offhand */, new ItemStack(Material.SHIELD));
        // Armor head to toe
        inventory.setItem(39, IRON_HELMET);
        inventory.setItem(38, IRON_CHESTPLATE);
        inventory.setItem(37, IRON_LEGGINGS);
        inventory.setItem(36, IRON_BOOTS);

        inventory.setHeldItemSlot(0);
        //</editor-fold>

        // Fill food bars
        player.setSaturation(20);
        player.setFoodLevel(20);
    }
}
