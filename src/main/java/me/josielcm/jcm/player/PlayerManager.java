package me.josielcm.jcm.player;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import lombok.Getter;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.Arena;
import me.josielcm.jcm.game.GameState;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;

public class PlayerManager {

    @Getter
    private static HashMap<UUID, TeamType> players = new HashMap<>();

    public static void onJoin(Player player) {
        if (JBuildBattle.getInstance().getGameManager().getGameState() == GameState.PLAYING) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }

        if (player.hasPermission("jbuildbattle.team.pro")) {
            players.put(player.getUniqueId(), TeamType.PROS);
            player.teleport(Arena.getSpawnPros());
        } else {
            players.put(player.getUniqueId(), TeamType.NOOBS);
            player.teleport(Arena.getSpawnNoobs());
        }

        TeamManager.addPlayerToTeam(player, players.get(player.getUniqueId()));
    }

    public static void sendTitle(String titlemsg, String subtitle, int fadeIn, int stay, int fadeOut) {
        Times times = Times.times(Duration.ofSeconds(fadeOut),
                Duration.ofSeconds(stay),
                Duration.ofSeconds(fadeIn));

        Title title = Title.title(
            Color.parse(titlemsg),
            Color.parse(subtitle),
            times
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(title);
        }
    }

    public static void playSound(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void onLeave(Player player) {
        if (players.containsKey(player.getUniqueId())) {
            players.remove(player.getUniqueId());
            TeamManager.removePlayerFromTeam(player);
        }
    }

    public static TeamType getTeam(Player player) {
        return players.get(player.getUniqueId());
    }

}
