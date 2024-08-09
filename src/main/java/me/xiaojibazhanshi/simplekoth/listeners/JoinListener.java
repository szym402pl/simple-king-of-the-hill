package me.xiaojibazhanshi.simplekoth.listeners;

import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.data.PlayerDataManager;
import me.xiaojibazhanshi.simplekoth.objects.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerDataManager manager = SimpleKOTH.getPlayerDataManager();
        UUID uuid = event.getPlayer().getUniqueId();

        if (!PlayerDataManager.playerDataSet.contains(manager.getPlayerDataByUUID(uuid))) {
            PlayerData cleanData = new PlayerData(uuid, 0, 0, 0);
            manager.overridePlayerData(PlayerDataManager.playerDataSet, cleanData);
        }
    }

}
