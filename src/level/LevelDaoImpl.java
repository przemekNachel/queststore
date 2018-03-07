package level;

import abstractdao.AbstractDao;
import exceptionlog.ExceptionLog;

import java.util.HashMap;
import java.sql.*;
import java.util.Map;

public class LevelDaoImpl extends AbstractDao {

    public HashMap<Integer, String> getLevelCollection() {

        HashMap<Integer, String> levels = new HashMap<>();

        String grabAll = "SELECT * FROM predefined_levels;";

        try (PreparedStatement stmt = connection.prepareStatement(grabAll)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                Integer levelThreshold = rs.getInt("threshold");
                String levelName = rs.getString("level_name");
                levels.put(levelThreshold, levelName);
            }
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }

        return levels;
    }

    public boolean saveLevelCollection(HashMap<Integer, String> levels) {

        String sqlDelete = "DELETE FROM predefined_levels;";
        String sqlInsert = "INSERT INTO predefined_levels (threshold, level_name) VALUES(?, ?);";
        try (PreparedStatement deleteStmt = connection.prepareStatement(sqlDelete);
             PreparedStatement insertStmt = connection.prepareStatement(sqlInsert)){

            /* prepare a batch of insertion statements */
            for(Map.Entry<Integer, String> entry : levels.entrySet()) {

                insertStmt.setInt(1, entry.getKey());
                insertStmt.setString(2, entry.getValue());
                insertStmt.addBatch();
            }

            /* execute the statements */
            deleteStmt.executeUpdate();
            insertStmt.executeBatch();

            connection.commit();
            return true;

        } catch (SQLException e) {

            /* roll back on failure*/
            try {

                connection.rollback();
            } catch (SQLException e1) {

                ExceptionLog.add(e1);
            }
            ExceptionLog.add(e);
            return false;
        }
    }

    public Level getLevel(int userID) {

        int experienceGained = -1;

        String query = "SELECT experience_gained FROM user_experience WHERE user_id=?;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, userID);
            ResultSet results = stmt.executeQuery();

            if (results.next()) {

                experienceGained = results.getInt("experience_gained");
            }

        } catch (SQLException e) {

            ExceptionLog.add(e);
        }
        return new Level(experienceGained);
    }

    public boolean addExperience(int userID, Level level) {

        String add = "INSERT INTO user_experience(user_id, experience_gained) VALUES (?, ?);";

        try (PreparedStatement addStmt = connection.prepareStatement(add)) {

            addStmt.setInt(1, userID);
            addStmt.setInt(2, level.getCurrentExpirience());

            final int amountUpdated = addStmt.executeUpdate();

            connection.commit();
            return amountUpdated > 0;

        } catch (SQLException e) {

            ExceptionLog.add(e);
            return false;
        }
    }

    public boolean updateExperience(int userID, Level level) {


        String update = "UPDATE user_experience SET experience_gained=? WHERE user_id=?;";

        try (PreparedStatement updateStmt = connection.prepareStatement(update)) {

            updateStmt.setInt(1, level.getCurrentExpirience());
            updateStmt.setInt(2, userID);

            final int amountUpdated = updateStmt.executeUpdate();

            connection.commit();
            return amountUpdated > 0;

        } catch (SQLException e) {

            ExceptionLog.add(e);
            return false;
        }
    }
}
