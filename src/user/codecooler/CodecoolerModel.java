package user.codecooler;

import artifact.ArtifactModel;
import generic_group.Group;
import user.user.Role;
import user.user.User;
import user.wallet.WalletService;

import java.util.Iterator;

public class CodecoolerModel extends User {

    private WalletService wallet;
    private Group<ArtifactModel> artifacts;

    public CodecoolerModel(String nickname, String email, String password, WalletService wallet, Group<User> studentGroup) {
        this.role = Role.CODECOOLER;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.wallet = wallet;

        associatedGroups = new Group<>("Groups to which adheres");
        associatedGroups.add(studentGroup);

        this.artifacts = new Group<>("artifacts of " + this.nickname);
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
        Group<String> names = new Group<>("generic_group.Group Names");

        Group<Group<User>> groups = getAssociatedGroups();
        Iterator<Group<User>> iter = groups.getIterator();
        while(iter.hasNext()) {
            names.add(iter.next().getName());
        }

        return names;
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
