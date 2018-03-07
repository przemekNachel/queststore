package abstractdao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AbstractDao {

    private static String JDBC = "jdbc:sqlite:database/database.db";
    protected Connection connection;

    public AbstractDao() {
        /* do nothing, we initialize connection on our own */
    }

    public AbstractDao(Connection connection) {

        this.connection = connection;
    }

    public void establishConnection() {

        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(AbstractDao.JDBC);
            connection.setAutoCommit(false);
        } catch (SQLException | ClassNotFoundException e) {

            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public void finalizeConnection() {

        try {

            connection.close();
        } catch(SQLException e) {

            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
}
