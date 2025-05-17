package me.josielcm.jcm.commands;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.listener.PlayerListener;
import me.josielcm.jcm.player.TeamType;

@CommandAlias("jbuild")
public class JBuildCommand extends BaseCommand {

    private static final String PREFIX = "<gradient:#FCD46D:#FCD369:#FCD265:#FCD160:#FCD05C:#FCD160:#FCD265><b>zEvento</b> <grey>»</grey> ";
    private static final String NO_PERMISSION = "<red>¡No tienes permiso para usar este comando!</red>";

    @Subcommand("start")
    @CommandPermission("jbuild.cmd")
    public void onStartCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.start")) {
            JBuildBattle.getInstance().getGameManager().start();
            sender.sendMessage(Color.parse("<green>JBuild started!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("stop")
    @CommandPermission("jbuild.cmd")
    public void onStopCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.stop")) {
            JBuildBattle.getInstance().getGameManager().stop();
            sender.sendMessage(Color.parse("<red>JBuild stopped!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("reset")
    @CommandPermission("jbuild.cmd")
    public void onResetCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.reset")) {
            JBuildBattle.getInstance().getGameManager().reset();
            sender.sendMessage(Color.parse("<yellow>JBuild reset!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("winner")
    @CommandPermission("jbuild.cmd")
    public void onWinnerCommand(CommandSender sender, String type) {
        if (sender.hasPermission("jbuild.winner")) {
            if (type.equalsIgnoreCase("pros") || type.equalsIgnoreCase("noobs")) {

                JBuildBattle.getInstance().getGameManager().win(TeamType.valueOf(type.toUpperCase()));
                sender.sendMessage(Color.parse("<green>Winner set to " + type + "!", PREFIX));
            } else {
                sender.sendMessage(Color.parse("<red>Invalid type! Use 'pros' or 'noobs'.", PREFIX));
            }
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("glow")
    @CommandPermission("jbuild.cmd")
    public void onGlowCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.glow")) {
            JBuildBattle.getInstance().getGameManager().toggleGlow();
            sender.sendMessage(Color.parse("<yellow>Glow toggled!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("addtime")
    @CommandPermission("jbuild.cmd")
    public void onAddTimeCommand(CommandSender sender, int time) {
        if (sender.hasPermission("jbuild.addtime")) {
            JBuildBattle.getInstance().getGameManager().addTime(time);
            sender.sendMessage(Color.parse("<green>Added " + time + " seconds!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("removetime")
    @CommandPermission("jbuild.cmd")
    public void onRemoveTimeCommand(CommandSender sender, int time) {
        if (sender.hasPermission("jbuild.removetime")) {
            JBuildBattle.getInstance().getGameManager().removeTime(time);
            sender.sendMessage(Color.parse("<red>Removed " + time + " seconds!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("settime")
    @CommandPermission("jbuild.cmd")
    public void onSetTimeCommand(CommandSender sender, int time) {
        if (sender.hasPermission("jbuild.settime")) {
            JBuildBattle.getInstance().getGameManager().setTime(time);
            sender.sendMessage(Color.parse("<yellow>Set time to " + time + " seconds!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("toggleall")
    @CommandPermission("jbuild.cmd")
    public void onToggleAllCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.toggleall")) {
            JBuildBattle.getInstance().getGameManager().toggleAll();
            sender.sendMessage(Color.parse("<green>All events toggled!", PREFIX));
        } else {
            sender.sendMessage(Color.parse(NO_PERMISSION, PREFIX));
        }
    }

    @Subcommand("mute")
    @CommandPermission("jbuild.admin.mute")
    public void onMute(CommandSender sender, String playerName) {
        Player target = Bukkit.getPlayerExact(playerName);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(Color.parse("<red>¡El jugador no existe o no está conectado!</red>", PREFIX));
            return;
        }

        if (PlayerListener.getMutedPlayers().contains(target.getUniqueId())) {
            PlayerListener.getMutedPlayers().remove(target.getUniqueId());
            sender.sendMessage(Color.parse("<green>¡Se ha removido el silencio al jugador!</green>", PREFIX));
            target.sendMessage(Color.parse("<green>¡La restricción del chat ha sido removida!</green>", PREFIX));
            return;
        }

        PlayerListener.getMutedPlayers().add(target.getUniqueId());
        target.sendMessage(Color.parse("<red>¡Un moderador te ha silenciado!</red>", PREFIX));
        sender.sendMessage(Color.parse("<green>¡El jugador " + target.getName() + " ha sido silenciado!</green>", PREFIX));
    }

    @CommandAlias("reportar")
    public void onReport(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(Color.parse("<red>¡Uso correcto: /reportar <mensaje>!</red>", PREFIX));
            return;
        }

        if (isOnCooldown(player)) {
            player.sendMessage(Color.parse("<red>¡Tienes que esperar para mandar otro reporte!</red>", PREFIX));
            return;
        }

        String message = String.join(" ", args);
        String reportMessage = "<red><b>REPORTE</b></red> <grey>»</grey> " +
                             "<yellow>" + player.getName() + "</yellow> " +
                             "<grey>reportó:</grey> " + message;

        Bukkit.broadcast(Color.parse(reportMessage), "jbuild.admin.mute");
        PlayerListener.getCooldownCommand().put(player.getUniqueId(), System.currentTimeMillis());
        player.sendMessage(Color.parse("<green>¡Tu reporte ha sido enviado al staff!</green>", PREFIX));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
    }

    @CatchUnknown
    public void onUnknownCommand(CommandSender sender) {
        sender.sendMessage(Color.parse("<gold><b>----- Comandos JBuild Battle -----</b>"));
        sender.sendMessage(Color.parse("<yellow>» <green>/jbuild start</green> <yellow>- Inicia el juego</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <red>/jbuild stop</red> <yellow>- Detiene el juego</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <gold>/jbuild reset</gold> <yellow>- Reinicia el juego</yellow>"));
        sender.sendMessage("");
        sender.sendMessage(Color.parse("<gold><b>----- Tiempo -----</b>"));
        sender.sendMessage(Color.parse("<yellow>» <green>/jbuild addtime <tiempo></green> <yellow>- Añade tiempo</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <red>/jbuild removetime <tiempo></red> <yellow>- Quita tiempo</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <aqua>/jbuild settime <tiempo></aqua> <yellow>- Establece el tiempo</yellow>"));
        sender.sendMessage("");
        sender.sendMessage(Color.parse("<gold><b>----- Extras -----</b>"));
        sender.sendMessage(Color.parse("<yellow>» <light_purple>/jbuild glow</light_purple> <yellow>- Activa/desactiva el brillo</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <gold>/jbuild winner <pros|noobs></gold> <yellow>- Establece el ganador</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <gold>/jbuild toggleall</gold> <yellow>- Habilita todos los eventos</yellow>"));
        sender.sendMessage("");
        sender.sendMessage(Color.parse("<light_purple><b>----- Moderación -----</b>"));
        sender.sendMessage(Color.parse("<yellow>» <light_purple>/jbuild mute <jugador></light_purple> <yellow>- Silencia/Desilencia a un jugador</yellow>"));
        sender.sendMessage(Color.parse("<yellow>» <light_purple>/reportar <mensaje></light_purple> <yellow>- Envía un reporte al staff</yellow>"));
    }

    private boolean isOnCooldown(Player player) {
        if (!PlayerListener.getCooldownCommand().containsKey(player.getUniqueId())) {
            return false;
        }

        long lastMessageTime = PlayerListener.getCooldownCommand().get(player.getUniqueId());
        long currentTime = System.currentTimeMillis();

        return (currentTime - lastMessageTime) < 5000;
    }

    @HelpCommand
    public void onHelpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
