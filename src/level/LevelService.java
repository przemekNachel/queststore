package level;

import java.util.HashMap;

public class LevelService {
    public void initializeLevels(){
        LevelDaoImpl dao = new LevelDaoImpl();
        dao.establishConnection();

        HashMap<Integer, String> levels = dao.getLevelCollection();
        Level.setLevels(levels);

        dao.finalizeConnection();
    }

    public void saveLevels(HashMap<Integer, String> levels){
        LevelDaoImpl dao = new LevelDaoImpl();
        dao.establishConnection();

        dao.saveLevelCollection(levels);

        dao.finalizeConnection();
    }
}
