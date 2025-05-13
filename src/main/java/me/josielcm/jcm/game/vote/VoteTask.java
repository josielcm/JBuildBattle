package me.josielcm.jcm.game.vote;

import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.api.formats.Color;
import me.josielcm.jcm.api.formats.Format;
import me.josielcm.jcm.ui.BossBarManager;

public class VoteTask extends BukkitRunnable {

    @Getter
    @Setter
    private AtomicInteger time = new AtomicInteger(0);

    @Getter
    @Setter
    private String message = "";

    public VoteTask(int time, String message) {
        this.time.set(time);
        this.message = message;
    }

    @Override
    public void run() {
        if (time.get() <= 0) {
            VoteManager.stopVotes();
            cancel();
            return;
        }

        BossBarManager.updateText(Color.parse(message.replace("<time>", Format.formatTime(time.get()))));
        time.decrementAndGet();
    }
    
}
