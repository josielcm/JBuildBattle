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
        Cuboid region = null;

        if (team != null) {
            switch (team) {
                case PROS:
                    region = getProsRegion();
                    break;
                case NOOBS:
                    region = getNoobsRegion();
                    break;
            }
        }

        return region;
    }

    public static boolean isInRegion(Player player) {
        Cuboid region = getRegion(player);

        if (region != null) {
            boolean isIn = region.isIn(player);
            return isIn;
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
