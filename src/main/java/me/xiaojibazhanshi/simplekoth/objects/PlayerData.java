package me.xiaojibazhanshi.simplekoth.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class PlayerData {

    @Getter
    @Setter
    private UUID uuid;

    @Getter
    @Setter
    private int wins;

    @Getter
    @Setter
    private int losses;

    @Getter
    @Setter
    private int globalPoints;

    @Getter
    @Setter
    private double avgPoints;

    public PlayerData(UUID uuid, int wins, int losses, int globalPoints) {
        this.uuid = uuid;
        this.wins = wins;
        this.losses = losses;
        this.globalPoints = globalPoints;

        if (wins + losses > 0) {
            this.avgPoints = Math.round((double) globalPoints / (wins + losses));
        } else {
            this.avgPoints = globalPoints;
        }
    }
}
