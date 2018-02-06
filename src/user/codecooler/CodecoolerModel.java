package user.codecooler;

import artifact.ArtifactModel;
import generic_group.Group;
import user.user.Role;
import user.wallet.WalletService;
import user.user.RawUser;

import java.util.Iterator;

public class CodecoolerModel extends RawUser {

    private WalletService wallet;
    private Group<ArtifactModel> artifacts;

    public CodecoolerModel(RawUser rawUser, WalletService wallet, Group<ArtifactModel> artifacts) {
        super(Role.CODECOOLER,
                rawUser.getName(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupNames());

        this.wallet = wallet;

        this.artifacts = artifacts;
    }

    public void addArtifact(ArtifactModel artifact) {
        artifacts.add(artifact);
    }

    public ArtifactModel getArtifact(String name) {

        Iterator<ArtifactModel> iter = artifacts.getIterator();
        while(iter.hasNext()) {

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
        String statistics;
        String walletBalance;
        String artifactsOwned = "Owned artifacts:";

        walletBalance = "Wallet balance: " + wallet.toString() + "\n\n";

        Iterator<ArtifactModel> iter = artifacts.getIterator();
        while(iter.hasNext()) {
            artifactsOwned += "\n" + iter.next().getName();
        }
        statistics = walletBalance + artifactsOwned;

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
}
