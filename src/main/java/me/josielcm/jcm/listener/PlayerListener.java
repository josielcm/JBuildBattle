package me.josielcm.jcm.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.GameState;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.ui.BossBarManager;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        if (player.hasPermission("jbuildbattle.bypass")) {
            player.sendMessage(Color.parse("<gold>¡Bienvenido al BUILDBATTLE!"));
            player.sendMessage(Color.parse("<yellow>¡Tienes bypass!"));
            return;
        }

        player.sendMessage(Color.parse("<gold>¡Bienvenido al BUILDBATTLE!"));
        
        if (JBuildBattle.getInstance().getGameManager().getGameState() == GameState.PLAYING) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }

        PlayerManager.onJoin(player);
        BossBarManager.addPlayer(player);
        ev.joinMessage(Color.parse("<gold>¡" + player.getName() + " se ha unido a la partida!"));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        PlayerManager.onLeave(player);
        ev.quitMessage(Color.parse("<red>¡" + player.getName() + " se ha ido de la partida!"));
    }

}
