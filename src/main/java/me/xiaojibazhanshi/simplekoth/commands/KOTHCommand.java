package me.xiaojibazhanshi.simplekoth.commands;

import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.data.ConfigManager;
import me.xiaojibazhanshi.simplekoth.guis.KOTHGui;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KOTHCommand implements CommandExecutor {

    SimpleKOTH main;

    public KOTHCommand(SimpleKOTH main) {
        this.main = main;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().info("Only a player can execute this command!");
            return true;
        }

        boolean hasPerms = player.hasPermission(ConfigManager.getSetupPermission());
        KOTHGui.openGUI(main, player, hasPerms);

        return true;
    }
}
