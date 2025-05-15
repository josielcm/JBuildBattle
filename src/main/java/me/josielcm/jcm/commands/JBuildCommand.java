package me.josielcm.jcm.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.player.TeamType;

@CommandAlias("jbuild")
@CommandPermission("jbuild.admin")
public class JBuildCommand extends BaseCommand {

    @Subcommand("start")
    public void onStartCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.start")) {
            JBuildBattle.getInstance().getGameManager().start();
            sender.sendMessage(Color.parse("<green>JBuild started!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("stop")
    public void onStopCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.stop")) {
            JBuildBattle.getInstance().getGameManager().stop();
            sender.sendMessage(Color.parse("<red>JBuild stopped!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("reset")
    public void onResetCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.reset")) {
            JBuildBattle.getInstance().getGameManager().reset();
            sender.sendMessage(Color.parse("<yellow>JBuild reset!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("winner")
    public void onWinnerCommand(CommandSender sender, String type) {
        if (sender.hasPermission("jbuild.winner")) {
            if (type.equalsIgnoreCase("pros") || type.equalsIgnoreCase("noobs")) {

                JBuildBattle.getInstance().getGameManager().win(TeamType.valueOf(type.toUpperCase()));
                sender.sendMessage(Color.parse("<green>Winner set to " + type + "!"));
            } else {
                sender.sendMessage(Color.parse("<red>Invalid type! Use 'pros' or 'noobs'."));
            }
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("glow")
    public void onGlowCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.glow")) {
            JBuildBattle.getInstance().getGameManager().toggleGlow();
            sender.sendMessage(Color.parse("<yellow>Glow toggled!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("addtime")
    public void onAddTimeCommand(CommandSender sender, int time) {
        if (sender.hasPermission("jbuild.addtime")) {
            JBuildBattle.getInstance().getGameManager().addTime(time);
            sender.sendMessage(Color.parse("<green>Added " + time + " seconds!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("removetime")
    public void onRemoveTimeCommand(CommandSender sender, int time) {
        if (sender.hasPermission("jbuild.removetime")) {
            JBuildBattle.getInstance().getGameManager().removeTime(time);
            sender.sendMessage(Color.parse("<red>Removed " + time + " seconds!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("settime")
    public void onSetTimeCommand(CommandSender sender, int time) {
        if (sender.hasPermission("jbuild.settime")) {
            JBuildBattle.getInstance().getGameManager().setTime(time);
            sender.sendMessage(Color.parse("<yellow>Set time to " + time + " seconds!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @Subcommand("toggleall")
    public void onToggleAllCommand(CommandSender sender) {
        if (sender.hasPermission("jbuild.toggleall")) {
            JBuildBattle.getInstance().getGameManager().toggleAll();
            sender.sendMessage(Color.parse("<green>All events toggled!"));
        } else {
            sender.sendMessage(Color.parse("<red>You don't have permission to use this command."));
        }
    }

    @CatchUnknown
    public void onUnknownCommand(CommandSender sender) {
        sender.sendMessage(Color.parse("<gold><bold>----- Comandos JBuild Battle -----</bold>"));
        sender.sendMessage(Color.parse("<yellow>» <green>/jbuild start <yellow>- Inicia el juego"));
        sender.sendMessage(Color.parse("<yellow>» <red>/jbuild stop <yellow>- Detiene el juego"));
        sender.sendMessage(Color.parse("<yellow>» <gold>/jbuild reset <yellow>- Reinicia el juego"));
        sender.sendMessage(Color.parse(""));
        sender.sendMessage(Color.parse("<gold><bold>----- Tiempo -----</bold>"));
        sender.sendMessage(Color.parse("<yellow>» <green>/jbuild addtime <time> <yellow>- Añade tiempo"));
        sender.sendMessage(Color.parse("<yellow>» <red>/jbuild removetime <time> <yellow>- Quita tiempo"));
        sender.sendMessage(Color.parse("<yellow>» <aqua>/jbuild settime <time> <yellow>- Establece el tiempo"));
        sender.sendMessage(Color.parse(""));
        sender.sendMessage(Color.parse("<gold><bold>----- Extras -----</bold>"));
        sender.sendMessage(Color.parse("<yellow>» <light_purple>/jbuild glow <yellow>- Activa/desactiva el brillo"));
        sender.sendMessage(Color.parse("<yellow>» <gold>/jbuild winner <pros|noobs> <yellow>- Establece el ganador"));
        sender.sendMessage(Color.parse("<yellow>» <gold>/jbuild toggleall <yellow>- Habilitar todos los eventos"));
    }

    @HelpCommand
    public void onHelpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
