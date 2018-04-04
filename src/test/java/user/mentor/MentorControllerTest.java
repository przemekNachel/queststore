package user.mentor;

import generic_group.Group;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import quest.QuestModel;
import quest.QuestService;
import user.service.UserService;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MentorController.class})
public class MentorControllerTest {

    @Rule
    public final TextFromStandardInputStream systemInMock
            = TextFromStandardInputStream.emptyStandardInputStream();
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    QuestModel questModel;

    @Mock
    QuestService questService;

    @Spy
    UserService userService = new UserService();

    MentorController mentorController;

    @Before
    public void setup() throws Exception {
        mentorController = PowerMockito.spy(new MentorController());

        setInternalState(mentorController, "questSvc", questService);
        setInternalState(mentorController, "userSvc", userService);

        questModel = new QuestModel("jakasNazwa", "jakisOpis", 999);
        doReturn(questModel).when(mentorController, "getQuestFromUserInput", Matchers.anyString());

    }

    @Test
    public void test_displayAllQuests_prints_properly_quests() throws Exception {
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

        when(questService.getAllQuests()).thenReturn(groups);
        invokeMethod(mentorController, "displayAllQuests");


        assertTrue(outContent.toString().contains("questmodel1\n" +
                "questmodel2\n" +
                "questmodel3\n" +
                "questmodel4\n" +
                "questmodel5\n" +
                "questmodel6"));

    }

    @Test
    public void test_displayAllQuests_does_not_crash_with_null_argument() throws Exception {

        when(questService.getAllQuests()).thenReturn(null);
        invokeMethod(mentorController, "displayAllQuests");

    }

    @Test
    public void test_updateQuest_updates_desc_and_reward_properly() throws Exception {

        systemInMock.provideLines("nowyOpis", "123");

        invokeMethod(mentorController, "updateQuest");

        assertEquals(questModel.getDescription(), "nowyOpis");
        assertEquals(questModel.getReward(), Integer.valueOf(123));
        verify(questService).updateQuest(questModel);

    }

    @Test
    public void test_removeQuest_calls_query_service_delete_quest() throws Exception {

        invokeMethod(mentorController, "removeQuest");
        verify(questService, times(1)).deleteQuest(questModel.getName());

    }

    @Test
    public void test_showCodecoolerWalletsBalance_should_callgetAllUsers_on_user_service() throws Exception {
        System.setOut(new PrintStream(outContent));
        invokeMethod(mentorController, "showCodecoolerWalletsBalance");

        verifyPrivate(mentorController, times(1)).invoke("showCodecoolerWalletsBalance");
        verify(userService, times(1)).getAllUsers();
        assertNotNull(outContent.toString());

    }

    @Test
    public void test_showCodecoolerWalletsBalance_handle_a_NPE() throws Exception {

        System.setOut(new PrintStream(outContent));
        when(userService.getAllUsers()).thenReturn(null);

        invokeMethod(mentorController, "showCodecoolerWalletsBalance");

        assertTrue(outContent.toString().contains("No codecoolers found"));


    }


}
