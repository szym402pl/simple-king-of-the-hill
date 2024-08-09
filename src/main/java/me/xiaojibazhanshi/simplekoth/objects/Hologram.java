package me.xiaojibazhanshi.simplekoth.objects;

import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.List;

public record Hologram(List<String> hologramLines, @Nullable Location baseLocation) {
}
