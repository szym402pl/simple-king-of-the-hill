package me.xiaojibazhanshi.simplekoth.runnables;

import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.data.ConfigManager;
import me.xiaojibazhanshi.simplekoth.data.PlayerDataManager;
import me.xiaojibazhanshi.simplekoth.objects.PlayerData;
import me.xiaojibazhanshi.simplekoth.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class KOTHRegionRunnable extends BukkitRunnable {

    PlayerDataManager manager;
    private int timer;
    private int pointLimit;
    private HashMap<UUID, Integer> playersAndAddedPoints;
    private Location persistentBaseLocation;

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return;
        }

        if (timer <= 0) {
            stop(null, false, false);
            return;
        }

        Util.updateHologram(SimpleKOTH.activeHologramLines, timer);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Util.isInKOTHRegion(player) && !playersAndAddedPoints.containsKey(player.getUniqueId())) {
                continue;
            }

            if (timer <= 10) {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
                player.sendTitle("", ChatColor.RED + "" + timer, 3, 20, 3);
            }

            if (!Util.isInKOTHRegion(player)) {
                Util.sendActionbar(player, "&cYou've left the &4&lKOTH &carea!    &cRush back if you don't want to lose!");
                continue;
            }

            addPoint(player);
        }

        timer--;
    }

    public void start(SimpleKOTH main, Location baseLocation) {
        manager = SimpleKOTH.getPlayerDataManager();
        SimpleKOTH.activeKOTH = this;
        SimpleKOTH.activeHologramLines = Util.spawnHologramInWorld(ConfigManager.getHologram(), baseLocation);
        ConfigManager.setHologram(main, baseLocation);

        timer = ConfigManager.getKothRegion().gameDuration();
        pointLimit = ConfigManager.getKothRegion().pointLimit();
        playersAndAddedPoints = new HashMap<>();
        persistentBaseLocation = baseLocation;

        runTaskTimer(main, 0, 20);
    }

    public void stop(@Nullable Player winner, boolean forever, boolean restartOrReload) {
        SimpleKOTH.activeKOTH = null;
        SimpleKOTH.activeHologramLines = Util.deleteHologramFromWorld(SimpleKOTH.activeHologramLines);

        if (!restartOrReload)
            ConfigManager.clearHologram(SimpleKOTH.getInstance());

        handleGameEnd(winner, forever);
    }

    private void handleGameEnd(@Nullable Player winner, boolean forever) {
        cancel();
        boolean isThereAWinner = winner != null;

        for (UUID key : playersAndAddedPoints.keySet()) {
            PlayerData playerData = PlayerDataManager.getPlayerDataByUUID(key);
            Player player = Bukkit.getPlayer(key);

            if (player == null) continue;

            Util.sendActionbar(player, " ");

            boolean isTheWinner = isThereAWinner && key.equals(winner.getUniqueId());

            int points = playerData.getGlobalPoints() + playersAndAddedPoints.get(key);
            int losses = isTheWinner ? playerData.getLosses() : playerData.getLosses() + 1;
            int wins = isTheWinner ? playerData.getWins() + 1 : playerData.getWins();

            PlayerData newPlayerData = new PlayerData(key, wins, losses, points);
            manager.overridePlayerData(PlayerDataManager.playerDataSet, newPlayerData);

            String title = isTheWinner
                    ? Util.color(ConfigManager.getWinnerTitle())
                    : Util.color(ConfigManager.getLoserTitle());

            Sound sound = isTheWinner ? Sound.ENTITY_PLAYER_LEVELUP : Sound.ENTITY_VILLAGER_NO;

            player.playSound(player.getLocation(), sound, 1F, 1F);
            player.sendTitle("", title, 15, 20, 15);
        }

        String message = isThereAWinner
                ? Util.translatePlaceholders(Util.color(ConfigManager.getWinnerSelectedMsg()), winner)
                : Util.color(ConfigManager.getNoWinnerMsg());

        Bukkit.broadcastMessage("\n" + message + "\n");

        if (!forever) {
            KOTHRegionRunnable runnable = new KOTHRegionRunnable();
            runnable.start(SimpleKOTH.getInstance(), persistentBaseLocation);
        }
    }

    private void addPoint(Player player) {
        UUID uuid = player.getUniqueId();

        if (!playersAndAddedPoints.containsKey(uuid)) {
            playersAndAddedPoints.put(uuid, 0);
            return;
        }

        playersAndAddedPoints.put(player.getUniqueId(), playersAndAddedPoints.get(uuid) + 1);

        if (pointLimit - playersAndAddedPoints.get(uuid) <= 10) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1F, 1F);
        }

        if (playersAndAddedPoints.get(uuid) >= pointLimit) {
            stop(player, false, false);
            return;
        }

        Util.sendActionbar(player, "&a+&e1 &apoint &7| &aGet &e"
                + (pointLimit - playersAndAddedPoints.get(uuid)) + "&a more to win");
    }
}
