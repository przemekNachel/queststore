package user.admin;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import user.service.UserService;

import java.util.NoSuchElementException;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.reflect.Whitebox.setInternalState;


@RunWith(PowerMockRunner.class)
@PrepareForTest({AdminController.class})
public class AdminFeaturesTest {

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
    public void test_if_AdminController_calls_createGroup_when_user_chose_third_option_from_main_menu() {

        try {
            systemInMock.provideLines("3");
            doNothing().when(controller).createGroup();
            controller.start();

        } catch (NoSuchElementException ignored) {
            Mockito.verify(controller, Mockito.times(1)).createGroup();
        }
    }
}