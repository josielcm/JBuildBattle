package me.josielcm.jcm.game.vote;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.Key;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.api.items.ItemBuilder;
import me.josielcm.jcm.game.GameTheme;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.ui.BossBarManager;

public class VoteManager {
    
    @Getter
    private Set<GameTheme> options = Set.of(GameTheme.values());

    @Getter
    private static Set<UUID> playersVoted = new HashSet<>();

        private static Map<GameTheme, Integer> votes = new HashMap<>(Map.of(
        GameTheme.CITY, 0,
        GameTheme.CASTLE, 0,
        GameTheme.LANDSCAPE, 0,
        GameTheme.COLOR, 0
    ));

    @Getter
    private static VoteTask voteTask;

    @Setter
    private static String voteMessage = "<gold><time></gold>";

    public static void startVotes() {
        if (voteTask != null) {
            voteTask.cancel();
            voteTask = null;
        }

        giveItems();
        voteTask = new VoteTask(30, voteMessage);
        voteTask.runTaskTimer(JBuildBattle.getInstance(), 0, 20);
    }

    public static void stopVotes() {
        if (voteTask != null) {
            voteTask.cancel();
            voteTask = null;
        }

        String winner = getThemeWinner().getName();
        PlayerManager.sendTitle("<color:#C8B273>" + winner, "", 1, 5, 1);
        BossBarManager.updateText(Color.parse("<color:#C8B273>" + winner));

        JBuildBattle.getInstance().getGameManager().startGame(getThemeWinner());

        playersVoted.clear();
        votes.replaceAll((k, v) -> 0);
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
            player.sendMessage("<gold>¡Votación terminada!");
        });
    }

    public static void addVote(GameTheme theme, UUID player) {
        if (playersVoted.contains(player)) {
            return;
        }

        playersVoted.add(player);
        votes.put(theme, votes.get(theme) + 1);
    }

    private static void giveItems() {
        ItemStack cityItem = ItemBuilder.builder()
                .material(Material.PAPER)
                .displayName("<color:#C8B273>Ciudad")
                .pdc(Key.getVoteOptionsItemsKey(),"CITY")
                .build();
        ItemStack castleItem = ItemBuilder.builder()
                .material(Material.PAPER)
                .displayName("<color:#C8B273>Castillo")
                .pdc(Key.getVoteOptionsItemsKey(),"CASTLE")
                .build();

        ItemStack landscapeItem = ItemBuilder.builder()
                .material(Material.PAPER)
                .displayName("<color:#C8B273>Paisaje")
                .pdc(Key.getVoteOptionsItemsKey(),"LANDSCAPE")
                .build();
        
        ItemStack colorItem = ItemBuilder.builder()
                .material(Material.PAPER)
                .displayName("<color:#C8B273>Colorido")
                .pdc(Key.getVoteOptionsItemsKey(),"COLOR")
                .build();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().addItem(cityItem, castleItem, landscapeItem, colorItem);
            player.sendMessage(Color.parse("<gold>¡Vota por una tematica!"));
            player.sendMessage(Color.parse("<grey>Click derecho al item para votar"));
        });
        
    }

    public static GameTheme getThemeWinner() {
        GameTheme winner = GameTheme.NONE;
        int maxVotes = 0;

        for (Map.Entry<GameTheme, Integer> entry : votes.entrySet()) {
            if (entry.getValue() > maxVotes) {
                maxVotes = entry.getValue();
                winner = entry.getKey();
            }
        }

        return winner;
    }

}