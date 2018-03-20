package user.wallet;

public interface WalletDao {

    WalletService getWallet(int userID);

    void updateWallet(int userID, WalletService wallet);
}
