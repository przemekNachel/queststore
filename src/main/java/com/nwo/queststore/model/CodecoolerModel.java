package main.java.com.nwo.queststore.model;

import level.LevelService;
import user.user.Role;
import user.wallet.WalletService;

import java.util.Iterator;

public class CodecoolerModel extends RawUserModel {

    private WalletService wallet;
    private LevelModel levelModel;
    private GroupModel<ArtifactModel> artifacts;

    public CodecoolerModel(RawUserModel rawUser, WalletService wallet, GroupModel<ArtifactModel> artifacts, LevelModel levelModel) {
        super(Role.CODECOOLER,
                rawUser.getName(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupModelNames());

        this.wallet = wallet;
        this.levelModel = levelModel;
        this.artifacts = artifacts;
    }

    public void addArtifact(ArtifactModel artifact) {
        artifacts.add(artifact);
    }

    public ArtifactModel getArtifact(String name) {

        Iterator<ArtifactModel> iter = artifacts.getIterator();
        while (iter.hasNext()) {

            ArtifactModel currentArtifact = iter.next();
            if (currentArtifact.getName().equals(name)) {

                return currentArtifact;
            }
        }
        return null;
    }

    public GroupModel<ArtifactModel> getCodecoolerArtifacts() {

        return artifacts;
    }

    public String getStatisticsDisplay() {

        LevelService levelService = new LevelService();
        levelService.initializeLevels();
        String statistics;
        String walletBalance;
        String currentLevelStats;

        currentLevelStats = "Current levelModel " + levelModel.getCurrentLevel() + " Xp: " + Integer.toString(levelModel.getCurrentExpirience());
        walletBalance = "\nWallet balance: " + wallet.toString() + "\n\n";

        statistics = currentLevelStats + walletBalance;

        return statistics;
    }

    public String getCodecoolerGroupDisplay() {

        String groupNamesString = "";

        for (String groupName : getAssociatedGroupModelNames()) {

            groupNamesString += "|" + groupName;
        }
        return groupNamesString;
    }

    public WalletService getWallet() {
        return wallet;
    }

    public LevelModel getLevelModel() {
        return levelModel;
    }
}