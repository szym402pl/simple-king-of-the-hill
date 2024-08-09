package me.xiaojibazhanshi.simplekoth.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import me.xiaojibazhanshi.simplekoth.data.ConfigManager;
import me.xiaojibazhanshi.simplekoth.objects.Hologram;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static String translatePlaceholders(String string, Player winner) {
        return color(string.replace("{winner}", winner.getName()));
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static boolean isInKOTHRegion(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        if (regions == null) return false;

        ProtectedRegion region = regions.getRegion(ConfigManager.getKothRegion().wgRegionName());
        if (region == null) return false;

        Location playerLoc = player.getLocation();

        int x = playerLoc.getBlockX();
        int y = playerLoc.getBlockY();
        int z = playerLoc.getBlockZ();

        return region.contains(x, y, z);
    }

    public static Object nullCheck(String path, FileConfiguration config) {
        Object obj = config.get(path);

        if (obj == null) {
            Bukkit.getLogger().warning("WARNING!\n\n" +
                    "Config object is not set!" +
                    "\nPath: " + path + "\n\n" +
                    "WARNING!");
        }

        return obj;
    }

    public static void sendActionbar(Player player, String text) {
        TextComponent component = new TextComponent(color(text));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, component);
    }

    public static List<ArmorStand> spawnHologramInWorld(Hologram hologram, Location location) {
        List<ArmorStand> list = new ArrayList<>();
        Location spawnLoc = location.clone();

        for (int i = 0; i < hologram.hologramLines().size(); i++) {
            ArmorStand armorStand = spawnLoc.getWorld().spawn(spawnLoc, ArmorStand.class);

            int reverseNumber = (hologram.hologramLines().size() - i) - 1;
            armorStand.setCustomName(Util.color(Util.color(hologram.hologramLines().get(reverseNumber))));
            armorStand.setCustomNameVisible(true);

            armorStand.setInvulnerable(true);
            armorStand.setGravity(false);
            armorStand.setInvisible(true);

            list.add(armorStand);
            spawnLoc.add(0, 0.25, 0);
        }

        return list;
    }


    public static void updateHologram(List<ArmorStand> hologram, int timer) {
        int numberToReplace = timer + 1;
        String patternString = "(?<!\\d)" + numberToReplace + "(?!\\d)";

        for (ArmorStand line : hologram) {
            String customName = line.getCustomName();
            if (customName == null) {
                continue;
            }

            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(customName);

            String updatedName = matcher.replaceAll(String.valueOf(timer));

            line.setCustomName(updatedName.replace("{timer}", String.valueOf(timer)));
        }
    }

    public static List<ArmorStand> deleteHologramFromWorld(List<ArmorStand> hologram) {
        for (ArmorStand line : hologram) {
            line.remove();
        }

        return null;
    }

    public static ItemStack createItemStack(Material material, String displayName, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setLore(lore);
        meta.setDisplayName(displayName);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        item.setItemMeta(meta);

        return item;
    }

    public static GuiItem getFiller() {
        ItemStack cleanIS = Util.createItemStack(Material.GRAY_STAINED_GLASS_PANE, " ", List.of(" "));
        return ItemBuilder.from(cleanIS).asGuiItem(event -> event.setCancelled(true));
    }


}
