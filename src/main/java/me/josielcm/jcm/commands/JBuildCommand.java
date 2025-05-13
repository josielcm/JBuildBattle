package me.josielcm.jcm.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.player.TeamType;

@CommandAlias("jbuild")
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

    @CatchUnknown
    public void onUnknownCommand(CommandSender sender) {
        sender.sendMessage(Color.parse("<gold>Sos pelotudo o que se te olvidó el comando? xd"));
        sender.sendMessage(Color.parse("<gold>Usa /jbuild start/stop/reset/winner(type=pros/noobs)"));
    }

    @HelpCommand
    public void onHelpCommand(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
