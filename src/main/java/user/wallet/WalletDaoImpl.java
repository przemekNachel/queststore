package user.wallet;


import java.sql.*;

public class WalletDaoImpl implements WalletDao {

    private static String JDBC = "jdbc:mysql://54.37.232.83:3306/queststore?user=queststore&password=kuuurla&serverTimezone=UTC&useSSL=false";

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

    private void executeWalletUpdate(String updateQuery) {

        boolean succeeded = true;
        Connection connect = null;
        Statement statement = null;
        try {

            connect = establishConnection();
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

    public void addWallet(int userID, WalletService wallet) {

        String add = "INSERT INTO user_wallet(user_id, balance) " +
                "VALUES (" + userID + ", " + wallet.getBalance() + ");";

        executeWalletUpdate(add);

    }

    public void updateWallet(int userID, WalletService wallet) {


        String update = "UPDATE user_wallet " +
                "SET balance=" + wallet.getBalance() + " WHERE user_id=" + userID + ";";

        executeWalletUpdate(update);
    }

    private Connection establishConnection() throws SQLException {
        Connection connect = DriverManager.getConnection(WalletDaoImpl.JDBC);
        connect.setAutoCommit(false);
        return connect;
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
