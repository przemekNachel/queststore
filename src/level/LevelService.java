package level;

import java.util.HashMap;

public class LevelService {
    public void initializeLevels(){
        LevelDaoImpl dao = new LevelDaoImpl();
        HashMap<Integer, String> levels = dao.getLevelCollection();
        Level.setLevels(levels);
    }

    public void saveLevels(HashMap<Integer, String> levels){
        LevelDaoImpl dao = new LevelDaoImpl();
        dao.saveLevelCollection(levels);
    }
}
