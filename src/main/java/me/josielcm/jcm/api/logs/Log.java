package me.josielcm.jcm.api.logs;

import org.bukkit.Bukkit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import me.josielcm.jcm.JBuildBattle;

public class Log {
    
    private static JBuildBattle plugin = JBuildBattle.getInstance();
    private static String pluginName = "NONE";
    
    public enum LogLevel {
        INFO("<gold>"),
        WARNING("<yellow>"),
        ERROR("<red>"),
        SUCCESS("<gold>"),
        DEBUG("<aqua>");
        
        private final String color;
        
        LogLevel(String color) {
            this.color = color;
        }
        
        public String getColor() {
            return color;
        }
    }

    public static void onLoad() {
        pluginName = plugin.getPluginMeta().getName();
        logHeader(LogLevel.INFO, "Loading " + pluginName + "...");
    }

    public static void onEnable() {
        logPluginStatus(LogLevel.SUCCESS, pluginName + " loaded successfully!");
        logFooter();
    }

    public static void onReload() {
        logHeader(LogLevel.INFO, "Reloading " + pluginName + "...");
        logFooter();
    }

    public static void onDisable() {
        logHeader(LogLevel.INFO, pluginName + " is shutting down...");
        logFooter();
    }
    
    /**
     * Envía un mensaje de log con el nivel especificado
     */
    public static void log(LogLevel level, String message) {
        Bukkit.getConsoleSender().sendRichMessage(level.getColor() + message);
    }
    
    /**
     * Crea un encabezado para mensajes importantes
     */
    public static void logHeader(LogLevel level, String title) {
        String separator = "-".repeat(50);
        
        Bukkit.getConsoleSender().sendRichMessage("");
        Bukkit.getConsoleSender().sendRichMessage(level.getColor() + separator);
        Bukkit.getConsoleSender().sendRichMessage(level.getColor() + "  " + title);
        Bukkit.getConsoleSender().sendRichMessage(level.getColor() + separator);
    }
    
    /**
     * Muestra los detalles de estado del plugin
     */
    public static void logPluginStatus(LogLevel level, String title) {
        logHeader(level, title);
    }
    
    /**
     * Agrega un pie de página estándar a los mensajes de log
     */
    public static void logFooter() {
        String separator = "─".repeat(50);
        
        Bukkit.getConsoleSender().sendRichMessage("<gold>" + separator);
        Bukkit.getConsoleSender().sendRichMessage("<gold>Dev JosielCM");
        Bukkit.getConsoleSender().sendRichMessage("");
    }
    
    /**
     * Registra errores con información de timestamp
     */
    public static void logError(String errorMessage, Exception e) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = LocalDateTime.now().format(formatter);
        
        logHeader(LogLevel.ERROR, "Error Detected");
        log(LogLevel.ERROR, "Time: " + timestamp);
        log(LogLevel.ERROR, "Message: " + errorMessage);
        
        if (e != null) {
            log(LogLevel.ERROR, "Exception: " + e.getClass().getName());
            log(LogLevel.ERROR, "Cause: " + e.getMessage());
            log(LogLevel.ERROR, "Stack Trace:");
            for (StackTraceElement element : e.getStackTrace()) {
                log(LogLevel.ERROR, "  at " + element.toString());
            }
        }
        
        Bukkit.getConsoleSender().sendRichMessage("<gray>Check logs for full stack trace.");
    }
}