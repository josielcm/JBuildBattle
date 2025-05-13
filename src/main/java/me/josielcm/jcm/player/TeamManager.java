package me.josielcm.jcm.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import me.josielcm.jcm.api.formats.Color;
import net.kyori.adventure.text.format.NamedTextColor;

public class TeamManager {
    
    @Getter
    private static Team prosTeam;

    @Getter
    private static Team noobsTeam;

    public TeamManager() {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getMainScoreboard();
    
        if (scoreboard == null) {
            scoreboard = scoreboardManager.getNewScoreboard();
        }
    
        if (scoreboard.getTeam("pros") != null) {
            scoreboard.getTeam("pros").unregister();
        }
        if (scoreboard.getTeam("noobs") != null) {
            scoreboard.getTeam("noobs").unregister();
        }
    
        prosTeam = scoreboard.registerNewTeam("pros");
        noobsTeam = scoreboard.registerNewTeam("noobs");
    
        prosTeam.color(NamedTextColor.BLUE);
        prosTeam.prefix(Color.parse("<blue>"));
        prosTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    
        noobsTeam.color(NamedTextColor.RED);
        noobsTeam.prefix(Color.parse("<red>"));
        noobsTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    }

    public static void addPlayerToTeam(Player player, TeamType teamType) {
        Team team = null;

        if (teamType == TeamType.PROS) {
            team = prosTeam;
        } else if (teamType == TeamType.NOOBS) {
            team = noobsTeam;
        }

        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    public static void removePlayerFromTeam(Player player) {
        if (prosTeam.hasEntry(player.getName())) {
            prosTeam.removeEntry(player.getName());
        } else if (noobsTeam.hasEntry(player.getName())) {
            noobsTeam.removeEntry(player.getName());
        }
    }

}
