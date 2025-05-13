package me.josielcm.jcm;

import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.api.Key;
import me.josielcm.jcm.api.logs.Log;
import me.josielcm.jcm.commands.JBuildCommand;
import me.josielcm.jcm.game.GameManager;
import me.josielcm.jcm.listener.GameListener;
import me.josielcm.jcm.listener.PlayerListener;
import me.josielcm.jcm.listener.VoteListener;

import java.util.Arrays;
import java.util.List;

public final class JBuildBattle extends JavaPlugin {

    @Getter
    private static JBuildBattle instance;

    @Getter
    private GameManager gameManager;

    private PaperCommandManager commandManager;

    @Getter
    @Setter
    private boolean debug = false;

    @Override
    public void onLoad() {
        instance = this;
        Log.onLoad();
    }

    @Override
    public void onEnable() {
        Key.instanceKeys();
        FileManager.loadFiles();
        FileManager.debug();

        gameManager = new GameManager();

        setupCommands();
        registerListeners();

        Log.onEnable();
    }

    @Override
    public void onDisable() {
        if (commandManager != null) {
            commandManager.unregisterCommands();
        }

        Log.onDisable();
    }

    public void reload() {
        FileManager.reload();
        FileManager.debug();
        if (commandManager != null) {
            commandManager.unregisterCommands();
        }
        setupCommands();
        Log.onReload();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new VoteListener(), this);
    }

    private void setupCommands() {
        commandManager = new PaperCommandManager(this);

        // Register all commands at once
        List<BaseCommand> commands = Arrays.asList(
                new JBuildCommand());

        for (BaseCommand command : commands) {
            commandManager.registerCommand(command);
        }
    }
}