package level;

import java.util.HashMap;
import java.util.Map;
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
        Map<Integer, String> segregatedLevels = new TreeMap<>(levels);
        for (Map.Entry<Integer, String> entry : segregatedLevels.entrySet()) {
            if (entry.getKey() > experienceGained) {
                return entry.getValue();
            }
        }
        String higherThenAnyExistingLevel = "MASTER OF MASTERS LEVEL OVER 9000";
        return higherThenAnyExistingLevel;
    }

    public void addExperience(int experiencePoints) {
        experienceGained += experiencePoints;
    }

    public Integer getCurrentExpirience() {
        return experienceGained;
    }
}
