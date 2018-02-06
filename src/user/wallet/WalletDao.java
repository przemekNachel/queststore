package user.wallet;

public interface WalletDao {

    public WalletService getWallet(String userID);
    public void updateWallet(String userID, WalletService wallet);
}
