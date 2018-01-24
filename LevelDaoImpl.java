import java.util.HashMap;
import java.sql.*;

public class LevelDaoImpl {
    public HashMap<Integer, String> getLevelCollection() {
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
