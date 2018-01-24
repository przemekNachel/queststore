import java.util.HashMap;
import java.sql.*;
import java.util.Map;

public class LevelDaoImpl {
    public HashMap<Integer, String> getLevelCollection(){
        try {
            HashMap<Integer, String> levels = new HashMap<>();
            Connection databaseConnection = Connect();
            Statement statement = databaseConnection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM predefined_levels;");

            while (rs.next()) {
                Integer levelThreshold = rs.getInt("threshold");
                String levelName = rs.getString("level_name");
                levels.put(levelThreshold, levelName);
            }

            return levels;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    public boolean saveLevelCollection(HashMap<Integer, String> levels,){
        try {
            Connection databaseConnection = Connect();
            Statement statement = databaseConnection.createStatement();

            String sqlCommand = "DELETE FROM predefined_levels";
            statement.executeUpdate(sqlCommand);

            for(Map.Entry<Integer, String> entry : levels.entrySet()) {
                sqlCommand = "INSERT INTO predefined_levels (threshold, level_name) VALUES('" + entry.getKey() +
                        "', '" + entry.getValue() + "');";
                statement.executeUpdate(sqlCommand);
                }

            return true;

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
            return false;
        }
    }

    private static Connection Connect() throws java.sql.SQLException, java.lang.ClassNotFoundException {
        Connection connection = null;

        String databasePath = "C:/Users/Nikodem/java/untitled/src/database.db";
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);


        return connection;
    }
}
