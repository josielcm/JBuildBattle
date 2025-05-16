package me.josielcm.jcm.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.GameState;
import me.josielcm.jcm.game.vote.VoteManager;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.player.TeamManager;
import me.josielcm.jcm.ui.BossBarManager;
import net.kyori.adventure.bossbar.BossBar;

public class PlayerListener implements Listener {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        BossBarManager.addPlayer(player);
        ev.joinMessage(Color.parse(""));

        if (player.hasPermission("jbuildbattle.bypass")) {
            player.sendMessage(Color.parse("<yellow>¡Tienes bypass del BuildBattle capo!"));
            return;
        }

        BossBar emptyBar = BossBar.bossBar(
                Color.parse(""),
                0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS);

        player.showBossBar(emptyBar);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 10, true, false));
        player.getInventory().clear();

        GameState gameState = JBuildBattle.getInstance().getGameManager().getGameState();
        
        if (gameState == GameState.PLAYING) {
            player.setGameMode(GameMode.CREATIVE);
        } else {
            player.setGameMode(GameMode.ADVENTURE);
        }

        if (gameState == GameState.VOTING) {
            VoteManager.openVoteMenu(player);
        }

        if (JBuildBattle.getInstance().getGameManager().isGlow()) {
            player.setGlowing(true);
        }

        PlayerManager.onJoin(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();
        PlayerManager.onLeave(player);
        TeamManager.removePlayerFromTeam(player);
        ev.quitMessage(Color.parse(""));
    }

}
