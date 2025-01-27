package com.mitigy.teamedpvp.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.mitigy.teamedpvp.TeamedPVP;

public class FileUtility {
    Logger log = Bukkit.getLogger();
    File file = null;
    FileConfiguration configuration = null;
    Boolean createNewFile = null;
    OfflinePlayer player = null;

    /**
     * @param player reference of data
     */
    public FileUtility(OfflinePlayer player) {
        this.player = player;
    }

    /**
     * get the config from file
     * @return file configuration
     */
    public FileConfiguration getConfig(@NotNull Boolean createNewFile) {
        if(configuration == null) {
            File file = getFile(createNewFile);
            if (file == null) return null;
            configuration = YamlConfiguration.loadConfiguration(file);
            return configuration;
        }
        return configuration;
    }

    /**
     * this will save the config to the file
     */
    public void saveConfig() {
        try {
            configuration.save(file);
            log.info("Saved change for " + file.getName());
        } catch (IOException e) {
            log.info("Cannot save to " + file.getName());
        }
    }

    /**
     * set an object to a certain path
     * @param path the path
     * @param object the object to set
     */
    public void set(String path, Object object, Boolean save) {
        FileConfiguration config = getConfig(false);
        config.set(path, object);
        if (save) {
            saveConfig();
        }
    }

    /**
     * this will get the player's file
     * @return the player's uuid file
     */
    public File getFile(@NotNull Boolean createNewFile) {
        if(this.file == null) {
            File dataFolder = TeamedPVP.getInstance().getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdir();

                log.info("The plugin's data folder was created successfully!");
            }

            this.file = new File(TeamedPVP.getInstance().getDataFolder(), player.getUniqueId() + ".yml");

            // If the file is there then return it
            if (this.file.exists()) return file;

            // If we don't want to create a new file and the file doesn't exist then return null
            if (!createNewFile && !this.file.exists()) {
                return null;
            }

            if(!this.file.exists()) {
                try {
                    if(this.file.createNewFile()) {
                        log.info("File for player " + player.getName() + " has been created");
                        log.info("Saved as " + player.getUniqueId() + ".yml");

                        set("kills", 0, false);
                        set("deaths", 0, false);
                        set("level", 0, true);

                        log.info("Saved kills:0 for " + player.getName());
                        log.info("Saved deaths:0 for " + player.getName());
                        log.info("Saved level:0 for " + player.getName());
                    }
                }catch (IOException e) {
                    log.severe("Cannot create data for " + player.getName());
                }
            }
            return file;
        }
        return file;
    }

    /**
     * this will reload the config
     */
    public void reloadConfig() {
        YamlConfiguration.loadConfiguration(file);
    }
}