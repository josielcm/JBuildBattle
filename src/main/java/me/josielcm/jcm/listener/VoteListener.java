package me.josielcm.jcm.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.josielcm.jcm.api.Key;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.GameTheme;
import me.josielcm.jcm.game.vote.VoteManager;

public class VoteListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        if (ev.getItem() == null)
            return;
        if (!ev.getItem().hasItemMeta())
            return;

        Player player = ev.getPlayer();

        if (ev.getItem().getItemMeta().getPersistentDataContainer().has(Key.getVoteOptionsItemsKey(),
                PersistentDataType.STRING)) {
            PersistentDataContainer data = ev.getItem().getItemMeta().getPersistentDataContainer();
            ev.setCancelled(true);

            if (VoteManager.getPlayersVoted().contains(player.getUniqueId())) {
                player.sendMessage(Color.parse("<red>¡Ya has votado!"));
                player.getInventory().clear();
                return;
            }

            switch (data.get(Key.getVoteOptionsItemsKey(), PersistentDataType.STRING)) {
                case "CITY":
                    VoteManager.addVote(GameTheme.CITY, player.getUniqueId());
                    player.getInventory().clear();
                    player.sendMessage(Color.parse("<gold>¡Has votado por la Ciudad!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    break;
                case "CASTLE":
                    VoteManager.addVote(GameTheme.CASTLE, player.getUniqueId());
                    player.getInventory().clear();
                    player.sendMessage(Color.parse("<gold>¡Has votado por el Castillo!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    break;
                case "LANDSCAPE":
                    VoteManager.addVote(GameTheme.LANDSCAPE, player.getUniqueId());
                    player.getInventory().clear();
                    player.sendMessage(Color.parse("<gold>¡Has votado por el Paisaje!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    break;
                case "COLOR":
                    VoteManager.addVote(GameTheme.COLOR, player.getUniqueId());
                    player.getInventory().clear();
                    player.sendMessage(Color.parse("<gold>¡Has votado por el tema Colorido!"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    break;
                default:
                    player.sendMessage(Color.parse("<red>¡Opción de voto inválida!"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 0.5f);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent ev) {
        Player player = ev.getPlayer();

        if (ev.getItemDrop().getItemStack().hasItemMeta()) {
            if (ev.getItemDrop().getItemStack().getItemMeta().getPersistentDataContainer()
                    .has(Key.getVoteOptionsItemsKey(), PersistentDataType.STRING)) {
                ev.setCancelled(true);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 0.5f);
            }
        }
    }

}
//
// CITY("Ciudad"),
// CASTLE("Castillo"),
// LANDSCAPE("Paisaje"),
// COLOR("Colorido"),
// NONE("Ninguno");
