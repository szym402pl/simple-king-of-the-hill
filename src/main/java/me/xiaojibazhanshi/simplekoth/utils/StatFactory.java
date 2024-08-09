package me.xiaojibazhanshi.simplekoth.utils;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.data.PlayerDataManager;
import me.xiaojibazhanshi.simplekoth.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatFactory {

    private static final PlayerDataManager manager = SimpleKOTH.getPlayerDataManager();

    /**
     * @param sortedBy The sorting criteria:
     *                                <ul>
     *                                  <li>1 or default: Sorted by wins</li>
     *                                  <li>2: Sorted by losses</li>
     *                                  <li>3: Sorted by points</li>
     *                                  <li>4: Sorted by average points</li>
     *                                </ul>
     *                 @return A list of `ItemStack` objects representing the player rankings.
     */
    public static List<GuiItem> generateRankingList(int sortedBy) {
        SortedSet<PlayerData> dataSet;

        switch (sortedBy) {
            case 1 -> dataSet = manager.getPDSortedByLosses();
            case 2 -> dataSet = manager.getPDSortedByPoints();
            case 3 -> dataSet = manager.getPDSortedByAvgPoints();
            default -> dataSet = manager.getPDSortedByWins();
        }

        return dataSet.stream()
                .filter(StatFactory::hasStats)
                .map(playerData -> getPlayerStats(playerData.getUuid(), false))
                .map(itemStack -> ItemBuilder.from(itemStack).asGuiItem(event -> event.setCancelled(true)))
                .collect(Collectors.toList());
    }

    private static boolean hasStats(PlayerData data) {
        return (data.getGlobalPoints() > 0) && (data.getWins() > 0) && (data.getLosses() > 0);
    }

    public static ItemStack getPlayerStats(UUID uuid, boolean ownStats) {
        PlayerData data = PlayerDataManager.getPlayerDataByUUID(uuid);
        Player player = Bukkit.getPlayer(uuid);

        ItemStack stats = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta statsMeta = (SkullMeta) stats.getItemMeta();

        assert statsMeta != null;
        assert player != null;

        statsMeta.setDisplayName(Util.color("&a&l" + player.getName() + "&7's stats:"));
        if (ownStats) statsMeta.setDisplayName(Util.color("&a&lYour stats"));

        statsMeta.setOwningPlayer(player);
        statsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<String> lore = new ArrayList<>();

        lore.add("");
        lore.add(Util.color("&7Lifetime Wins: &a" + data.getWins()));
        lore.add(Util.color("&7Lifetime Losses: &c") + data.getLosses());
        lore.add(Util.color("&7Lifetime Points: &e") + data.getGlobalPoints());
        lore.add(Util.color("&7Average Points: &b") + data.getAvgPoints());

        statsMeta.setLore(lore);
        stats.setItemMeta(statsMeta);

        return stats;
    }

}
