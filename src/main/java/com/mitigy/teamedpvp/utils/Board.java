package com.mitigy.teamedpvp.utils;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;

public class Board {
    private final Scoreboard scoreboard;
    private final Objective objective;

    public Board(@NotNull String name, @NotNull Component displayName) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective(name, "dummy", displayName);

        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Component getTitle() {
        return objective.displayName();
    }

    public void setTitle(@NotNull Component displayName) {
        this.objective.displayName(displayName);
    }

    public void set(@NotNull int row, @NotNull String text) {
        Validate.isTrue(16 > row, "Row can't be higher than 16");
        if(text.length() > 32) { text = text.substring(0, 32); }

        for(String entry : this.scoreboard.getEntries()) {
            if(this.objective.getScore(entry).getScore() == row) {
                this.scoreboard.resetScores(entry);
                break;
            }
        }

        this.objective.getScore(text).setScore(row);
    }

    public void remove(int row) {
        for(String entry : this.scoreboard.getEntries()) {
            if(this.objective.getScore(entry).getScore() == row) {
                this.scoreboard.resetScores(entry);
                break;
            }
        }
    }
}