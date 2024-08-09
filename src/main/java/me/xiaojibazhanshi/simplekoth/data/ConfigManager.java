package me.xiaojibazhanshi.simplekoth.data;

import lombok.Getter;
import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.objects.Hologram;
import me.xiaojibazhanshi.simplekoth.objects.KOTHRegion;
import me.xiaojibazhanshi.simplekoth.runnables.KOTHRegionRunnable;
import me.xiaojibazhanshi.simplekoth.utils.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private static FileConfiguration config;

    @Getter
    private static String setupPermission;

    @Getter
    private static String loserTitle;
    @Getter
    private static String winnerTitle;

    @Getter
    private static String noWinnerMsg;
    @Getter
    private static String winnerSelectedMsg;

    @Getter
    private static KOTHRegion kothRegion;
    @Getter
    private static Hologram hologram;

    public static void setup(SimpleKOTH main) {
        main.saveDefaultConfig();
        main.getConfig().options().copyDefaults(true);

        config = main.getConfig();
        initializeVariables();

        if (hologram.baseLocation() != null) {
            new KOTHRegionRunnable().start(main, hologram.baseLocation());
        }
    }

    public static void reload(SimpleKOTH main) {
        main.reloadConfig();
        setup(main);
    }

    private static void initializeVariables() {
        setupPermission = (String) Util.nullCheck("hologram-setup-permission", config);

        loserTitle = (String) Util.nullCheck("titles.loser", config);
        winnerTitle = (String) Util.nullCheck("titles.winner", config);

        noWinnerMsg = (String) Util.nullCheck("messages.winner-absent", config);
        winnerSelectedMsg = (String) Util.nullCheck("messages.winner-present", config);

        kothRegion = retrieveConfiguredKOTHRegion();
        hologram = retrieveConfiguredHologram();
    }

    private static KOTHRegion retrieveConfiguredKOTHRegion() {
        return new KOTHRegion
                ((String) Util.nullCheck("koth-region.wg-region-name", config),
                        (int) Util.nullCheck("koth-region.game-duration", config),
                        (int) Util.nullCheck("koth-region.point-limit", config));
    }

    @SuppressWarnings("unchecked")
    private static Hologram retrieveConfiguredHologram() {
        List<String> lines = (List<String>) Util.nullCheck("hologram.text", config);
        Location location = config.getLocation("hologram.location");

        return new Hologram(lines, location);
    }

    public static void setHologram(SimpleKOTH main, Location location) {
        hologram = new Hologram(hologram.hologramLines(), location);
        main.getConfig().set("hologram.location", location);
        main.saveConfig();
    }

    public static void clearHologram(SimpleKOTH main) {
        hologram = new Hologram(hologram.hologramLines(), null);
        main.getConfig().set("hologram.location", null);
        main.saveConfig();
    }

}
