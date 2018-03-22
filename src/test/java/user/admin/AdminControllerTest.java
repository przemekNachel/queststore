package user.admin;

import abstractusercontroller.AbstractUserController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import user.service.UserService;
import user.user.Role;
import user.user.User;

import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.reflect.Whitebox.invokeMethod;
import static org.powermock.reflect.Whitebox.setInternalState;


@RunWith(PowerMockRunner.class)
@PrepareForTest({AdminController.class})
public class AdminControllerTest {

    @Rule
    public final TextFromStandardInputStream systemInMock = TextFromStandardInputStream.emptyStandardInputStream();


    AdminController controller;

    @Spy
    UserService userService = new UserService();

    @Before
    public void setup() {
        controller = spy(new AdminController());
        setInternalState(controller, "userSvc", userService);
    }

    @Test
    public void test_if_AdminController_calls_createMentor_when_user_chose_first_option_from_main_menu() throws Exception {

        try {
            systemInMock.provideLines("1");
            doNothing().when(controller, "createMentor");
            controller.start();

        } catch (NoSuchElementException ignored) {
            verifyPrivate(controller, Mockito.times(1)).invoke("createMentor");
        }
    }

    @Test
    public void test_createMentor_creates_notNull_instance_ofMentor_from_user_input() throws Exception {

        systemInMock.provideLines("krzsztof_jarzyna@zeszczecina.pl", "admin1");

        doReturn(true).when(userService).addUser(Mockito.any());
        doReturn("KrzysztofJarzyna").when(controller, "getNonexistentMentorName");

        invokeMethod(controller, "createMentor");

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userService).addUser(userArgumentCaptor.capture());

        assertEquals(userArgumentCaptor.getValue().getEmail(), "krzsztof_jarzyna@zeszczecina.pl");
        assertEquals(userArgumentCaptor.getValue().getPassword(), "admin1");
        assertEquals(userArgumentCaptor.getValue().getRole(), Role.MENTOR);


    }

    @Test
    public void test_if_AdminController_calls_assignMentorToGroup_when_user_chose_first_option_from_main_menu() throws Exception {

        try {
            systemInMock.provideLines("2");
            doNothing().when(controller).assignMentorToGroup();
            controller.start();

        } catch (NoSuchElementException ignored) {
            Mockito.verify(controller, Mockito.times(1)).assignMentorToGroup();
        }
    }

    @Test
    public void test_if_assignMentorToGroup_calls_userService() throws Exception {

        String expected = "SuperGrupa";
        systemInMock.provideLines(expected);

        doReturn(null).when(userService).getUserGroupNames();
        doReturn(null).when(controller, "getMentorFromUserInput");
        doReturn(expected).when((AbstractUserController) controller).getNameFromUserInput(Mockito.anyString(), Mockito.anyString(), Mockito.any());

        doReturn(true).when(userService).addUserAdherence(Mockito.any(), Mockito.anyString());

        controller.assignMentorToGroup();

        verifyPrivate(controller).invoke("getMentorFromUserInput");
        Mockito.verify(userService).addUserAdherence(Mockito.any(), Mockito.anyString());


    }


}