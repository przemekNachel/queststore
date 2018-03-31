package main.java.com.nwo.queststore.controller;

import artifact.ArtifactModel;
import artifact.ArtifactService;
import artifact.ArtifactStoreView;
import generic_group.Group;
import user.codecooler.CodecoolerModel;
import user.service.UserService;
import user.user.User;

import java.sql.*;

public class ArtifactStoreController {

    private ArtifactStoreView view;
    private ArtifactService artifactSvc;

    public ArtifactStoreController() {

        this.view = new ArtifactStoreView();
        this.artifactSvc = new ArtifactService();
    }

    private Group<String> getAllowedArtifactNames() {

        Group<String> allowedArtifactNames = new Group<>("allowed artifact name user input");
        // get available artifacts by category/ group

        Group<String> artifactGroupNames = artifactSvc.getArtifactGroupNames();

        for (String groupName : artifactGroupNames) {

            Group<ArtifactModel> artifactGroup = artifactSvc.getArtifactGroup(groupName);

            for (ArtifactModel currentArtifact : artifactGroup) {

                allowedArtifactNames.add(currentArtifact.getName());
            }
        }
        return allowedArtifactNames;
    }

    private String getArtifactStoreDisplay() {

        // get available artifacts by category/ group
        Group<String> artifactGroupNames = artifactSvc.getArtifactGroupNames();

        String display = "\n  Available artifacts:\n";
        for (String groupName : artifactGroupNames) {

            Group<ArtifactModel> artifactGroup = artifactSvc.getArtifactGroup(groupName);

            display += "\n    Group " + artifactGroup.getName();
            for (ArtifactModel currentArtifact : artifactGroup) {

                display += "\n      " + currentArtifact.getName() + " - " + currentArtifact.getDescription() + " - PRICE: " + currentArtifact.getPrice();
            }
        }

        return display;
    }

    private String getArtifactNameFromUserInput() {

        Group<String> allowedArtifactNames = getAllowedArtifactNames();
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

    private Group<CodecoolerModel> intersect(Group<User> students, Group<User> consumers) {

        Group<CodecoolerModel> intersection = new Group<>("Codecooler(s) buying an artifact");
        for (User consumer : consumers) {

            for (User student : students) {

                if(consumer.getName().equals(student.getName())) {

                    intersection.add((CodecoolerModel)consumer);
                }
            }
        }
        return intersection;
    }

    private Group<CodecoolerModel> getConsumerGroup(CodecoolerModel codecooler) {

        UserService userSvc = new UserService();

        // get all user groups to choose from and display them
        Group<String> allowedGroupNames = codecooler.getAssociatedGroupNames();

        // get group which will crowd-fund the artifact
        Group<CodecoolerModel> codecoolers = null;
        boolean providedExistentGroupName = false;
        boolean wantToBuyAlone = false;
        String groupsDisplay = "\n\n  Groups that can crowd-fund this purchase: \n\n   " + codecooler.getCodecoolerGroupDisplay() + "\n";
        do {
            view.printLine(groupsDisplay);
            String consumerGroupName = view.getStringFromUserInput(view.chooseGroup);
            if (allowedGroupNames.contains(consumerGroupName)) {

                Group<User> students = userSvc.getUserGroup("codecoolers");
                Group<User> consumers = userSvc.getUserGroup(consumerGroupName);

                codecoolers = intersect(students, consumers);

                providedExistentGroupName = true;

            } else {
                if (consumerGroupName.equals("ALONE")) {

                    codecoolers = new Group<>("buying alone");
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

        Group<CodecoolerModel> consumers = getConsumerGroup(codecooler);
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
        for (User user : consumers) {

            userSvc.updateUser(user);
        }
    }

    /* reason for the below method: a getter for consumer group gets the same codecooler and
     * we have extract them because it is their wallet and artifacts that have supposedly been changed */
    private CodecoolerModel getSelf(Group<CodecoolerModel> consumers, CodecoolerModel twin) {

        for (CodecoolerModel codecooler : consumers) {

            if (codecooler.getName().equals(twin.getName())) {

                return codecooler;
            }
        }
        return null;
    }

    public ArtifactModel buyArtifact(String name, Group<CodecoolerModel> consumers) {

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
