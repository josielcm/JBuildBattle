package me.josielcm.jcm.api;

import org.bukkit.NamespacedKey;

import lombok.Getter;
import me.josielcm.jcm.JBuildBattle;

public class Key {
    
    @Getter
    private static NamespacedKey selectorItemsKey = null;

    @Getter
    private static NamespacedKey voteOptionsItemsKey = null;

    public static void instanceKeys() {
        selectorItemsKey = new NamespacedKey(JBuildBattle.getInstance(), "selectorItemsKey");
        voteOptionsItemsKey = new NamespacedKey(JBuildBattle.getInstance(), "voteOptionsItemsKey");
    }

}
