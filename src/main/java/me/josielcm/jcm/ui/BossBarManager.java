package me.josielcm.jcm.ui;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class BossBarManager {

    @Getter
    @Setter
    private static BossBar bossBar;

    public BossBarManager() {
        bossBar = BossBar.bossBar(
                Color.parse("<yellow>Esperando..."),
                0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS);
    }

    public static void updateText(Component text) {
        bossBar.name(text);
    }

    public static void addPlayer(Player player) {
        removePlayer(player);
        player.showBossBar(bossBar);
    }

    public static void removePlayer(Player player) {
        player.hideBossBar(bossBar);
    }

    public static void removeAllPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.isEmpty()) {
            return;
        }

        final List<Player> playerList = new java.util.ArrayList<>(players);
        final int[] index = { 0 };

        new BukkitRunnable() {
            @Override
            public void run() {
                if (index[0] >= playerList.size()) {
                    this.cancel();
                    return;
                }

                Player player = playerList.get(index[0]);
                if (player != null && player.isOnline()) {
                    removePlayer(player);
                }

                index[0]++;
            }
        }.runTaskTimer(JBuildBattle.getInstance(), 0L, 2L); // 2 ticks (0.1 segundos)
    }

}
