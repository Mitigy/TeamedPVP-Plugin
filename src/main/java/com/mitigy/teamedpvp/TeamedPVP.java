package com.mitigy.teamedpvp;

import com.earth2me.essentials.Essentials;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import java.util.logging.Logger;

import com.mitigy.teamedpvp.commands.*;
import com.mitigy.teamedpvp.eventHandlers.*;
import com.mitigy.teamedpvp.gameLogic.GameControl;
import com.mitigy.teamedpvp.gameLogic.Queue;
import com.mitigy.teamedpvp.modeControl.Mode;
import com.mitigy.teamedpvp.utils.ScoreboardManager;
import com.mitigy.teamedpvp.utils.Utils;

public final class  TeamedPVP extends JavaPlugin {
    private static TeamedPVP instance;
    Logger log = Bukkit.getLogger();

    public TeamedPVP() {
        instance = this;
    }
    public static TeamedPVP getInstance() { return instance; }

    private LuckPerms luckPermsApi = null;
    private Essentials essentialsApi = null;
    private static Chat chat = null;
    public LuckPerms getLuckPermsApi() { return luckPermsApi; }
    public Essentials getEssentialsApi() { return essentialsApi; }
    public Chat getChat() { return chat; }

    @Override
    public void onEnable() {
        // Plugin startup logic
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsApi = provider.getProvider();
        }
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();

        essentialsApi = (Essentials) getServer().getPluginManager().getPlugin("Essentials");

        Bukkit.getWorld("world").setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        // Initialize the red/blue teams as minecraft teams
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (scoreboard.getTeam("red") == null) {
            Team redTeamVanilla = scoreboard.registerNewTeam("red");

            redTeamVanilla.setAllowFriendlyFire(false);
            redTeamVanilla.displayName(Component.text("RED", NamedTextColor.RED));
            redTeamVanilla.prefix(Component.text("[RED] ", NamedTextColor.RED));
            log.info("Red team initialization successful.");
        }else {
            log.info("Red team already exists.");
        }
        if (scoreboard.getTeam("blue") == null) {
            Team blueTeamVanilla = scoreboard.registerNewTeam("blue");

            blueTeamVanilla.setAllowFriendlyFire(false);
            blueTeamVanilla.displayName(Component.text("BLUE", NamedTextColor.BLUE));
            blueTeamVanilla.prefix(Component.text("[BLUE] ", NamedTextColor.BLUE));
            log.info("Blue team initialization successful.");
        }else {
            log.info("Blue team already exists.");
        }

        new Queue();
        new Mode();
        new GameControl();
        new ScoreboardManager();

        this.getCommand("ping").setExecutor(new pingCommand());
        this.getCommand("joinqueue").setExecutor(new joinQueueCommand());
        this.getCommand("setpersistentdata").setExecutor(new setpersistentdataCommand());
        this.getCommand("forcestart").setExecutor(new forcestartCommand());
        this.getCommand("pickmode").setExecutor(new pickmodeCommand());
        this.getCommand("spectatemode").setExecutor(new spectatemodeCommand());
        this.getCommand("playmode").setExecutor(new playmodeCommand());
        this.getCommand("stats").setExecutor(new statsCommand());
        this.getCommand("setkills").setExecutor(new setkillsCommand());
        this.getCommand("setdeaths").setExecutor(new setdeathsCommand());
        this.getCommand("setlevel").setExecutor(new setlevelCommand());
        this.getServer().getPluginManager().registerEvents(new cancelAdvancementCriterionGrant(), this);
        this.getServer().getPluginManager().registerEvents(new joinEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new executeCommandItems(), this);
        this.getServer().getPluginManager().registerEvents(new onRespawnEvent(), this);
        this.getServer().getPluginManager().registerEvents(new quitEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new closeInventoryEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new inventoryClickEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new itemDropEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new onHeldItemChangeEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new playerSwapHandItemsEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new onPlayerDeathEvent(), this);
        this.getServer().getPluginManager().registerEvents(new entityDamageEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new playerMoveEventHandler(), this);
        this.getServer().getPluginManager().registerEvents(new foodLevelChangeEventHandler(), this);

        Utils.sendMsgToAllOnlinePlayers("TeamedPVP plugin enabled successfully!", false);
        log.info("TeamedPVP plugin enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Utils.sendMsgToAllOnlinePlayers("TeamedPVP plugin disabled successfully!", false);
        log.info("TeamedPVP plugin disabled successfully!");
    }
}
