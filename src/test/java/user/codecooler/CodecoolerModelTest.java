package user.codecooler;

import artifact.ArtifactModel;
import generic_group.Group;
import level.Level;
import org.junit.Before;
import org.junit.Test;
import user.user.RawUser;
import user.user.Role;
import user.wallet.WalletService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

public class CodecoolerModelTest {

    private CodecoolerModel codecoolerModel;
    private RawUser rawUser;
    private Group<String> group;
    private WalletService wallet;
    private Group<ArtifactModel> artifactsGroup;
    private Level level;
    private int experienceGained;
    private String expectedOutput;
    private ArtifactModel artifact;

    @Before
    public void setup(){
        group = new Group<>("");
        artifactsGroup = new Group<>("");
        rawUser = new RawUser(Role.CODECOOLER, "", "", "", group);

    }

    @Test
    public void testIfCorrectStatisticOutputWhenXp0() {
        experienceGained = 0;
        level = new Level(experienceGained);
        wallet = new WalletService(0);
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);

        expectedOutput = "Current level Level 1 Xp: " + experienceGained + "\nWallet balance: 0\n\n";
        assertEquals(expectedOutput, codecoolerModel.getStatisticsDisplay());
    }

    @Test
    public void testIfCorrectStatisticOutputWhenXpIsNotBoundValue() {
        experienceGained = 150;
        level = new Level(experienceGained);
        wallet = new WalletService(0);
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);

        expectedOutput = "Current level Level 2 Xp: " + experienceGained + "\nWallet balance: 0\n\n";
        assertEquals(expectedOutput, codecoolerModel.getStatisticsDisplay());
    }

    @Test
    public void testIfCorrectStatisticOutputWhenXpIsBoundValue(){
        experienceGained = 200;
        level = new Level(experienceGained);
        wallet = new WalletService(100);
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);

        expectedOutput = "Current level Level 3 Xp: " + experienceGained + "\nWallet balance: 100\n\n";
        assertEquals(expectedOutput, codecoolerModel.getStatisticsDisplay());
    }

    @Test
    public void testIfCorrectStatisticOutputWhenXpValueBiggerThanLevelThreshold(){
        int maxIntValue = 2147483647;
        level = new Level(maxIntValue);
        wallet = new WalletService(0);
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);

        expectedOutput = "Current level MASTER OF MASTERS LEVEL OVER 9000 Xp: " + maxIntValue + "\nWallet balance: 0\n\n";
        assertEquals(expectedOutput, codecoolerModel.getStatisticsDisplay());
    }

    @Test
    public void testIfCorrectCodecoolerGroupOutputWhenNotInAnyGroup(){
        group = new Group<>("");
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);
        expectedOutput = "";
        assertEquals(expectedOutput, codecoolerModel.getCodecoolerGroupDisplay());
    }

    @Test
    public void testIfCorrectCodecoolerGroupOutputWhenInOneGroup(){
        Group<String> ccGroup = new Group<>("Codecooler group");
        String group = "Team India";
        ccGroup.add(group);

        rawUser = new RawUser(Role.CODECOOLER, "", "", "", ccGroup);
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);

        expectedOutput = "|" + group;
        assertEquals(expectedOutput, codecoolerModel.getCodecoolerGroupDisplay());
    }

    @Test
    public void testIfCorrectCodecoolerMultipleGroupOutput(){
        Group<String> ccGroup = new Group<>("Codecooler group");
        String group1 = "Team India";
        String group2 = "Team CodeCool";
        String group3 = "Test Group";

        ccGroup.add(group1);
        ccGroup.add(group2);
        ccGroup.add(group3);

        rawUser = new RawUser(Role.CODECOOLER, "", "", "", ccGroup);
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);

        expectedOutput = "|" + group1 + "|" + group2 + "|" + group3;

        assertEquals(expectedOutput, codecoolerModel.getCodecoolerGroupDisplay());
    }

    @Test
    public void testIfCorrectCodecoolerOwnedArtifactsOutputWhenDontHaveAnyArtifacts() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        artifactsGroup = new Group<>("");
        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);
        CodecoolerController ccController = new CodecoolerController(codecoolerModel);

        Method privateMethodReflection = CodecoolerController.class.getDeclaredMethod("codecoolerArtifactsToString", CodecoolerModel.class);
        privateMethodReflection.setAccessible(true);
        String result = (String) privateMethodReflection.invoke(ccController, codecoolerModel);

        expectedOutput = "";
        assertEquals(expectedOutput, result);
    }

    @Test
    public void testIfCorrectCodecoolerOwnedArtifactsOutputWithOneArtifact() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        artifact = new ArtifactModel("artifact", "", 0);
        artifactsGroup = new Group<>("Artifact group");
        artifactsGroup.add(artifact);

        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);
        CodecoolerController ccController = new CodecoolerController(codecoolerModel);

        Method privateMethodReflection = CodecoolerController.class.getDeclaredMethod("codecoolerArtifactsToString", CodecoolerModel.class);
        privateMethodReflection.setAccessible(true);
        String result = (String) privateMethodReflection.invoke(ccController, codecoolerModel);

        expectedOutput = "  artifact  NOT USED\n";
        assertEquals(expectedOutput, result);
    }

    @Test
    public void testIfCorrectCodecoolerOwnedArtifactsOutputWithMultipleArtifacts() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        ArtifactModel artifact1 = new ArtifactModel("computer", "", 0);
        ArtifactModel artifact2 = new ArtifactModel("keyboard", "", 0);
        ArtifactModel artifact3 = new ArtifactModel("wi-fi", "", 0);
        artifactsGroup = new Group<>("Artifact group");
        artifactsGroup.add(artifact1);
        artifactsGroup.add(artifact2);
        artifactsGroup.add(artifact3);

        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);
        CodecoolerController ccController = new CodecoolerController(codecoolerModel);

        Method privateMethodReflection = CodecoolerController.class.getDeclaredMethod("codecoolerArtifactsToString", CodecoolerModel.class);
        privateMethodReflection.setAccessible(true);
        String result = (String) privateMethodReflection.invoke(ccController, codecoolerModel);

        expectedOutput = "  computer  NOT USED\n  keyboard  NOT USED\n  wi-fi  NOT USED\n";
        assertEquals(expectedOutput, result);
    }

    @Test
    public void testIfCorrectOwnedArtifactsOutputWhenArtifactUsed() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException{
        artifact = new ArtifactModel("artifact", "", 0);
        artifactsGroup = new Group<>("Artifact group");
        artifactsGroup.add(artifact);
        artifact.setUsageStatus(true);

        codecoolerModel = new CodecoolerModel(rawUser, wallet, artifactsGroup, level);
        CodecoolerController ccController = new CodecoolerController(codecoolerModel);

        Method privateMethodReflection = CodecoolerController.class.getDeclaredMethod("codecoolerArtifactsToString", CodecoolerModel.class);
        privateMethodReflection.setAccessible(true);
        String result = (String) privateMethodReflection.invoke(ccController, codecoolerModel);

        expectedOutput = "  artifact  USED\n";
        assertEquals(expectedOutput, result);
    }
}