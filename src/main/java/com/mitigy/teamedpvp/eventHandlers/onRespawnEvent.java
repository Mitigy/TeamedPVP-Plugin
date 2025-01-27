package com.mitigy.teamedpvp.eventHandlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.gameLogic.Queue;
import com.mitigy.teamedpvp.utils.Utils;

public class onRespawnEvent implements Listener {
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        GameControl instance = GameControl.getInstance();
        if (instance.getRedTeamList().contains(player)) {
            sendTeamMoveMsg(player, ChatColor.RED + "red", ChatColor.BLUE + "blue");
            Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
                // code
                instance.movePlayerToTeam(player, "blue", true, true);
                Utils.updateAllPlayersScoreboardTeamSizes();
                Utils.givePlayerKitAndFillFood(player);
            }, 1L);

            e.setRespawnLocation(instance.getBlueTeamSpawn());
        }else if (instance.getBlueTeamList().contains(player)) {
            sendTeamMoveMsg(player, ChatColor.BLUE + "blue", ChatColor.RED + "red");
            Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
                // code
                instance.movePlayerToTeam(player, "red", true, true);
                Utils.updateAllPlayersScoreboardTeamSizes();
                Utils.givePlayerKitAndFillFood(player);
            }, 1L);

            e.setRespawnLocation(instance.getRedTeamSpawn());
        }

        Bukkit.getScheduler().runTaskLater(TeamedPVP.getInstance(), () -> {
            player.playSound(player, Sound.ENTITY_PLAYER_BREATH, 1F, 2F);
        }, 1L);
    }

    private void sendTeamMoveMsg(@NotNull Player player, @NotNull String startTeam, @NotNull String endTeam) {
        Utils.sendMsgPluginPrefix(player,
            String.format("You have been moved from %s" + ChatColor.RESET + " team to %s" + ChatColor.RESET + " team.", startTeam, endTeam)
        );
    }
}
