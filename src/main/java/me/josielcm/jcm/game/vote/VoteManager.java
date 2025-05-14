package me.josielcm.jcm.game.vote;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
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
    private static final Set<GameTheme> options = EnumSet.allOf(GameTheme.class);

    @Getter
    private static final Set<UUID> playersVoted = new HashSet<>();

    private static final Map<GameTheme, Integer> votes = new EnumMap<>(GameTheme.class);

    @Getter
    private static VoteTask voteTask;

    @Setter
    private static String voteMessage = "<yellow>Tiempo de votación</yellow> <grey>|</grey <gold><b><time></b></gold>";

    @Getter
    private static Gui gui;

    static {
        Arrays.stream(GameTheme.values())
                .forEach(theme -> votes.put(theme, 0));
    }

    public static void startVotes() {
        if (voteTask != null) {
            voteTask.cancel();
            voteTask = null;
        }

        openVoteMenu();
        voteTask = new VoteTask(30, voteMessage);
        voteTask.runTaskTimer(JBuildBattle.getInstance(), 0, 20);
        PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
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
            player.closeInventory();
            player.sendMessage(Color.parse("<gold>¡Votación terminada!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1f, 0.8f);
        });
    }

    public static void addVote(GameTheme theme, UUID player) {
        if (playersVoted.contains(player)) {
            return;
        }

        playersVoted.add(player);
        votes.put(theme, votes.get(theme) + 1);
    }

    private static void openVoteMenu() {
        gui = Gui.gui()
                .title(Color.parse("<gold><b>¡Vota por una tematica!</b><gold>"))
                .rows(3)
                .disableAllInteractions()
                .create();

        gui.setCloseGuiAction(event -> handleGuiClose(event));

        Map<GameTheme, GuiItem> items = createVoteItems();
        items.forEach((theme, item) -> gui.setItem(getSlotForTheme(theme), item));

        openGuiForEligiblePlayers();
    }

    private static Map<GameTheme, GuiItem> createVoteItems() {
        Map<GameTheme, ItemStack> itemStacks = Map.of(
                GameTheme.CITY,
                createItemStack("Ciudad", "<gradient:#ECECEC:#FBFBFB>",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIzYWM5M2Q1ZTk5OTVkZTg0YWE5NWRlM2M4OWU0MDQyNTYwNjAzNGRkOGQ3NWNmZDcxOTYxNjA5YmQ1MjgxNSJ9fX0="),
                GameTheme.CASTLE,
                createItemStack("Castillo", "<gradient:#B5B5B5:#C1C1C1>",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmVlZjdlNTZjZGU3NDA3NzJkZmI3NmRkZDJmNTg0YmU4OTA3Yjg1OTc2NjhlNDAyNjM0OTg2NDY5MjMwYWE0OSJ9fX0="),
                GameTheme.LANDSCAPE,
                createItemStack("Paisaje", "<gradient:#74FFFF:#98FF7C>",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgwZDMyOTVkM2Q5YWJkNjI3NzZhYmNiOGRhNzU2ZjI5OGE1NDVmZWU5NDk4YzRmNjlhMWMyYzc4NTI0YzgyNCJ9fX0="),
                GameTheme.COLOR,
                createItemStack("Colorido",
                        "<gradient:#FF6B6B:#FFAF70:#FFF275:#C7EE80:#8EEA8B:#6A9EFF:#7B4FFF:#8B00FF>",
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY1NzAxZGE0ZDUwZGQ0NDdlNjgzZGQ5MDA3N2NhMjJkNmI0NjEyZTVkYTEwODAzZTNjNDg2YzUyOTg3ZmVlZiJ9fX0="));

        return itemStacks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> createGuiItem(e.getKey(), e.getValue())));
    }

    private static ItemStack createItemStack(String name, String gradient, String texture) {
        return ItemBuilder.builder()
                .material(Material.PLAYER_HEAD)
                .displayName(gradient + name)
                .skullTexture(texture)
                .pdc(Key.getVoteOptionsItemsKey(), name.toUpperCase())
                .build();
    }

    private static GuiItem createGuiItem(GameTheme theme, ItemStack itemStack) {
        return new GuiItem(itemStack, event -> {
            event.setCancelled(true);
            handleVote(event.getWhoClicked(), theme);
        });
    }

    private static void handleVote(HumanEntity player, GameTheme theme) {
        if (playersVoted.contains(player.getUniqueId())) {
            player.sendMessage(Color.parse("<red>¡Ya has votado!"));
            gui.close(player);
            return;
        }

        addVote(theme, player.getUniqueId());
        player.sendMessage(Color.parse("<gold>¡Votaste por " + theme.getName() + "!"));
        player.closeInventory();
    }

    private static int getSlotForTheme(GameTheme theme) {
        return switch (theme) {
            case CITY -> 10;
            case CASTLE -> 12;
            case LANDSCAPE -> 14;
            case COLOR -> 16;
            default -> throw new IllegalArgumentException("Theme not supported: " + theme);
        };
    }

    private static void openGuiForEligiblePlayers() {
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> !playersVoted.contains(player.getUniqueId()))
                .forEach(gui::open);
    }

    public static void handleGuiClose(InventoryCloseEvent event) {
        try {
            HumanEntity player = event.getPlayer();
            if (playersVoted.contains(player.getUniqueId())) {
                player.sendMessage(Color.parse("<red>¡Ya has votado!"));
                gui.close(player);
                return;
            }
    
            Bukkit.getScheduler().runTaskLater(JBuildBattle.getInstance(), () -> {
                try {
                    player.closeInventory();
                    gui.open(player);
                } catch (Exception e) {
                    Bukkit.getLogger().severe("Error reopening GUI: " + e.getMessage());
                }
            }, 1L);
        } catch (Exception e) {
            Bukkit.getLogger().severe("Error handling GUI close: " + e.getMessage());
        }
    }

    public static GameTheme getThemeWinner() {
        if (votes.isEmpty() || playersVoted.isEmpty()) {
            Bukkit.getLogger().warning("No votes registered, returning NONE");
            return GameTheme.NONE;
        }
    
        return votes.entrySet().stream()
            .collect(Collectors.groupingBy(Map.Entry::getValue))
            .entrySet().stream()
            .max((entry1, entry2) -> Integer.compare(entry1.getKey(), entry2.getKey()))
            .map(entry -> {
                List<GameTheme> themes = entry.getValue().stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
                GameTheme winner = themes.get(new Random().nextInt(themes.size()));
                Bukkit.getLogger().info("Winner theme selected: " + winner);
                return winner;
            })
            .orElse(GameTheme.NONE);
    }

}