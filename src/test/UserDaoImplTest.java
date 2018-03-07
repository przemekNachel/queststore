package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import user.user.RawUser;
import user.user.UserDaoImpl;

import java.sql.SQLException;

@DisplayName("Dao Tests:")
class UserDaoImplTest {

    @Test
    @DisplayName("Dao Test getUser")
    void getExistingUserTest() throws SQLException{
        UserDaoImpl dao = new UserDaoImpl();
        RawUser testUser = dao.getUser("Admin1");

        assertEquals("Admin1", testUser.getName());
        assertEquals("password", testUser.getPassword());
    }
}