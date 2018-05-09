package level;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class LevelDaoImpl {

    private static Connection connect() throws java.sql.SQLException {
        String JDBC = "jdbc:mysql://54.37.232.83:3306/queststore?user=queststore&password=kuuurla&serverTimezone=UTC&useSSL=false";
        Connection connect = DriverManager.getConnection(JDBC);
        connect.setAutoCommit(false);
        return connect;
    }

    public HashMap<Integer, String> getLevelCollection() {
        try {
            HashMap<Integer, String> levels = new HashMap<>();
            Connection databaseConnection = connect();
            Statement statement = databaseConnection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM predefined_levels;");

            while (rs.next()) {
                Integer levelThreshold = rs.getInt("threshold");
                String levelName = rs.getString("level_name");
                levels.put(levelThreshold, levelName);
            }
            databaseConnection.commit();
            rs.close();
            statement.close();
            databaseConnection.close();
            return levels;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    public boolean saveLevelCollection(HashMap<Integer, String> levels) {
        try {
            Connection databaseConnection = connect();
            Statement statement = databaseConnection.createStatement();

            String sqlCommand = "DELETE FROM predefined_levels";
            statement.executeUpdate(sqlCommand);

            for (Map.Entry<Integer, String> entry : levels.entrySet()) {
                sqlCommand = "INSERT INTO predefined_levels (threshold, level_name) VALUES('" + entry.getKey() +
                        "', '" + entry.getValue() + "');";
                statement.executeUpdate(sqlCommand);
            }
            databaseConnection.commit();
            statement.close();
            databaseConnection.close();
            return true;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            return false;
        }
    }

    public Level getLevel(int userID) {
        Connection connect = null;
        Statement statement = null;
        int experienceGained = -1;

        try {
            connect = connect();
            statement = connect.createStatement();

            String query = "SELECT experience_gained FROM user_experience WHERE user_id='" + userID + "';";
            ResultSet results = statement.executeQuery(query);

            if (results.next()) {
                experienceGained = results.getInt("experience_gained");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                close(connect, statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return experienceGained < 0 ? null : new Level(experienceGained);
    }

    public void addExperience(int userID, Level level) {

        String add = "INSERT INTO user_experience(user_id, experience_gained) " +
                "VALUES (" + userID + ", " + level.getCurrentExperience() + ");";

        executeExperienceUpdate(add);
    }

    public void updateExperience(int userID, Level level) {


        String update = "UPDATE user_experience " +
                "SET experience_gained=" + level.getCurrentExperience() + " WHERE user_id=" + userID + ";";

        executeExperienceUpdate(update);
    }

    private void executeExperienceUpdate(String updateQuery) {

        boolean succeeded = true;
        Connection connect = null;
        Statement statement = null;
        try {

            connect = connect();
            statement = connect.createStatement();

            statement.executeUpdate(updateQuery);

        } catch (SQLException e) {

            e.printStackTrace();
            succeeded = false;
        } finally {
            if (succeeded) {
                try {
                    connect.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            try {
                close(connect, statement);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(Connection connect, Statement statement) throws SQLException {
        if (statement != null) {
            statement.close();
        }
        if (connect != null) {
            connect.close();
        }
    }
}
