import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Level{
    private static HashMap<Integer, String> levels = new HashMap<>();
    private Integer experienceGained;

    public static void addLevel(String name, Integer treshold){
        LevelService service = new LevelService();
        levels.put(treshold, name);
        service.saveLevels(levels);
    }

    public String getCurrentLevel() {
        Map<Integer, String> segregatedLevels = new TreeMap<>(levels);
        for(Map.Entry<Integer, String> entry : segregatedLevels.entrySet()) {
            if (entry.getKey() > experienceGained) {
                return entry.getValue();
            }
        }
        return null;
    }
    public Integer getCurrentExpirience(){
        return experienceGained;
    }

    public static HashMap<Integer, String> getLevels() {
        return levels;
    }

    public static void setLevels(HashMap<Integer, String> importedLevels) {
        levels = importedLevels;
    }
}