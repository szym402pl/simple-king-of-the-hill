package me.xiaojibazhanshi.simplekoth.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.xiaojibazhanshi.simplekoth.SimpleKOTH;
import me.xiaojibazhanshi.simplekoth.objects.PlayerData;
import org.bukkit.Bukkit;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerDataManager {

    public static Set<PlayerData> playerDataSet;
    private File file;
    private Gson gson;

    public PlayerDataManager(SimpleKOTH main) {
        setup(main);
    }

    private void setup(SimpleKOTH main) {
        playerDataSet = new HashSet<>();

        file = new File(main.getDataFolder(), "playerdata.json");
        gson = new Gson();

        if (!file.exists()) {
            try {
                file.createNewFile();
                saveData();
            } catch (IOException ex) {
                Bukkit.getLogger().warning("There was an exception while creating the player data file!");
            }
        } else {
            loadData();
        }
    }

    private void loadData() {
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<Set<PlayerData>>() {
            }.getType();
            playerDataSet = gson.fromJson(reader, type);
            if (playerDataSet == null) {
                playerDataSet = new HashSet<>();
            }
        } catch (IOException ex) {
            Bukkit.getLogger().warning("There was an exception while loading player data!");
        }
    }

    public void saveData() {
        try (Writer writer = new FileWriter(file, false)) {
            gson.toJson(playerDataSet, writer);
        } catch (IOException ex) {
            Bukkit.getLogger().warning("There was an exception while saving player data!");
        }
    }

    public void overridePlayerData(Set<PlayerData> dataSet, PlayerData newData) {
        dataSet.removeIf(playerData -> playerData.getUuid().equals(newData.getUuid()));
        dataSet.add(newData);
        saveData();
    }

    public PlayerData getPlayerDataByUUID(UUID uuid) {
        Optional<PlayerData> selectedData = playerDataSet
                .stream()
                .filter(data -> data.getUuid().equals(uuid))
                .findFirst();

        return selectedData.orElse(null);
    }

    public SortedSet<PlayerData> getPDSortedByWins() {
        return new TreeSet<>(Comparator.comparingInt(PlayerData::getWins).reversed()) {{
            addAll(playerDataSet);
        }};
    }

    public SortedSet<PlayerData> getPDSortedByPoints() {
        return new TreeSet<>(Comparator.comparingInt(PlayerData::getGlobalPoints).reversed()) {{
            addAll(playerDataSet);
        }};
    }

    public SortedSet<PlayerData> getPDSortedByLosses() {
        return new TreeSet<>(Comparator.comparingInt(PlayerData::getLosses).reversed()) {{
            addAll(playerDataSet);
        }};
    }

    public SortedSet<PlayerData> getPDSortedByAvgPoints() {
        return new TreeSet<>(Comparator.comparingDouble(PlayerData::getAvgPoints).reversed()) {{
            addAll(playerDataSet);
        }};
    }


}
