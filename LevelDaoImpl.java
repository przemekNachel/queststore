import java.util.HashMap;
import java.sql.*;

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
    }

    private static Connection Connect() throws java.sql.SQLException, java.lang.ClassNotFoundException {
        Connection connection = null;

        String databasePath = "C:/Users/Nikodem/java/untitled/src/database.db";
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);


        return connection;
    }
}
