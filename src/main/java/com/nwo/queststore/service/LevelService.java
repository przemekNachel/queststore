package main.java.com.nwo.queststore.service;

import main.java.com.nwo.queststore.dao.LevelDaoImpl;
import main.java.com.nwo.queststore.model.LevelModel;

import java.util.HashMap;

public class LevelService {
    public void initializeLevels(){
        LevelDaoImpl dao = new LevelDaoImpl();
        HashMap<Integer, String> levels = dao.getLevelCollection();
        LevelModel.setLevels(levels);
    }

    public void saveLevels(HashMap<Integer, String> levels){
        LevelDaoImpl dao = new LevelDaoImpl();
        dao.saveLevelCollection(levels);
    }
}
