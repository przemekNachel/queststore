package level;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Level {
    private static HashMap<Integer, String> levels = new HashMap<>();
    private int experienceGained;

    public Level(int experienceGained) {
        this.experienceGained = experienceGained;
    }

    public static void addLevel(String name, Integer treshold) {
        LevelService service = new LevelService();
        service.initializeLevels();
        levels.put(treshold, name);
        service.saveLevels(levels);
    }

    public static HashMap<Integer, String> getLevels() {
        return levels;
    }

    public static void setLevels(HashMap<Integer, String> importedLevels) {
        levels = importedLevels;
    }

    public String getCurrentLevel() {
        String higherThanAnyExistingLevel = "";
        NavigableMap<Integer, String> segregatedLevels = new TreeMap<>(levels);
        for (Map.Entry<Integer, String> entry : segregatedLevels.entrySet()) {
            Map.Entry<Integer, String> nextEntry = segregatedLevels.higherEntry(entry.getKey());
            try {
                if (experienceGained >= entry.getKey() && experienceGained < nextEntry.getKey()) {
                    return entry.getValue();
                }
            }catch(NullPointerException e){
                higherThanAnyExistingLevel = "MASTER OF MASTERS LEVEL OVER 9000";
            }
        }
        return higherThanAnyExistingLevel;
    }

    public void addExperience(int experiencePoints) {
        experienceGained += experiencePoints;
    }

    public Integer getCurrentExperience() {
        return experienceGained;
    }
}
