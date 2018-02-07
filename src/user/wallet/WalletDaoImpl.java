package user.wallet;


import java.sql.*;

public class WalletDaoImpl implements WalletDao {

    private static String JDBC = "jdbc:sqlite:database/database.db";

    public WalletService getWallet(int userID) {


        Connection connect = null;
        Statement statement = null;
        int balance = -1;

        try {
            connect = establishConnection();
            statement = connect.createStatement();

            String query = "SELECT balance FROM user_wallet WHERE user_id='" + userID + "';";
            ResultSet results = statement.executeQuery(query);

            if (results.next()) {

                balance = results.getInt("balance");
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

        return balance < 0 ? null : new WalletService(balance);
    }

    public void updateWallet(int userID, WalletService wallet) {

        boolean succeeded = true;
        Connection connect = null;
        Statement statement = null;
        try {

            connect = establishConnection();
            statement = connect.createStatement();

            String updateWallet = "INSERT INTO user_wallet(user_id, balance) " +
                    "VALUES (" + userID + ", " + wallet.getBalance() + ");";

            statement.executeUpdate(updateWallet);

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

    private void upgradeWallet(int balance, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateWallet = "UPDATE user_wallet " +
                "SET balance=" + balance + " WHERE user_id=" + userId + ";";

        statement.executeUpdate(updateWallet);
        connect.commit();
        close(connect, statement);
    }

    private Connection establishConnection() throws SQLException {

        try{
            Class.forName("org.sqlite.JDBC");
        }catch(ClassNotFoundException e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        Connection connect = DriverManager.getConnection(WalletDaoImpl.JDBC);
        connect.setAutoCommit(false);
        return connect;
    }

    private void close(Connection connect, Statement statement) throws SQLException{
        if(statement != null){
            statement.close();
        }
        if(connect != null){
            connect.close();
        }
    }
}
