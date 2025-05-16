package me.josielcm.jcm.ui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.api.formats.Color;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;

public class BossBarManager {

    @Getter
    @Setter
    private static BossBar bossBar;

    public BossBarManager() {
        bossBar = BossBar.bossBar(
                Color.parse("<gold><b>Esperando</b></gold>"),
                0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS);
    }

    public static void updateText(Component text) {
        bossBar.name(text);
    }

    public static void addPlayer(Player player) {
        if (bossBar == null) {
            bossBar = BossBar.bossBar(
                Color.parse("<gold><b>Esperando</b></gold>"),
                0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS);
        }

        removePlayer(player);
        player.showBossBar(bossBar);
    }

    public static void removePlayer(Player player) {
        player.hideBossBar(bossBar);
    }

    public static void removeAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.hideBossBar(bossBar);
        });
    }

}
