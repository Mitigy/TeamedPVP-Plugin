package com.mitigy.teamedpvp.modeControl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import java.util.*;

import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.utils.Board;
import com.mitigy.teamedpvp.utils.ScoreboardManager;
import com.mitigy.teamedpvp.utils.Utils;

public class Mode {
    private static Mode instance;
    private ItemStack JoinQueueEmerald;

    public Mode() {
        instance = this;

        //<editor-fold desc="Create the Join Queue Emerald">
        JoinQueueEmerald = new ItemStack(Material.EMERALD);
        ItemMeta joinQueueMeta = JoinQueueEmerald.getItemMeta();
        joinQueueMeta.displayName(Component.text("Join Queue!", NamedTextColor.GREEN)
                .decorations(Collections.singleton(TextDecoration.ITALIC), false));
        joinQueueMeta.lore(Arrays.asList(
                Component.text("RIGHT CLICK", NamedTextColor.YELLOW)
                        .decorate(TextDecoration.BOLD)
                        .decoration(TextDecoration.ITALIC, false)
                        .append(Component.text(" to join the queue.", NamedTextColor.GRAY)
                                .decorations(new HashSet<>(Arrays.asList(TextDecoration.ITALIC, TextDecoration.BOLD)), false)
                        )
        ));

        PersistentDataContainer container = joinQueueMeta.getPersistentDataContainer();

        NamespacedKey executeCommandKey = new NamespacedKey(TeamedPVP.getInstance(), "interact-execute-command");
        container.set(executeCommandKey, PersistentDataType.STRING, "joinqueue");

        NamespacedKey canInteractKey = new NamespacedKey(TeamedPVP.getInstance(), "can-interact");
        container.set(canInteractKey, PersistentDataType.STRING, "false");

        NamespacedKey canClickKey = new NamespacedKey(TeamedPVP.getInstance(), "can-click");
        container.set(canClickKey, PersistentDataType.STRING, "false");

        NamespacedKey canDropKey = new NamespacedKey(TeamedPVP.getInstance(), "can-drop");
        container.set(canDropKey, PersistentDataType.STRING, "false");

        JoinQueueEmerald.setItemMeta(joinQueueMeta);
        //</editor-fold>
    }
    public static Mode getInstance() { return instance; }

    private HashMap<UUID, Integer> playerMode = new HashMap<>();

    public Integer getPlayersMode(UUID uuid) {
        return playerMode.get(uuid);
    }

    public void spectateMode(@NotNull Player player) {
        spectateMode(player, true);
    }

    public void spectateMode(@NotNull Player player, @NotNull Boolean sendPickModeMsg) {
        Utils.setValueInHashMap(playerMode, player.getUniqueId(), 0);
        if (sendPickModeMsg) {
            Utils.sendMsgPluginPrefix(player, Utils.replaceColorCodes("Use &3/pickmode&r to open the mode selector again."));
        }

        // Wait 1 tick to clear the player's inv because if they died and the game ended then the clear won't work.
        Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
            player.getInventory().clear();
        }, 1L);
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void prePlayMode(@NotNull Player player) {
        Utils.setValueInHashMap(playerMode, player.getUniqueId(), -1);

        // Set the player's board to show that the game is waiting for player
        Board board = ScoreboardManager.getInstance().getBoardByPlayer(player);
        board.remove(3);
        board.set(2, Utils.replaceColorCodes("&fWaiting…"));

        PlayerInventory inventory = player.getInventory();

        inventory.setItem(4, JoinQueueEmerald);

        Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
            // code
            inventory.setHeldItemSlot(4);
        }, 1L);

        Boolean isFlying = player.isFlying();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(isFlying);
    }

    public void playMode(@NotNull Player player) { playMode(player, false); }

    public void playMode(@NotNull Player player, @NotNull Boolean putPlayerOnTeam) {
        GameControl gameControlInstance = GameControl.getInstance();
        UUID playerID = player.getUniqueId();

        HashMap<UUID, Integer> killsHashMap = gameControlInstance.getKillsHashMap();
        if (!killsHashMap.containsKey(playerID)) {
            killsHashMap.put(playerID, 0);
        }

        HashMap<UUID, Integer> deathsHashMap = gameControlInstance.getDeathsHashMap();
        if (!deathsHashMap.containsKey(playerID)) {
            deathsHashMap.put(playerID, 0);
        }

        Utils.setValueInHashMap(playerMode, player.getUniqueId(), 1);
        if (putPlayerOnTeam) {
            GameControl.getInstance().putNewPlayerOnTeam(player);
        }
        Utils.givePlayerKitAndFillFood(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(false);
    }
}
