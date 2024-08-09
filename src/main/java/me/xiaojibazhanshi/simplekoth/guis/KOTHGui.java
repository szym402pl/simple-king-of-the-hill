package me.xiaojibazhanshi.simplekoth.guis;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.data.ConfigManager;
import me.xiaojibazhanshi.simplekoth.runnables.KOTHRegionRunnable;
import me.xiaojibazhanshi.simplekoth.utils.StatFactory;
import me.xiaojibazhanshi.simplekoth.utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class KOTHGui {

    private static final int[] BUTTON_SLOTS = {11, 4, 15, 22};

    public static void openGUI(SimpleKOTH main, Player player, boolean hasSetupPerms) {
        Gui gui = Gui.gui()
                .title(Component.text(Util.color("&4&lKing Of The Hill &7- Main Page")))
                .rows(3).create();

        GuiItem stats = getStatsButton(player);
        GuiItem setup = getSetupButton(main, player, gui);
        GuiItem ranking = getRankingButton(player);
        GuiItem reload = getReloadButton(main, player, gui);

        gui.setItem(BUTTON_SLOTS[0], stats);
        if (hasSetupPerms) {
            gui.setItem(BUTTON_SLOTS[1], setup);
            gui.setItem(BUTTON_SLOTS[3], reload);
        }
        gui.setItem(BUTTON_SLOTS[2], ranking);

        gui.getFiller().fill(Util.getFiller());
        gui.open(player);
    }

    private static GuiItem getStatsButton(Player player) {

        return ItemBuilder.from(StatFactory.getPlayerStats(player.getUniqueId(), true))
                .asGuiItem(event -> event.setCancelled(true));
    }

    private static GuiItem getSetupButton(SimpleKOTH main, Player player, Gui guiToClose) {
        String isKOTHRunning = SimpleKOTH.activeKOTH != null
                ? Util.color("&cKOTH is running and will be terminated on click.")
                : Util.color("&aKOTH is not running and will be setup on click.");

        List<String> lore = Arrays.asList("",
                Util.color("&7Click me to setup/terminate &4KOTH&7."),
                Util.color("&7Selected &bWorldGuard &7region: &b" + ConfigManager.getKothRegion().wgRegionName()),
                "", isKOTHRunning);

        ItemStack item = Util.createItemStack
                (Material.ARMOR_STAND,
                        Util.color("&a&lSetup &4&lKOTH &a&l(&b&lHologram&a&l)"),
                        lore);

        return ItemBuilder.from(item).asGuiItem(event -> {
            event.setCancelled(true);
            boolean isKOTHActive = SimpleKOTH.activeKOTH != null;

            Player executor = (Player) event.getWhoClicked();
            executor.sendMessage(
                    (isKOTHActive ? Util.color("&4KOTH &a&otermination &r&awas successful!")
                            : Util.color("&4KOTH &a&osetup &r&awas successful!")));

            guiToClose.close(player);

            if (isKOTHActive) {
                SimpleKOTH.activeKOTH.stop(null, true, false);
                return;
            }

            KOTHRegionRunnable runnable = new KOTHRegionRunnable();
            runnable.start(main, player.getLocation());
        });
    }

    private static GuiItem getRankingButton(Player player) {
        List<String> lore = Arrays.asList("",
                Util.color("&7Click me to show the player ranking."),
                "",
                Util.color("&8NOTE: &7You must obtain at least &c1"),
                Util.color("&7of each stat in order to qualify."));

        ItemStack item = Util.createItemStack
                (Material.NETHERITE_SWORD,
                        Util.color("&a&lRanking"),
                        lore);

        return ItemBuilder.from(item).asGuiItem(event -> {
            event.setCancelled(true);

            RankingGui.openGUI(player, 0);
        });
    }

    private static GuiItem getReloadButton(SimpleKOTH main, Player player, Gui guiToBeClosed) {
        List<String> lore = Arrays.asList("",
                Util.color("&7Click me to reload the config."),
                "",
                Util.color("&8NOTE: &7Any errors &7that occur"),
                Util.color("&7will be visible in the console."));

        ItemStack item = Util.createItemStack
                (Material.BARRIER,
                        Util.color("&c&lRELOAD THE CONFIG"),
                        lore);

        return ItemBuilder.from(item).asGuiItem(event -> {
            event.setCancelled(true);
            guiToBeClosed.close(event.getWhoClicked());

            if (SimpleKOTH.activeKOTH != null) {
                player.sendMessage(Util.color("&cYou cannot reload the config right now!" +
                        "\n&cTerminate the current &4KOTH &cand try again."));
                return;
            }

            ConfigManager.reload(main);
            player.sendMessage(Util.color("&aConfig was reloaded successfully!"));
        });
    }


}
