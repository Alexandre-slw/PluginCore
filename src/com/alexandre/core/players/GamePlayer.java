package com.alexandre.core.players;

import com.alexandre.core.Main;
import com.alexandre.core.api.inventory.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GamePlayer {

    private final Player player;
    private final int roleID;
    private final ArrayList<String> permissions;
    
    private FastInv currentGui = null;

    private Scoreboard scoreboard = null;
    private Objective objective = null;
    private final ArrayList<Team> scoreboardTeams = new ArrayList<>();
    private static final ArrayList<String> scoreboardInvisibleChars = new ArrayList<>();

    static  {
        Collections.addAll(GamePlayer.scoreboardInvisibleChars, "🎈", "🎆", "🎇", "🧨", "✨", "🎉", "🎊", "🎃", "🎄", "🎋", "🎍", "🎎", "🎏", "🎐", "🎑", "🧧", "🎀", "🎁", "🎗", "🎞", "🎟", "🎫", "🎠", "🎡", "🎢", "🎪");
    }

    public GamePlayer(Player player) {
        this.player = player;
        this.roleID = 0;
        this.permissions = new ArrayList<>();
    }

    public GamePlayer(GamePlayer player) {
        this(player.getPlayer());
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getRoleID() {
        return this.roleID;
    }

    public ArrayList<String> getPermissions() {
        return this.permissions;
    }

    public String getDisplayName() {
        return this.getPlayer().getDisplayName();
    }

    public void open(FastInv gui) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            if (gui == null) {
                GamePlayer.this.player.closeInventory();
                GamePlayer.this.currentGui = null;
                return;
            }

            GamePlayer.this.currentGui = gui;
            gui.open(GamePlayer.this.getPlayer());
        }, 1);
    }

    public FastInv getGui() {
        return this.currentGui;
    }

    public void removeGui() {
        this.currentGui = null;
    }

    public void setScoreboard(String title, String... lines) {
        if (this.scoreboard == null) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            this.objective = scoreboard.registerNewObjective(title, "test");
            this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        this.objective.setDisplayName(title);

        int i = 0;
        for (String line : lines) {
            Team team;
            if (i >= this.scoreboardTeams.size()) {
                String name = null;
                for (int j = 0; j < scoreboardInvisibleChars.size(); j++) {
                    name = scoreboardInvisibleChars.get(i);
                    if (this.scoreboard.getTeam(name) == null) break;
                }
                team = this.scoreboard.registerNewTeam(name);
                this.scoreboardTeams.add(team);
                team.addEntry(name);
            } else {
                team = this.scoreboardTeams.get(i);
            }

            if (line.length() <= 16) {
                team.setPrefix(line);
                team.setSuffix("");
            } else {
                if (line.length() > 32) line = line.substring(0, 32);
                String pre = line.substring(0, 16);
                String suf = line.substring(16);
                if (pre.endsWith("§")) {
                    pre = pre.substring(0, pre.length() - 1);
                    suf = "§" + suf;
                    if (suf.length() > 16) suf = suf.substring(0, 16);
                }
                team.setPrefix(pre);
                team.setSuffix(suf);
            }

            this.objective.getScore(team.getName()).setScore(lines.length - i - 1);

            i++;
        }

        int index = i;
        while (i < this.scoreboardTeams.size()) {
            Team team = this.scoreboardTeams.remove(index);
            team.removeEntry(team.getName());
            i++;
        }

        this.getPlayer().setScoreboard(this.scoreboard);
    }

    public void removeScoreboard() {
        if (this.scoreboard == null) return;
        this.scoreboardTeams.clear();
        this.scoreboard = null;
        this.objective = null;
        this.getPlayer().setScoreboard(null);
    }

    public void setScoreboard(String title, List<String> lines) {
        this.setScoreboard(title, lines.toArray(new String[0]));
    }

}
