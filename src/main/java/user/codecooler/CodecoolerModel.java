package user.codecooler;

import artifact.ArtifactModel;
import generic_group.Group;
import level.Level;
import user.user.RawUser;
import user.user.Role;
import user.wallet.WalletService;

import java.util.Iterator;

public class CodecoolerModel extends RawUser {

    private WalletService wallet;
    private Level level;
    private Group<ArtifactModel> artifacts;

    public CodecoolerModel(RawUser rawUser, WalletService wallet, Group<ArtifactModel> artifacts, Level level) {
        super(Role.CODECOOLER,
                rawUser.getNickname(),
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

    public Group<ArtifactModel> getArtifacts() {
        return artifacts;
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