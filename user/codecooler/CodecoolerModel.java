package User.Codecooler;

import Artifact.ArtifactModel;

import java.util.Iterator;

public class CodecoolerModel extends User {

    public WalletService wallet;
    public Group<ArtifactModel> artifacts;
    // public Level.Level level;

    public CodecoolerModel(String nickname, String email, String password, WalletService wallet, Group<User> studentGroup) {
        this.role = Role.CODECOOLER;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.wallet = wallet;

        associatedGroups = new Group<Group<User>>("Groups to which adheres");
        associatedGroups.add(studentGroup);

        this.artifacts = new Group<ArtifactModel>("artifacts of " + this.nickname);
    }

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
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

    public Group<String> getGroupNames() {
        Group<String> names = new Group<String>("GenericGroup.Group Names");

        Group<Group<User>> groups = getAssociatedGroups();
        Iterator<Group<User>> iter = groups.getIterator();
        while(iter.hasNext()) {
            names.add(iter.next().getName());
        }

        return names;
    }

    public String getStatisticsDisplay() {
        String statistics = "";
        String walletBalance = "Wallet balance: ";
        String artifactsOwned = "Owned artifacts:";

        walletBalance = wallet.toString() + "\n";

        Iterator<ArtifactModel> iter = artifacts.getIterator();
        while(iter.hasNext()) {
            artifactsOwned += "\n" + iter.next().getName();
        }
        statistics = walletBalance + artifactsOwned;

        return statistics;
    }

    public String getCodecoolerGroupDisplay() {
        String groupNamesString = "";

        Group<Group<User>> groups = getAssociatedGroups();
        Iterator<Group<User>> iter = groups.getIterator();
        while(iter.hasNext()) {
            groupNamesString += "|" + iter.next().getName();
        }

        return groupNamesString;
    }

    public WalletService getWallet() {
        return wallet;
    }
}
