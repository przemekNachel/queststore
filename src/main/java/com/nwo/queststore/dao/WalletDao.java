package main.java.com.nwo.queststore.dao;

import main.java.com.nwo.queststore.service.WalletService;

public interface WalletDao {

    public WalletService getWallet(int userID);
    public void updateWallet(int userID, WalletService wallet);
}
