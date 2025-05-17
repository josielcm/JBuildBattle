package me.josielcm.jcm.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.FileManager;
import me.josielcm.jcm.api.regions.Cuboid;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.game.vote.VoteManager;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.player.TeamManager;
import me.josielcm.jcm.player.TeamType;
import me.josielcm.jcm.ui.BossBarManager;

public class GameManager {

    @Getter
    @Setter
    private GameState gameState;

    @Getter
    @Setter
    private TeamManager teamManager;

    @Getter
    @Setter
    private GameTheme gameTheme;

    @Getter
    private GameTask gameTask;

    @Getter
    private BukkitTask task;

    @Getter
    @Setter
    private boolean canLeaveZone = false;

    @Getter
    @Setter
    private boolean allowALL = false;

    @Getter
    @Setter
    private boolean glow = false;

    public GameManager() {
        this.gameTheme = GameTheme.NONE;
        this.gameState = GameState.WAITING;
        this.teamManager = new TeamManager();
        new BossBarManager();
        instanceArena();
    }

    private void instanceArena() {
        String worldName = FileManager.getSettings().getString("zones.world");
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            JBuildBattle.getInstance().getLogger()
                    .severe("¡No se pudo encontrar el mundo '" + worldName
                            + "'! Asegúrate de que existe o que esté bien escrito.");
            return;
        }

        Arena.setProsRegion(loadRegion("zones.pros", world));
        Arena.setSpawnPros(loadLocation("zones.pros.spawn", world));
        Arena.setNoobsRegion(loadRegion("zones.noobs", world));
        Arena.setSpawnNoobs(loadLocation("zones.noobs.spawn", world));

        Cuboid prosRegion = Arena.getProsRegion();
        Cuboid noobsRegion = Arena.getNoobsRegion();

        JBuildBattle.getInstance().getLogger().info("Se han cargado las regiones del juego:");
        JBuildBattle.getInstance().getLogger().info("- Pros: " +
                prosRegion.getPoint1().getBlockX() + "," +
                prosRegion.getPoint1().getBlockY() + "," +
                prosRegion.getPoint1().getBlockZ() + " a " +
                prosRegion.getPoint2().getBlockX() + "," +
                prosRegion.getPoint2().getBlockY() + "," +
                prosRegion.getPoint2().getBlockZ());
        JBuildBattle.getInstance().getLogger().info("- Noobs: " +
                noobsRegion.getPoint1().getBlockX() + "," +
                noobsRegion.getPoint1().getBlockY() + "," +
                noobsRegion.getPoint1().getBlockZ() + " a " +
                noobsRegion.getPoint2().getBlockX() + "," +
                noobsRegion.getPoint2().getBlockY() + "," +
                noobsRegion.getPoint2().getBlockZ());
    }

    public void start() {
        cancelGameTaskIfRunning();

        if (gameState == GameState.WAITING) {
            gameState = GameState.VOTING;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            BossBarManager.addPlayer(player);
        });
        startVoting();
    }

    private void startVoting() {
        VoteManager.startVotes();
    }

    public void startGame(GameTheme gameTheme) {
        this.gameTheme = gameTheme;
        this.gameState = GameState.STARTING;

        final AtomicInteger countdown = new AtomicInteger(60);

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown.get() <= 0) {
                    new BukkitRunnable() {
                        public void run() {
                            canLeaveZone = false;
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tick rate 20");
                            gameTask = new GameTask(3600);
                            gameTask.setMessage(FileManager.getMessages().getString("bossbar-timer"));
                            gameTask.runTaskTimer(JBuildBattle.getInstance(), 0L, 20L);
                            gameState = GameState.PLAYING;
                            changeGameMode(GameMode.CREATIVE);
                            BossBarManager.addAllPlayers();
                            PlayerManager.sendTitle("<color:#FFD04D><b>¡A construir!", "", 1, 5, 1);
                            PlayerManager.playSound(Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.8f);
                        };
                    }.runTask(JBuildBattle.getInstance());
                    cancel();
                    return;
                }

                if (countdown.get() == 60) {
                    new BukkitRunnable() {
                        public void run() {

                            try {
                                canLeaveZone = true;
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tick rate 80");
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                                        "cinematic start * cinematic_4238 960 80 1");
                                BossBarManager.removeAllPlayers();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        };
                    }.runTask(JBuildBattle.getInstance());
                }

                BossBarManager.updateText(Color.parse("<color:#FFD04D><b>Iniciando en " + countdown.get() + "</b>"));
                countdown.decrementAndGet();
            }
        }.runTaskTimerAsynchronously(JBuildBattle.getInstance(), 0L, 20L);
    }

    public void stop() {
        cancelGameTaskIfRunning();

        if (gameState == GameState.PLAYING) {
            gameState = GameState.ENDED;
        }

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.getInventory().clear();
        });

        teleportPlayers();
        changeGameMode(GameMode.SPECTATOR);
        canLeaveZone = true;
        BossBarManager.removeAllPlayers();
        BossBarManager.updateText(Color.parse("<color:#FFD04D><b>¡Se acabó el tiempo!</b>"));

        PlayerManager.sendTitle("<color:#FFD04D><b>¡Se acabó el tiempo!</b>", "", 1, 3, 1);
        PlayerManager.playSound(Sound.BLOCK_ANVIL_PLACE, 1.0f, 2);
    }

    public void reset() {
        cancelGameTaskIfRunning();
        gameState = GameState.WAITING;
        gameTheme = GameTheme.NONE;
        canLeaveZone = false;

        teleportPlayers();
        changeGameMode(GameMode.ADVENTURE);
        BossBarManager.updateText(Color.parse("<color:#FFD04D><b>Esperando</b></color>"));
        BossBarManager.addAllPlayers();
        PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 0.8f);
    }

    public void addTime(int time) {
        if (gameTask != null) {
            gameTask.addTime(time);
            if (gameTask.getTime().get() > 10) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    BossBarManager.addPlayer(player);
                });
            }
        }
    }

    public void removeTime(int time) {
        if (gameTask != null) {
            gameTask.removeTime(time);

            if (gameTask.getTime().get() <= 10) {
                BossBarManager.removeAllPlayers();
            }
        }
    }

    public void setTime(int time) {
        if (gameTask != null) {
            gameTask.setTime(time);
            if (gameTask.getTime().get() > 10) {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    BossBarManager.addPlayer(player);
                });
            }
        }
    }

    public void toggleAll() {
        allowALL = !allowALL;
        String message = allowALL ? "<color:#FE3739><b>¡LIBEREN EL CAOS!" : "<red><b>¡Caos desactivado!";
        if (allowALL) {
            changeGameMode(GameMode.CREATIVE);
        } else {
            reset();
        }
        PlayerManager.sendTitle(message, "", 1, 3, 1);
        PlayerManager.playSound(Sound.BLOCK_FIRE_AMBIENT, 1, 0.1f);
        PlayerManager.playSound(Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
    }

    public void toggleGlow() {
        glow = !glow;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.isEmpty()) {
            return;
        }

        final List<Player> playerList = new ArrayList<>(players);
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
                    player.setGlowing(glow);
                }

                index[0]++;
            }
        }.runTaskTimer(JBuildBattle.getInstance(), 0L, 2L); // 2 ticks (0.1 segundos)
    }

    public void win(TeamType team) {
        switch (team) {
            case PROS:
                PlayerManager.sendTitle("<color:#FFD04D><b>¡Los <color:#5C93FC>pros</color> han ganado!</b>", "", 1, 3,
                        1);
                PlayerManager.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                launchFireworks(team);

                break;
            case NOOBS:
                PlayerManager.sendTitle("<color:#FFD04D><b>¡Los <color:#FC5C5C>100</color> han ganado!</b>", "", 1, 3,
                        1);
                PlayerManager.playSound(Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
                launchFireworks(team);
                break;
            default:
                break;
        }
    }

    private void launchFireworks(TeamType team) {
        switch (team) {
            case PROS:
                final AtomicInteger countdown = new AtomicInteger(30);
                Set<UUID> prosPlayers = PlayerManager.getPlayers().keySet().stream()
                        .filter(uuid -> {
                            Player player = Bukkit.getPlayer(uuid);
                            return player != null && PlayerManager.getTeam(player) == TeamType.PROS;
                        })
                        .collect(java.util.stream.Collectors.toSet());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (countdown.get() <= 0) {
                            this.cancel();
                            return;
                        }

                        for (UUID uuid : prosPlayers) {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null && player.isOnline()) {
                                Location loc = player.getLocation();
                                spawnFirework(loc);
                            }
                        }

                        countdown.decrementAndGet();
                    }
                }.runTaskTimer(JBuildBattle.getInstance(), 0L, 10L); // Cada 0.5 segundos
                break;

            case NOOBS:
                final AtomicInteger noobsCountdown = new AtomicInteger(20);
                Set<UUID> noobsPlayers = PlayerManager.getPlayers().keySet().stream()
                        .filter(uuid -> {
                            Player player = Bukkit.getPlayer(uuid);
                            return player != null && PlayerManager.getTeam(player) == TeamType.NOOBS;
                        })
                        .collect(java.util.stream.Collectors.toSet());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (noobsCountdown.get() <= 0) {
                            this.cancel();
                            return;
                        }

                        for (UUID uuid : noobsPlayers) {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null && player.isOnline()) {
                                Location loc = player.getLocation();
                                spawnFirework(loc);
                            }
                        }

                        noobsCountdown.decrementAndGet();
                    }
                }.runTaskTimer(JBuildBattle.getInstance(), 0L, 20L); // Cada 1 segundo
                break;

            default:
                break;
        }
    }

    private void spawnFirework(Location location) {
        org.bukkit.entity.Firework firework = location.getWorld().spawn(location, org.bukkit.entity.Firework.class);
        org.bukkit.inventory.meta.FireworkMeta meta = firework.getFireworkMeta();

        org.bukkit.FireworkEffect.Type[] types = org.bukkit.FireworkEffect.Type.values();
        org.bukkit.FireworkEffect.Type type = types[new java.util.Random().nextInt(types.length)];

        java.util.Random random = new java.util.Random();
        org.bukkit.Color[] colors = {
                org.bukkit.Color.AQUA,
                org.bukkit.Color.BLUE,
                org.bukkit.Color.FUCHSIA,
                org.bukkit.Color.GREEN,
                org.bukkit.Color.LIME,
                org.bukkit.Color.ORANGE,
                org.bukkit.Color.PURPLE,
                org.bukkit.Color.RED,
                org.bukkit.Color.YELLOW
        };

        int numColors = random.nextInt(3) + 1;
        java.util.List<org.bukkit.Color> effectColors = new java.util.ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            effectColors.add(colors[random.nextInt(colors.length)]);
        }

        org.bukkit.FireworkEffect effect = org.bukkit.FireworkEffect.builder()
                .with(type)
                .withColor(effectColors)
                .withFlicker()
                .withTrail()
                .build();

        meta.addEffect(effect);
        meta.setPower(random.nextInt(1, 3));
        firework.setFireworkMeta(meta);

        location.getWorld().playSound(location, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
    }

    public void changeGameMode(GameMode gameMode) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.hasPermission("jbuildbattle.bypass")) {
                player.setGameMode(gameMode);
            }
        });
    }

    public void teleportPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        if (players.isEmpty()) {
            return;
        }

        final List<Player> playerList = new ArrayList<>(players);
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
                    TeamType type = PlayerManager.getTeam(player);
                    Location spawnLocation = (type == TeamType.PROS) ? Arena.getSpawnPros() : Arena.getSpawnNoobs();

                    player.teleport(spawnLocation);
                }

                index[0]++;
            }
        }.runTaskTimer(JBuildBattle.getInstance(), 0L, 3L); // 3 ticks (0.15 segundos)
    }

    private void cancelGameTaskIfRunning() {
        VoteManager.forceStop();

        if (gameTask != null) {
            gameTask.cancel();
            gameTask = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private Location loadLocation(String basePath, World world) {
        double x = FileManager.getSettings().getDouble(basePath + ".x");
        double y = FileManager.getSettings().getDouble(basePath + ".y");
        double z = FileManager.getSettings().getDouble(basePath + ".z");
        return new Location(world, x, y, z);
    }

    private Cuboid loadRegion(String basePath, World world) {
        int x1 = FileManager.getSettings().getInt(basePath + ".pos1.x");
        int y1 = FileManager.getSettings().getInt(basePath + ".pos1.y");
        int z1 = FileManager.getSettings().getInt(basePath + ".pos1.z");
        int x2 = FileManager.getSettings().getInt(basePath + ".pos2.x");
        int y2 = FileManager.getSettings().getInt(basePath + ".pos2.y");
        int z2 = FileManager.getSettings().getInt(basePath + ".pos2.z");

        Location point1 = new Location(world, x1, y1, z1);
        Location point2 = new Location(world, x2, y2, z2);
        return new Cuboid(point1, point2);
    }

}
