import generic_group.Group;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import quest.QuestModel;
import quest.QuestService;
import user.mentor.MentorController;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.matchers.JUnitMatchers.containsString;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MentorController.class})
public class DisplayAllQuests {


    @Mock
    QuestService questService;

    MentorController mentorController;

    @Before
    public void setup() throws Exception {
        mentorController = new MentorController();
        Whitebox.setInternalState(mentorController, "questSvc", questService);

    }

    @Test
    public void test_displayAllQuests_prints_properly_quests() throws Exception {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));


        Group<Group<QuestModel>> groups = new Group<>("test");
        Group<QuestModel> questModels = new Group<>("questModels");

        questModels.add(new QuestModel("questmodel1", "questmodel1desc", 123));
        questModels.add(new QuestModel("questmodel2", "questmodel1desc", 123));
        questModels.add(new QuestModel("questmodel3", "questmodel1desc", 123));
        questModels.add(new QuestModel("questmodel4", "questmodel1desc", 123));
        questModels.add(new QuestModel("questmodel5", "questmodel1desc", 123));
        questModels.add(new QuestModel("questmodel6", "questmodel1desc", 123));

        groups.add(questModels);

        PowerMockito.when(questService.getAllQuests()).thenReturn(groups);
        PowerMockito.verifyPrivate(mentorController).invoke("displayAllQuests");


        Assert.assertThat(outContent.toString(), containsString("questmodel1\n" +
                "questmodel2\n" +
                "questmodel3\n" +
                "questmodel4\n" +
                "questmodel5\n" +
                "questmodel6"));


    }
}
