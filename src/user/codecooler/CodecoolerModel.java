package user.codecooler;

import artifact.ArtifactModel;
import generic_group.Group;
import level.Level;
import level.LevelService;
import user.user.Role;
import user.wallet.WalletService;
import user.user.RawUser;

import java.util.Iterator;

public class CodecoolerModel extends RawUser {

    private WalletService wallet;
    private Level level;
    private Group<ArtifactModel> artifacts;

    public CodecoolerModel(RawUser rawUser, WalletService wallet, Group<ArtifactModel> artifacts, Level level) {
        super(Role.CODECOOLER,
                rawUser.getName(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupNames());

        this.wallet = wallet;
        this.level = level;
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

    public Group<ArtifactModel> getCodecoolerArtifacts() {

        return artifacts;
    }

    public String getStatisticsDisplay() {
        LevelService levelService = new LevelService();
        levelService.initializeLevels();
        String statistics;
        String walletBalance;
        String currentLevelStats;
        String artifactsOwned = "Owned artifacts:";

        currentLevelStats = "Current level " + level.getCurrentLevel() + " Xp: " + Integer.toString(level.getCurrentExpirience());
        walletBalance = "\nWallet balance: " + wallet.toString() + "\n\n";


        Iterator<ArtifactModel> iter = artifacts.getIterator();
        while (iter.hasNext()) {
            artifactsOwned += "\n" + iter.next().getName();
        }
        statistics = currentLevelStats + walletBalance + artifactsOwned;

        return statistics;
    }

    public String getCodecoolerGroupDisplay() {

        String groupNamesString = "";

        for (String groupName : getAssociatedGroupNames()) {

            groupNamesString += "|" + groupName;
        }
        return groupNamesString;
    }

    public WalletService getWallet() {
        return wallet;
    }

    public Level getLevel() {
        return level;
    }
}