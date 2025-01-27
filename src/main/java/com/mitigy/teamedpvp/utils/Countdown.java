package com.mitigy.teamedpvp.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class Countdown {
    private int time;
    private int initial;

    protected BukkitTask task;
    protected final Plugin plugin;

    public Countdown(int time, Plugin plugin) {
        this.time = time;
        this.initial = time;
        this.plugin = plugin;
    }

    public abstract void count(int current, int initial);

    public final BukkitTask start() {
        task = new BukkitRunnable() {

            @Override
            public void run() {
                count(time, initial);
                if (time-- <= 0) cancel();
            }

        }.runTaskTimer(plugin, 0L, 20L);
        return task;
    }

}
