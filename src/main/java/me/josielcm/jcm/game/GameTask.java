package me.josielcm.jcm.game;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.JBuildBattle;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.api.formats.Format;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.ui.BossBarManager;

public class GameTask extends BukkitRunnable {

    @Getter
    private AtomicInteger time = new AtomicInteger(0);

    @Getter
    @Setter
    private String message = "";

    public GameTask(int time) {
        this.time.set(time);
    }

    @Override
    public void run() {
        if (time.get() <= 0) {
            JBuildBattle.getInstance().getGameManager().stop();
            BossBarManager.removeAllPlayers();
            cancel();
            return;
        }

        if (time.get() == 10) {
            BossBarManager.updateText(Color.parse("<gold><b>Esperando</b></gold>"));
            BossBarManager.removeAllPlayers();
        }

        switch (time.get()) {
            case 10:
                PlayerManager.sendTitle("<color:#37ff30><b>10", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 9:
                PlayerManager.sendTitle("<color:#ff7032><b>9", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 8:
                PlayerManager.sendTitle("<color:#ffa02d><b>8", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 7:
                PlayerManager.sendTitle("<color:#ffd027><b>7", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 6:
                PlayerManager.sendTitle("<color:#fff12e><b>6", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 5:
                PlayerManager.sendTitle("<color:#d5f940><b>5", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 4:
                PlayerManager.sendTitle("<color:#abf252><b>4", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);
                break;
            case 3:
                PlayerManager.sendTitle("<color:#81ea63><b>3", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.1f);
                break;
            case 2:
                PlayerManager.sendTitle("<color:#57e275><b>2", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 0.1f);
                break;
            case 1:
                PlayerManager.sendTitle("<color:#37ff30><b>1", "", 0, 2, 0);
                PlayerManager.playSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.1f);
                break;
            default:
                break;
        }

        BossBarManager.updateText(Color.parse(message.replace("<time>", Format.formatTime(time.get()))
                .replace("<theme>", JBuildBattle.getInstance().getGameManager().getGameTheme().getName())));
        time.decrementAndGet();
    }

    public void addTime(int time) {
        this.time.addAndGet(time);
    }

    public void removeTime(int time) {
        this.time.addAndGet(-time);
    }

    public void setTime(int time) {
        this.time.set(time);
    }

}
