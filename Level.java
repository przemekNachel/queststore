import java.util.HashMap;
import java.util.Map;

public class Level{
    private static HashMap<Integer, String> levels = new HashMap<>();
    private Integer experienceGained;

    public static void addLevel(String name, Integer treshold){
        LevelService service = new LevelService();
        levels.put(treshold, name);
        service.saveLevels(levels);
    }

    public String getCurrentLevel() {
        for(Map.Entry<Integer, String> entry : levels.entrySet()) {
            if (entry.getKey() > experienceGained) {
                return entry.getValue();
            }
        }
        return null;
    }
    public Integer getCurrentExpirience(){
        return experienceGained;
    }

    public HashMap<Integer, String> getLevels() {
        return levels;
    }

    public static void setLevels(HashMap<Integer, String> importedLevels) {
        levels = importedLevels;
    }
}