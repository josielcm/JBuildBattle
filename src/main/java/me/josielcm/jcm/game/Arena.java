package me.josielcm.jcm.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import me.josielcm.jcm.api.regions.Cuboid;
import me.josielcm.jcm.player.PlayerManager;
import me.josielcm.jcm.player.TeamType;

public class Arena {
    
    @Getter
    @Setter
    private static Cuboid prosRegion;

    @Getter
    @Setter
    private static Cuboid noobsRegion;

    @Getter
    @Setter
    private static Location spawnPros;

    @Getter
    @Setter
    private static Location spawnNoobs;

    public static Cuboid getRegion(Player player) {
        TeamType team = PlayerManager.getTeam(player);

        if (team != null) {
            switch (team) {
                case PROS:
                    return getProsRegion();
                case NOOBS:
                    return getNoobsRegion();
                default:
                    return null;
            }
        }

        return null;
    }

    public static boolean isInRegion(Player player) {
        Cuboid region = getRegion(player);

        if (region != null) {
            return region.isIn(player);
        }

        return false;
    }

    public static boolean isInRegion(Location location) {
        if (getProsRegion() != null && getProsRegion().isIn(location)) {
            return true;
        }

        if (getNoobsRegion() != null && getNoobsRegion().isIn(location)) {
            return true;
        }

        return false;
    }

}
