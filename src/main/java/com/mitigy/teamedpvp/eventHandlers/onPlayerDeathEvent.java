package com.mitigy.teamedpvp.eventHandlers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Scoreboard;
import java.util.HashMap;
import java.util.UUID;

import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.utils.Utils;

public class onPlayerDeathEvent implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player killed = e.getPlayer();
        Location killedLoc = killed.getLocation();
        UUID killedId = killed.getUniqueId();
        Player killer = killed.getKiller();
        GameControl gameControlInstance = GameControl.getInstance();

        Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        board.getPlayerTeam(killed).prefix();

        // If the killer is not a player then say the killed player died otherwise say that the killed player was slain by the killer player.
        if (killer == null) {
            e.deathMessage(
                    Component.text("").color(NamedTextColor.WHITE)
                    .append(board.getPlayerTeam(killed).prefix()) // e.g. [RED]
                    .append(Component.text(Utils.getPlayerNameWithModifiers(killed))) // e.g. [OWNER] Steve [0*]
                    .append(Component.text(" died."))
            );
        }else {
            e.deathMessage(
                    Component.text("").color(NamedTextColor.WHITE)
                    .append(board.getPlayerTeam(killed).prefix()) // e.g. [RED]
                    .append(Component.text(Utils.getPlayerNameWithModifiers(killed))) // e.g. [OWNER] Steve [0*]
                    .append(Component.text(" was slain by "))
                    .append(board.getPlayerTeam(killer).prefix()) // e.g [BLUE]
                    .append(Component.text(Utils.getPlayerNameWithModifiers(killer))) // e.g. Steve [0*]
                    .append(Component.text("."))
            );
        }

        if (killer != null) {
            HashMap<UUID, Integer> killsHashMap = gameControlInstance.getKillsHashMap();
            killsHashMap.replace(killer.getUniqueId(), killsHashMap.get(killer.getUniqueId()) + 1);
        }

        HashMap<UUID, Integer> deathsHashMap = gameControlInstance.getDeathsHashMap();
        deathsHashMap.replace(killed.getUniqueId(), deathsHashMap.get(killed.getUniqueId()) + 1);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId() != killedId && player.getLocation().distance(killedLoc) <= 16) {
                player.playSound(killedLoc, Sound.ENTITY_PLAYER_BREATH, 1F, 2F);
            }
        }

    }
}
