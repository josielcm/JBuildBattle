package me.josielcm.jcm.listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Getter;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.api.papi.PAPI;
import me.josielcm.jcm.game.GameState;
import me.josielcm.jcm.game.vote.VoteManager;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.player.TeamManager;
import me.josielcm.jcm.ui.BossBarManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class PlayerListener implements Listener {

    @Getter
    private HashMap<UUID, Long> cooldownMessage = new HashMap<>();

    @Getter
    private static Set<UUID> mutedPlayers = new HashSet<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        Player player = ev.getPlayer();

        BossBarManager.addPlayer(player);
        ev.joinMessage(Color.parse(""));

        BossBar emptyBar = BossBar.bossBar(
                Color.parse(""),
                0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS);

        player.showBossBar(emptyBar);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 10, true, false));
        player.getInventory().clear();

        if (player.hasPermission("jbuildbattle.bypass")) {
            player.sendMessage(Color.parse("<yellow>¡Tienes bypass del BuildBattle capo!"));
            return;
        }

        GameState gameState = JBuildBattle.getInstance().getGameManager().getGameState();

        switch (gameState) {
            case PLAYING:
                player.setGameMode(GameMode.CREATIVE);
                break;
            case ENDED:
                player.setGameMode(GameMode.SPECTATOR);
                break;
            case VOTING:
                VoteManager.openVoteMenu(player);
            case WAITING:
            default:
                player.setGameMode(GameMode.ADVENTURE);    
                break;
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

    @EventHandler
    public void onChat(AsyncChatEvent ev) {
        Player player = ev.getPlayer();
        Component message = formatMessage(player, ev.message());
        ev.setCancelled(true);

        if (mutedPlayers.contains(player.getUniqueId())) {
            player.sendMessage(Color.parse("<red>¡No puedes hablar, un moderador te silecio!</red>",
                    "<gradient:#FCD46D:#FCD369:#FCD265:#FCD160:#FCD05C:#FCD160:#FCD265><b>zEvento</b> <grey>»</grey> "));
            return;
        }

        if (isOnCooldown(player)) {
            player.sendMessage(Color.parse("<red>¡Debes esperar para enviar otro mensaje!</red>",
                    "<gradient:#FCD46D:#FCD369:#FCD265:#FCD160:#FCD05C:#FCD160:#FCD265><b>zEvento</b> <grey>»</grey> "));
            return;
        }

        String prefix = PAPI.setPlaceholders(player, "%luckperms_prefix%");
        ChatFormat format = getChatFormat(player);

        Component formattedMessage = format.format(prefix, player.getName(), message);
        Bukkit.broadcast(formattedMessage);
        cooldownMessage.put(player.getUniqueId(), System.currentTimeMillis());
    }

    private boolean isOnCooldown(Player player) {
        if (!cooldownMessage.containsKey(player.getUniqueId())) {
            return false;
        }

        long lastMessageTime = cooldownMessage.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastMessageTime) < 3000;
    }

    private enum ChatFormat {
        ADMIN("<gold>", "<gray>"),
        PROS("<color:#5C93FC>", "<gray>"),
        DEFAULT("<color:#FC5C5C>", "<gray>");

        private final String nameColor;
        private final String messageColor;

        ChatFormat(String nameColor, String messageColor) {
            this.nameColor = nameColor;
            this.messageColor = messageColor;
        }

        public Component format(String prefix, String name, Component message) {
            return Component.empty()
                    .append(Color.parse(prefix))
                    .append(Color.parse(nameColor + name + "</" + nameColor.substring(1)))
                    .append(Color.parse(" <dark_gray>»</dark_gray> "))
                    .append(Color.parse(messageColor))
                    .append(message);
        }
    }

    private ChatFormat getChatFormat(Player player) {
        if (player.hasPermission("jbuildbattle.admin")) {
            return ChatFormat.ADMIN;
        } else if (player.hasPermission("jbuildbattle.team.pro")) {
            return ChatFormat.PROS;
        }
        return ChatFormat.DEFAULT;
    }

    private Component formatMessage(Player player, Component message) {
        String plainText = PlainTextComponentSerializer.plainText().serialize(message);

        if (player.hasPermission("jbuildbattle.chat.format")) {
            return Color.parse(plainText);
        }

        return Component.text(plainText.replace("<", "＜").replace(">", "＞"));
    }

}
