package me.xiaojibazhanshi.simplekoth.guis;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.xiaojibazhanshi.simplekoth.utils.StatFactory;
import me.xiaojibazhanshi.simplekoth.utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class RankingGui {

    public static void openGUI(Player player, int sortingMethod) {
        PaginatedGui paginatedGui = Gui.paginated()
                .title(Component.text(Util.color("&4&lKing Of The Hill &7- Ranking")))
                .rows(6)
                .pageSize(45)
                .create();

        GuiItem sorting = getSortingButton(player, sortingMethod);
        GuiItem filler = Util.getFiller();

        paginatedGui.setItem(6, 5, sorting);

        paginatedGui.setItem(6, 3, ItemBuilder.from(Material.PAPER)
                .setName(Util.color("&a&lPrevious page"))
                .setLore(Util.color("&7Current page: &a" + paginatedGui.getCurrentPageNum())).asGuiItem(event -> {
                    event.setCancelled(true);
                    paginatedGui.previous();
                }));

        paginatedGui.setItem(6, 7, ItemBuilder.from(Material.PAPER).
                setName(Util.color("&a&lNext page"))
                .setLore(Util.color("&7Current page: &a" + paginatedGui.getCurrentPageNum())).asGuiItem(event -> {
                    event.setCancelled(true);
                    paginatedGui.next();
                }));

        for (int slot : new int[]{1, 2, 4, 6, 8, 9}) {
            paginatedGui.setItem(6, slot, filler);
        }

        List<GuiItem> rankingList = StatFactory.generateRankingList(sortingMethod);
        for (GuiItem rankingItem : rankingList) {
            paginatedGui.addItem(rankingItem);
        }

        paginatedGui.open(player);
    }

    private static GuiItem getSortingButton(Player player, int sortedBy) {
        String[] sort = {"&aLifetime Wins", "&cLifetime Losses", "&eLifetime Points", "&bAverage Points"};

        List<String> lore = Arrays.asList(
                Util.color("&7Click me to change the sorting method."),
                Util.color("&7Currently sorting by: " + sort[sortedBy]));

        ItemStack item = Util.createItemStack
                (Material.HOPPER,
                        Util.color("&a&lSorting"),
                        lore);

        return ItemBuilder.from(item).asGuiItem(event -> {
            int nextSort = sortedBy + 1;
            if (nextSort > 3) {
                nextSort = 0;
            }

            event.setCancelled(true);
            openGUI(player, nextSort);
        });
    }


}
