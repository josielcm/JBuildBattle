package me.josielcm.jcm.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import io.papermc.paper.event.entity.EntityMoveEvent;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.Arena;
import me.josielcm.jcm.game.GameState;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.player.TeamType;

public class GameListener implements Listener {
    
    @EventHandler
    public void onMove(PlayerMoveEvent ev) {
        Player player = ev.getPlayer();

        if (player.hasPermission("jbuildbattle.bypass")) {
            return;
        }

        if (hasMoved(ev)) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }

            if (Arena.isInRegion(player)) {
                return;
            } else {
                if (JBuildBattle.getInstance().getGameManager().isCanLeaveZone()) {
                    return;
                }

                ev.setCancelled(true);
                
                TeamType team = PlayerManager.getTeam(player);
                if (team != null) {
                    switch (team) {
                        case PROS:
                            player.teleport(Arena.getSpawnPros());
                            break;
                        case NOOBS:
                            player.teleport(Arena.getSpawnNoobs());
                            break;
                        default:
                            break;
                    }
                } else {
                    player.teleport(Arena.getSpawnNoobs());
                }

                player.sendMessage(Color.parse("<red>¡No puedes salir de tu zona!"));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent ev) {
        Player player = ev.getPlayer();

        if (player.hasPermission("jbuildbattle.bypass")) {
            return;
        }

        if (ev.getBlock() == null) {
            return;
        }

        if (Arena.getRegion(player).isIn(ev.getBlock().getLocation())) {
            if (JBuildBattle.getInstance().getGameManager().getGameState() == GameState.PLAYING) {
                return;
            }

            ev.setCancelled(true);
            player.sendMessage(Color.parse("<red>¡No puedes romper bloques en este momento!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
        } else {
            ev.setCancelled(true);
            player.sendMessage(Color.parse("<red>¡No puedes romper bloques fuera de tu zona!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent ev) {
        Player player = ev.getPlayer();

        if (player.hasPermission("jbuildbattle.bypass")) {
            return;
        }

        if (ev.getBlock() == null) {
            return;
        }

        if (Arena.getRegion(player).isIn(ev.getBlock().getLocation())) {
            if (JBuildBattle.getInstance().getGameManager().getGameState() == GameState.PLAYING) {
                return;
            }

            ev.setCancelled(true);
            player.sendMessage(Color.parse("<red>¡No puedes colocar bloques en este momento!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
        } else {
            ev.setCancelled(true);
            player.sendMessage(Color.parse("<red>¡No puedes colocar bloques fuera de tu zona!"));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 0.5f);
        }
    }

    @EventHandler
    public void onLiquidExpand(BlockFromToEvent ev) {
        Block fromBlock = ev.getBlock();
        Block toBlock = ev.getToBlock();
        Material fromType = fromBlock.getType();

        if (fromBlock == null || toBlock == null || fromType == null) {
            return;
        }

        if (!Arena.isInRegion(fromBlock.getLocation())) {
            if (fromType == Material.WATER || fromType == Material.LAVA) {
                ev.setCancelled(true);
            }
            return;
        }
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent ev) {
        if (!(ev.getEntity() instanceof Player)) {
            Location to = ev.getTo();
            
            if (!Arena.isInRegion(to)) {
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplote(EntityExplodeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent ev) {
        if (ev.getEntity() instanceof Creature) {
            ev.getEntity().remove();
            ev.setCancelled(true);
        }
    }

    private boolean hasMoved(PlayerMoveEvent ev) {
        return ev.getFrom().getBlockX() != ev.getTo().getBlockX() ||
               ev.getFrom().getBlockY() != ev.getTo().getBlockY() ||
               ev.getFrom().getBlockZ() != ev.getTo().getBlockZ();
    }

}
