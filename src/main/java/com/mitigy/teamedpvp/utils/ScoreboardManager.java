package com.mitigy.teamedpvp.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;

public class ScoreboardManager {
    private static ScoreboardManager instance;
    public ScoreboardManager() { instance = this; }
    public static ScoreboardManager getInstance() { return instance; }

    private HashMap<Player, Board> boardMap = new HashMap<>();
    public HashMap<Player, Board> getWrapperMap() { return boardMap; }

    public Board createBoard(@NotNull Player player, @NotNull String title, @NotNull Component displayName) {
        Board board = new Board(title, displayName);
        boardMap.put(player, board);
        return board;
    }

    public void showBoard(@NotNull Player player, @NotNull Board board) {
        player.setScoreboard(board.getScoreboard());
    }

    public Board getBoardByPlayer(@NotNull Player player) {
        return boardMap.get(player);
    }
}
