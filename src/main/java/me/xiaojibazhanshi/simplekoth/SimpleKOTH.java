package me.xiaojibazhanshi.simplekoth;

import lombok.Getter;
import me.xiaojibazhanshi.simplekoth.commands.KOTHCommand;
import me.xiaojibazhanshi.simplekoth.data.ConfigManager;
import me.xiaojibazhanshi.simplekoth.data.PlayerDataManager;
import me.xiaojibazhanshi.simplekoth.listeners.JoinListener;
import me.xiaojibazhanshi.simplekoth.runnables.KOTHRegionRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class SimpleKOTH extends JavaPlugin {

    @Getter
    private static SimpleKOTH instance;

    @Getter
    private static PlayerDataManager playerDataManager;
    public static KOTHRegionRunnable activeKOTH;
    public static List<ArmorStand> activeHologramLines;

    @Override
    public void onEnable() {
        instance = this;

        ConfigManager.setup(instance);
        playerDataManager = new PlayerDataManager(instance);

        getCommand("koth").setExecutor(new KOTHCommand(instance));
        Bukkit.getPluginManager().registerEvents(new JoinListener(), instance);
    }

    @Override
    public void onDisable() {
        activeKOTH.stop(null, true, true);
        playerDataManager.saveData();
    }
}
