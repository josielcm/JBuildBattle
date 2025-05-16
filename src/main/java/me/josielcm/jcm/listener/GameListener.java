package me.josielcm.jcm.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import io.papermc.paper.event.entity.EntityMoveEvent;
import lombok.Getter;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.Arena;
import me.josielcm.jcm.game.GameState;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.player.TeamType;

public class GameListener implements Listener {

    @Getter
    private static HashMap<UUID, Long> lastMessage = new HashMap<>();

    private static final long COOLDOWN_TIME = 5000;

    @EventHandler
    public void onMove(PlayerMoveEvent ev) {
        Player player = ev.getPlayer();

        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

        if (player.hasPermission("jbuildbattle.bypass")) {
            return;
        }

        if (hasMoved(ev)) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }

            boolean isInRegion = Arena.isInRegion(player);
            boolean canLeaveZone = JBuildBattle.getInstance().getGameManager().isCanLeaveZone();

            if (isInRegion) {
                return;
            } else {
                if (canLeaveZone) {
                    return;
                }

                TeamType team = PlayerManager.getTeam(player);
                if (team != null) {
                    Location spawnLocation;
                    switch (team) {
                        case PROS:
                            spawnLocation = Arena.getSpawnPros();
                            break;
                        case NOOBS:
                        default:
                            spawnLocation = Arena.getSpawnNoobs();
                            break;
                    }
                    if (spawnLocation != null) {
                        player.teleport(spawnLocation);
                    }
                } else {
                    player.teleport(Arena.getSpawnNoobs());
                }

                sendCooldownMessage(player, "<red>¡No puedes salir de tu área!", Sound.BLOCK_ANVIL_PLACE, 1, 2);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent ev) {
        Player player = ev.getPlayer();

        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

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
            sendCooldownMessage(player, "<red>¡No puedes romper bloques en este momento!", Sound.BLOCK_ANVIL_PLACE, 1,
                    2);
        } else {
            ev.setCancelled(true);
            sendCooldownMessage(player, "<red>¡No puedes romper bloques fuera de tu área!", Sound.BLOCK_ANVIL_PLACE, 1,
                    2);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent ev) {
        Player player = ev.getPlayer();

        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

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
            sendCooldownMessage(player, "<red>¡No puedes colocar bloques en este momento!", Sound.BLOCK_ANVIL_PLACE, 1,
                    2);
        } else {
            ev.setCancelled(true);
            sendCooldownMessage(player, "<red>¡No puedes colocar bloques fuera de tu área!", Sound.BLOCK_ANVIL_PLACE, 1,
                    2);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent ev) {
        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

        if (ev.getDamager() instanceof Player) {
            Player player = (Player) ev.getDamager();

            if (player.hasPermission("jbuildbattle.bypass")) {
                return;
            }

            if (ev.getEntity() instanceof Player) {
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent ev) {
        Player player = ev.getPlayer();

        if (player.hasPermission("jbuildbattle.copy") && player.isSneaking()) {
            if (ev.getAction() == Action.RIGHT_CLICK_BLOCK && ev.getClickedBlock() != null) {
                Block block = ev.getClickedBlock();
                player.getInventory().setItemInMainHand(new ItemStack(block.getType(), 1));
            }
        }

        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

        if (player.hasPermission("jbuildbattle.bypass")) {
            return;
        }

        if (ev.getItem() == null) {
            return;
        }

        ItemStack item = ev.getItem();

        if (item.getType().toString().contains("EGG") || item.getType().toString().contains("SPAWN_EGG")) {
            ev.setCancelled(true);
            sendCooldownMessage(player, "<red>¡No puedes usar huevos de generación!", Sound.BLOCK_ANVIL_PLACE, 1, 2);
        }

        if (item.getType() == Material.FLINT_AND_STEEL && ev.getClickedBlock() != null
                && ev.getClickedBlock().getType() == Material.TNT) {
            ev.setCancelled(true);
            sendCooldownMessage(player, "<red>¡No puedes encender TNT!", Sound.BLOCK_ANVIL_PLACE, 1, 2);
        }
    }

    @EventHandler
    public void onLiquidExpand(BlockFromToEvent ev) {
        Block fromBlock = ev.getBlock();
        Block toBlock = ev.getToBlock();
        Material fromType = fromBlock.getType();

        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

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
        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

        if (!(ev.getEntity() instanceof Player)) {
            Location to = ev.getTo();

            if (!Arena.isInRegion(to)) {
                ev.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onExplote(EntityExplodeEvent ev) {
        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

        ev.setCancelled(true);
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent ev) {
        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent ev) {
        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }
        ev.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent ev) {
        if (JBuildBattle.getInstance().getGameManager().isAllowALL()) {
            return;
        }

        if (ev.getEntity() instanceof Creature) {
            ev.getEntity().remove();
            ev.setCancelled(true);
        }
    }

    private boolean isOnCooldown(Player player) {
        if (!lastMessage.containsKey(player.getUniqueId())) {
            return false;
        }

        long lastMessageTime = lastMessage.get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastMessageTime) < COOLDOWN_TIME;
    }

    private void sendCooldownMessage(Player player, String message, Sound sound, float volume, float pitch) {
        if (!isOnCooldown(player)) {
            player.sendMessage(Color.parse(message, "<gradient:#FCD46D:#FCD369:#FCD265:#FCD160:#FCD05C:#FCD160:#FCD265><b>zEvento</b> <grey>»</grey> "));
            if (sound != null) {
                player.playSound(player.getLocation(), sound, volume, pitch);
            }
            lastMessage.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    private boolean hasMoved(PlayerMoveEvent ev) {
        return ev.getFrom().getBlockX() != ev.getTo().getBlockX() ||
                ev.getFrom().getBlockY() != ev.getTo().getBlockY() ||
                ev.getFrom().getBlockZ() != ev.getTo().getBlockZ();
    }

}
