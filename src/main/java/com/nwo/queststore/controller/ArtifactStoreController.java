package main.java.com.nwo.queststore.controller;

import main.java.com.nwo.queststore.model.ArtifactModel;
import artifact.ArtifactService;
import artifact.ArtifactStoreView;
import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.CodecoolerModel;
import user.service.UserService;
import main.java.com.nwo.queststore.model.UserModel;

public class ArtifactStoreController {

    private ArtifactStoreView view;
    private ArtifactService artifactSvc;

    public ArtifactStoreController() {

        this.view = new ArtifactStoreView();
        this.artifactSvc = new ArtifactService();
    }

    private GroupModel<String> getAllowedArtifactNames() {

        GroupModel<String> allowedArtifactNames = new GroupModel<>("allowed artifact name user input");
        // get available artifacts by category/ group

        GroupModel<String> artifactGroupModelNames = artifactSvc.getArtifactGroupNames();

        for (String groupName : artifactGroupModelNames) {

            GroupModel<ArtifactModel> artifactGroupModel = artifactSvc.getArtifactGroup(groupName);

            for (ArtifactModel currentArtifact : artifactGroupModel) {

                allowedArtifactNames.add(currentArtifact.getName());
            }
        }
        return allowedArtifactNames;
    }

    private String getArtifactStoreDisplay() {

        // get available artifacts by category/ group
        GroupModel<String> artifactGroupModelNames = artifactSvc.getArtifactGroupNames();

        String display = "\n  Available artifacts:\n";
        for (String groupName : artifactGroupModelNames) {

            GroupModel<ArtifactModel> artifactGroupModel = artifactSvc.getArtifactGroup(groupName);

            display += "\n    GroupModel " + artifactGroupModel.getName();
            for (ArtifactModel currentArtifact : artifactGroupModel) {

                display += "\n      " + currentArtifact.getName() + " - " + currentArtifact.getDescription() + " - PRICE: " + currentArtifact.getPrice();
            }
        }

        return display;
    }

    private String getArtifactNameFromUserInput() {

        GroupModel<String> allowedArtifactNames = getAllowedArtifactNames();
        String storeDisplay = getArtifactStoreDisplay();

        view.printLine(view.productsMessage);
        view.printLine(storeDisplay);

        String artifactName;
        boolean providedValidArtifactName = false;
        do {

            artifactName = view.getStringFromUserInput(view.artifactNameQuestion);
            if (allowedArtifactNames.contains(artifactName) || artifactName.equals(view.magicExitString)) {

                providedValidArtifactName = true;
            } else {
                view.printLine(view.artifactNotFoundError);
            }

        } while(!providedValidArtifactName);

        return artifactName;
    }

    private GroupModel<CodecoolerModel> intersect(GroupModel<UserModel> students, GroupModel<UserModel> consumers) {

        GroupModel<CodecoolerModel> intersection = new GroupModel<>("Codecooler(s) buying an artifact");
        for (UserModel consumer : consumers) {

            for (UserModel student : students) {

                if(consumer.getName().equals(student.getName())) {

                    intersection.add((CodecoolerModel)consumer);
                }
            }
        }
        return intersection;
    }

    private GroupModel<CodecoolerModel> getConsumerGroup(CodecoolerModel codecooler) {

        UserService userSvc = new UserService();

        // get all user groups to choose from and display them
        GroupModel<String> allowedGroupModelNames = codecooler.getAssociatedGroupModelNames();

        // get group which will crowd-fund the artifact
        GroupModel<CodecoolerModel> codecoolers = null;
        boolean providedExistentGroupName = false;
        boolean wantToBuyAlone = false;
        String groupsDisplay = "\n\n  Groups that can crowd-fund this purchase: \n\n   " + codecooler.getCodecoolerGroupDisplay() + "\n";
        do {
            view.printLine(groupsDisplay);
            String consumerGroupName = view.getStringFromUserInput(view.chooseGroup);
            if (allowedGroupModelNames.contains(consumerGroupName)) {

                GroupModel<UserModel> students = userSvc.getUserGroup("codecoolers");
                GroupModel<UserModel> consumers = userSvc.getUserGroup(consumerGroupName);

                codecoolers = intersect(students, consumers);

                providedExistentGroupName = true;

            } else {
                if (consumerGroupName.equals("ALONE")) {

                    codecoolers = new GroupModel<>("buying alone");
                    codecoolers.add(codecooler);
                    wantToBuyAlone = true;
                } else if (consumerGroupName.equals(view.magicExitString)) {
                    return null;
                } else {

                    view.printLine(view.invalidGroupName);
                }
            }
        } while(!providedExistentGroupName && !wantToBuyAlone);

        return codecoolers;
    }

    public void buyProductProcess(CodecoolerModel codecooler){

        view.printLine(view.abortHint);
        String artifactName = getArtifactNameFromUserInput();
        if (artifactName.equals(view.magicExitString)) {

            return;
        }

        GroupModel<CodecoolerModel> consumers = getConsumerGroup(codecooler);
        if (consumers == null) {
            /* purchase process aborted as requested by user */
            return;
        }
        CodecoolerModel buyer = getSelf(consumers, codecooler);
        /* the above is necessary for proper object to update */

        ArtifactModel boughtArtifact = buyArtifact(artifactName, consumers);

        if (boughtArtifact == null) {

            return;
        }
        /* purchase process succeeded */
        buyer.addArtifact(boughtArtifact);
        UserService userSvc = new UserService();
        for (UserModel userModel : consumers) {

            userSvc.updateUser(userModel);
        }
    }

    /* reason for the below method: a getter for consumer group gets the same codecooler and
     * we have extract them because it is their wallet and artifacts that have supposedly been changed */
    private CodecoolerModel getSelf(GroupModel<CodecoolerModel> consumers, CodecoolerModel twin) {

        for (CodecoolerModel codecooler : consumers) {

            if (codecooler.getName().equals(twin.getName())) {

                return codecooler;
            }
        }
        return null;
    }

    public ArtifactModel buyArtifact(String name, GroupModel<CodecoolerModel> consumers) {

        ArtifactModel artifact = artifactSvc.getArtifactByName(name);

        if(artifact == null){
            return null;
        }

        Integer divideAmong = consumers.size();
        Integer artifactPrice = artifact.getPrice();
        Integer alignedPrice = artifactPrice - (artifactPrice % divideAmong);
        Integer share = alignedPrice / divideAmong;

        boolean allCanAfford = true;
        for (CodecoolerModel codecooler : consumers) {

            allCanAfford &= codecooler.getWallet().canAfford(share);
        }

        if (!allCanAfford) {

            view.printLine(view.insufficientFunds);
            return null;
        }

        for (CodecoolerModel codecooler : consumers) {

            codecooler.getWallet().withdraw(share);
        }
        return artifact;
    }
}
