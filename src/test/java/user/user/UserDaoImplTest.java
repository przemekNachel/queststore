package user.user;

import org.dbunit.Assertion;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserDaoImpl.class})
public class UserDaoImplTest {

    public static final String DATABASE_PATH = "src/test/resources/database.db";

    @Spy
    UserDaoImpl dao = new UserDaoImpl();

    JdbcDatabaseTester jdbcDatabaseTester;

    private Connection connection;

    public void loadDatabase() {

        File file = new File("src/test/resources/database.db.sql");
        String sql = null;
        try {
            sql = Files.readAllLines(file.toPath())
                    .stream()
                    .collect(Collectors.joining(" "));
            jdbcDatabaseTester.getConnection().getConnection().createStatement().executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Before
    public void setup() throws Exception {
        Files.delete(new File("src/test/resources/database.db").toPath());
        jdbcDatabaseTester = new JdbcDatabaseTester("org.sqlite.JDBC", String.format("jdbc:sqlite:%s", DATABASE_PATH));
        loadDatabase();
        connection = jdbcDatabaseTester.getConnection().getConnection();

    }


    @Test
    public void test_getAllGroupsNames_returns_all_groups_from_database() throws Exception {


        IDataSet expectedDataSet = jdbcDatabaseTester.getConnection().createDataSet();
        doReturn(connection).when(dao, "establishConnection");

        List<String> groups = new ArrayList<>();
        List<String> expected = Arrays.asList("quest_basic", "quest_extra", "quests", "artifact_basic", "codecoolers", "mentors", "admins", "artifact_magic", "artifacts");


        dao.getAllGroupNames().iterator().forEachRemaining(groups::add);

        assertTrue(groups.containsAll(expected));
        verifyPrivate(dao).invoke("establishConnection");

        IDataSet actualDataSet = jdbcDatabaseTester.getConnection().createDataSet();

        Assertion.assertEquals(expectedDataSet, actualDataSet);

    }


}