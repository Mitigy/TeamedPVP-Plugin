package com.mitigy.teamedpvp.gameLogic;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.utils.Countdown;
import com.mitigy.teamedpvp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class Queue {

    private static Queue instance;
    public Queue() {
        instance = this;
    }
    public static Queue getInstance() { return instance; }

    private List<Player> queueList = new ArrayList<>();
    private BukkitTask gameStartCountdownTask;

    public List<Player> getQueueList() { return queueList; }
    public void clearGameStartCountDownTask() { gameStartCountdownTask = null; }

    public boolean addPlayerToQueue(Player player) {
        if (queueList.contains(player)) return false;

        queueList.add(player);

        if (gameStartCountdownTask == null && queueList.size() >= 1) {
            // Run game start countdown
            gameStartCountdownTask = new Countdown(10, TeamedPVP.getInstance()) {

                @Override
                public void count(int current, int initial) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.setLevel(current);
                        p.setExp(current / (float) initial);

                        // Only send the message on milestones
                        if (current != 0 && (current <= 5 || current == 10)) {
                            Utils.sendMsgPluginPrefix(p, "Time left: " + current);
                            if (current <= 3) {
                                p.playSound(p, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.RECORDS, 1.75F, 1.75F);
                            }
                        }
                    }

                    if (current == 0) {
                        Utils.sendMsgToAllOnlinePlayers("Time is up!", true);
                        GameControl.getInstance().prepareNewGame();
                        return;
                    }
                }

            }.start();
        }

        return true;
    }

    public boolean removePlayerFromQueue(Player player) {
        if (!queueList.contains(player)) return false;

        queueList.remove(player);
        return true;
    }

    public void clearQueue() { queueList.clear(); }
}
