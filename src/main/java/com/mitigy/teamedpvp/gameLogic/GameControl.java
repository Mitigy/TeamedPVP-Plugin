package com.mitigy.teamedpvp.gameLogic;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.mitigy.teamedpvp.utils.FileUtility;
import com.mitigy.teamedpvp.TeamedPVP;
import com.mitigy.teamedpvp.modeControl.Mode;
import com.mitigy.teamedpvp.utils.Countdown;
import com.mitigy.teamedpvp.utils.Utils;

public class GameControl {
    private static GameControl instance;
    public GameControl() { instance = this; }
    public static GameControl getInstance() { return instance; }

    Logger log = Bukkit.getLogger();

    private HashMap<UUID, Integer> killsHashMap = new HashMap<>();
    private HashMap<UUID, Integer> deathsHashMap = new HashMap<>();

    private Location redTeamSpawn = new Location(Bukkit.getWorld("world"), 0.5, 1, -66.5, 0, 0);
    private Location blueTeamSpawn = new Location(Bukkit.getWorld("world"), 0.5, 1, 67.5, 180, 0);

    private List<Player> redTeamList = new ArrayList<>();
    private List<Player> blueTeamList = new ArrayList<>();

    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
    Team redTeamVanilla = scoreboard.getTeam("red");
    Team blueTeamVanilla = scoreboard.getTeam("blue");

    private Integer gameState = 0;
    private Queue queueInstance = Queue.getInstance();

    public HashMap<UUID, Integer> getKillsHashMap() { return killsHashMap; }
    public HashMap<UUID, Integer> getDeathsHashMap() { return deathsHashMap; }
    public Location getRedTeamSpawn() { return redTeamSpawn; }
    public Location getBlueTeamSpawn() { return blueTeamSpawn; }
    public List<Player> getRedTeamList() { return redTeamList; }
    public List<Player> getBlueTeamList() { return blueTeamList; }
    public Integer getGameState() { return gameState; }

    public void prepareNewGame() {
        List<Player> queueList = queueInstance.getQueueList();
        
        if (queueList.size() < 2) {
            Utils.sendMsgToAllOnlinePlayers(Utils.replaceColorCodes("&c2 or more players are required to start the game!"), true);
            return;
        }

        // START: Split the queue into red team and blue team while biasing red team with the extra player.
        redTeamList.clear();
        blueTeamList.clear();

        Collections.shuffle(queueList, new Random(System.currentTimeMillis()));
        int half = queueList.size() / 2;
        int i = 0;
        for (; i < half; i++) {
            blueTeamList.add(queueList.get(i));
        }
        for (; i < queueList.size(); i++) {
            redTeamList.add(queueList.get(i));
        }

        // Put players on the correct scoreboard team based on their listed team
        redTeamVanilla.removeEntries(redTeamVanilla.getEntries()); // Remove all red team players
        blueTeamVanilla.removeEntries(blueTeamVanilla.getEntries()); // Remove all blue team players

        Collection<String> redTeamNames = redTeamList.stream().map((player) -> player.getName()).collect(Collectors.toList()); // Create a list of red team player's names
        Collection<String> blueTeamNames = blueTeamList.stream().map((player) -> player.getName()).collect(Collectors.toList());// Create a list of blue team player's names
        redTeamVanilla.addEntries(redTeamNames); // Add red team players to the red team vanilla
        blueTeamVanilla.addEntries(blueTeamNames);// Add blue team players to the blue team vanilla

        Mode modeInstance = Mode.getInstance();
        // Put all players into the correct mode for each of them
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!modeInstance.getPlayersMode(player.getUniqueId()).equals(-2)) {
                player.getInventory().clear();

                if (queueList.contains(player)) {
                    player.setFallDistance(0F);

                    modeInstance.playMode(player);
                    killsHashMap.put(player.getUniqueId(), 0);
                }
                else modeInstance.spectateMode(player);
            }
        }

        // Remove all non-player entities
        World world = Bukkit.getWorld("world");
        for(Entity entity : world.getEntities()){
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }

        // Make there as being a game occurring
        gameState = 1;
        Utils.updateAllPlayersScoreboardTeamSizes();

        redTeamList.forEach((player) -> player.teleport(redTeamSpawn));
        blueTeamList.forEach((player) -> player.teleport(blueTeamSpawn));
    }

    public void movePlayerToTeam(Player player, String teamName, Boolean isRespawn, Boolean triggerGameEnd) {
        if (teamName.equals("")) {
            redTeamList.remove(player);
            blueTeamList.remove(player);

            redTeamVanilla.removeEntry(player.getName());
            blueTeamVanilla.removeEntry(player.getName());

            if (!triggerGameEnd) return;
        }

        if (teamName.equals("red")) {
            redTeamList.add(player);
            blueTeamList.remove(player);

            redTeamVanilla.addEntry(player.getName());
            blueTeamVanilla.removeEntry(player.getName());

            if (!isRespawn) {
                player.teleport(redTeamSpawn);
            }
        }else if (teamName.equals("blue")) {
            blueTeamList.add(player);
            redTeamList.remove(player);

            blueTeamVanilla.addEntry(player.getName());
            redTeamVanilla.removeEntry(player.getName());

            if (!isRespawn) {
                player.teleport(blueTeamSpawn);
            }
        }

        if ((redTeamList.size() == 0 && blueTeamList.size() != 0) || (blueTeamList.size() == 0 && redTeamList.size() != 0)) {
            runGameOverSequence();
        }
    }

    public void putNewPlayerOnTeam(Player player) {
        if (redTeamList.size() <= blueTeamList.size()) {
            movePlayerToTeam(player, "red", false, false);
        }else {
            movePlayerToTeam(player, "blue", false, false);
        }
    }

    public void runGameOverSequence() {
        // Setup all the values that will be used repeatedly later.
        List<Map.Entry<UUID, Integer>> killsList = new ArrayList<>(killsHashMap.entrySet());
        killsList.sort((entry1, entry2) -> entry2.getValue() - entry1.getValue());


        HashMap<Player, Integer[]> lvlUpMap = new HashMap<>();

        // Do the saving of each player's kills/deaths in parallel.
        killsHashMap.entrySet().parallelStream().forEach((e) -> {
            Player player = Bukkit.getPlayer(e.getKey());
            UUID playerId = player.getUniqueId();

            // If the player had no kills or deaths there is no point in saving the config again.
            if (e.getValue() == 0 && deathsHashMap.get(playerId) == 0) return;

            FileUtility fileUtility = new FileUtility(player);
            FileConfiguration config = fileUtility.getConfig(true);

            // Some fun magic to reduce the number of file writes
            if (e.getValue() != 0) {
                Integer newKills = config.getInt("kills") + e.getValue();

                Integer newLvl = (int) Math.floor(Math.sqrt(newKills));
                Integer oldLvl = config.getInt("level");
                if (newLvl > oldLvl) {
                    lvlUpMap.put(player, new Integer[]{oldLvl, newLvl});
                    Utils.updatePlayerLevelSuffix(player, newLvl);
                    fileUtility.set("level", newLvl, false);
                }

                if (deathsHashMap.get(playerId) == 0) {
                    fileUtility.set("kills", newKills, true);
                }else {
                    fileUtility.set("kills", newKills, false);
                }

            }

            // Some more (opposite) fun magic to reduce the number of file writes
            if (deathsHashMap.get(playerId) != 0) {
                Integer newDeaths = config.getInt("deaths") + deathsHashMap.get(playerId);
                if (e.getValue() == 0) {
                    fileUtility.set("deaths", newDeaths, true);
                }else {
                    fileUtility.set("deaths", newDeaths, false);
                }
            }

            log.info("Saved kills/deaths data for " + player.getName());
        });

        Title gameOverTitle = Title.title(Component.text("Game Over", NamedTextColor.RED), Component.text(""));
        String horizontalLine = Utils.replaceColorCodes("&a&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        String nameLine = "&f&lPVP Arena";

        String[] firstData = returnKillsRankingData(killsList, 0);
        String firstKiller = String.format("&e&l1st Killer &7- %s&7 - %s", firstData[0], firstData[1]);

        String[] secondData = returnKillsRankingData(killsList, 1);
        String secondKiller = String.format("&6&l2nd Killer &7- %s&7 - %s", secondData[0], secondData[1]);

        String[] thirdData = returnKillsRankingData(killsList, 2);
        String thirdKiller = String.format("&c&l3rd Killer &7- %s&7 - %s", thirdData[0], thirdData[1]);

        // Clear out all the queues/lists/teams for a brand-new game
        Queue queueInstance = Queue.getInstance();
        queueInstance.clearGameStartCountDownTask();
        queueInstance.clearQueue();
        redTeamList.clear();
        blueTeamList.clear();
        redTeamVanilla.removeEntries(redTeamVanilla.getEntries());
        blueTeamVanilla.removeEntries(blueTeamVanilla.getEntries());

        gameState = -1;

        Utils.sendMsgToAllOnlinePlayers("Game Over!", true);

        Mode modeInstance = Mode.getInstance();
        // Put all players (not in super spectate mode) into spectate mode
        Bukkit.getOnlinePlayers().parallelStream().forEach((player) -> {
            if (!modeInstance.getPlayersMode(player.getUniqueId()).equals(-2)) {
                player.getInventory().clear();
                player.setTotalExperience(0);
                movePlayerToTeam(player, "", false, false);

                player.showTitle(gameOverTitle);

                player.sendMessage(
                    horizontalLine,
                    Utils.createCenteredMsg(nameLine),
                    "\n" + Utils.createCenteredMsg(firstKiller),
                    Utils.createCenteredMsg(secondKiller),
                    Utils.createCenteredMsg(thirdKiller),
                    "\n" + horizontalLine
                );

                if (lvlUpMap.containsKey(player)) {
                    Integer[] lvls = lvlUpMap.get(player);

                    player.sendMessage(
                        Utils.createCenteredMsg(Utils.replaceColorCodes("&e&lLevel Up!")),
                        "\n" + Utils.createCenteredMsg(Utils.replaceColorCodes("&7&l&m" + lvls[0] + "✫&3&l \u2192 &e&l" + lvls[1] + "✫")),
                        "\n" + horizontalLine
                    );
                }
            }
        });

        Bukkit.getOnlinePlayers().forEach((player) -> modeInstance.spectateMode(player, false));

        // Remove all non-player entities
        World world = Bukkit.getWorld("world");
        for(Entity entity : world.getEntities()){
            if (!(entity instanceof Player)) {
                entity.remove();
            }
        }

        new Countdown(10, TeamedPVP.getInstance()) {
            @Override
            public void count(int current, int initial) {
                if (current == 10 || current == 5 || current == 3) {
                    Utils.sendMsgToAllOnlinePlayers("Queuing for a new game will begin in " + current + " seconds!", true);
                }else if (current == 0) {
                    Mode modeInstance = Mode.getInstance();
                    Bukkit.getOnlinePlayers().forEach((player) -> {
                        modeInstance.prePlayMode(player);
                    });
                    gameState = 0;
                }
            }
        }.start();
    }

    private static String[] returnKillsRankingData(List<Map.Entry<UUID, Integer>> list, Integer index) {
        if (list.size() > index) {
            Map.Entry<UUID, Integer> entry = list.get(index);

            return new String[]{Utils.getPlayerNameWithModifiers(Bukkit.getPlayer(entry.getKey())), entry.getValue().toString()};
        }else {
            return new String[]{"N/A", "N/A"};
        }
    }
}
