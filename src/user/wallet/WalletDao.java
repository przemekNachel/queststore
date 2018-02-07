package user.wallet;

public interface WalletDao {

    public WalletService getWallet(int userID);
    public void updateWallet(int userID, WalletService wallet);
}
