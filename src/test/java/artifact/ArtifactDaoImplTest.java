package artifact;

import generic_group.Group;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ArtifactDaoImplTest {

    private ArtifactDaoImpl dao;
    private ResultSet resultSet;

    private ArtifactModel artifactMagic;
    private ArtifactModel artifactStandard;
    private ArtifactModel thirdArtifact;
    private ArtifactModel fourthArtifact;

    private Group<ArtifactModel> artifactsInGroup;

    @Before
    public void setup() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        dao = mock(ArtifactDaoImpl.class, withSettings().defaultAnswer(CALLS_REAL_METHODS));
        Connection c = mock(Connection.class);
        Statement statement = mock(Statement.class);
        resultSet = mock(ResultSet.class);

        artifactMagic = new ArtifactModel("Pillow", "Fluffy and big", 20);
        artifactStandard = new ArtifactModel("Chair", "Hard and uncomfortable", 30);
        thirdArtifact = new ArtifactModel("Table", "Stylish table", 40);
        fourthArtifact = new ArtifactModel("Ball", "Hard as steel", 50);

        artifactsInGroup = new Group<>("artifacts");
        artifactsInGroup.add(artifactMagic);
        artifactsInGroup.add(artifactStandard);
        artifactsInGroup.add(thirdArtifact);
        artifactsInGroup.add(fourthArtifact);

        when(dao.connectToDatabase()).thenReturn(c);
        when(c.createStatement()).thenReturn(statement);
        when(statement.executeQuery(any(String.class))).thenReturn(resultSet);

        when(resultSet.getString("name")).thenReturn(artifactMagic.getName());
        when(resultSet.getString("descr")).thenReturn(artifactMagic.getDescription());
        when(resultSet.getInt("price")).thenReturn(artifactMagic.getPrice());
    }

    @Test
    public void testGetArtifactByName_checkIfReturnsCorrectArtifact() throws SQLException {
        ArtifactModel a = dao.getArtifactByName("magic");
        checkIfIsEqualArtifact(artifactMagic, a);
    }

    @Test
    public void testGetArtifactById_checkIfReturnsCorrectArtifact() throws SQLException {
        ArtifactModel a = dao.getArtifactById(1);
        checkIfIsEqualArtifact(artifactMagic, a);
    }

    @Test(expected = SQLException.class)
    public void testGetArtifactByName_checkIfReturnsNullWhenArtifactDoesNotExist() throws SQLException {
        when(resultSet.getString("name")).thenThrow(new SQLException());
        dao.getArtifactByName("magic");
    }

    @Test(expected = SQLException.class)
    public void testGetArtifactById_checkIfReturnsNullWhenArtifactDoesNotExist() throws SQLException {
        when(resultSet.getString("name")).thenThrow(new SQLException());
        dao.getArtifactById(2);
    }

    @Test
    public void testGetArtifactGroup() throws SQLException {
        when(resultSet.next())
                .thenReturn(true, true, true, true, false);

        when(resultSet.getString("name")).thenReturn(
                artifactsInGroup.get(0).getName(),
                artifactsInGroup.get(1).getName(),
                artifactsInGroup.get(2).getName(),
                artifactsInGroup.get(3).getName());

        when(resultSet.getString("descr")).thenReturn(
                artifactsInGroup.get(0).getDescription(),
                artifactsInGroup.get(1).getDescription(),
                artifactsInGroup.get(2).getDescription(),
                artifactsInGroup.get(3).getDescription());

        when(resultSet.getInt("price")).thenReturn(
                artifactsInGroup.get(0).getPrice(),
                artifactsInGroup.get(1).getPrice(),
                artifactsInGroup.get(2).getPrice(),
                artifactsInGroup.get(3).getPrice());

        Group<ArtifactModel> group = dao.getArtifactGroup("artifacts");
        for (int i = 0; i < group.size(); i++) {
            checkIfIsEqualArtifact(artifactsInGroup.get(i), group.get(i));
        }
    }

    @Test
    public void testIfGetUserArtifactsReturnsProperGroup() throws SQLException {
        when(resultSet.next()).thenReturn(true, true, false);

        Group<ArtifactModel> expected = new Group<>("artifacts");
        expected.add(new ArtifactModel("Coconut", "White", 22));
        expected.add(new ArtifactModel("Orange", "Juicy", 33));

        when(resultSet.getString("name"))
                .thenReturn(expected.get(0).getName())
                .thenReturn(expected.get(1).getName());

        when(resultSet.getString("descr"))
                .thenReturn(expected.get(0).getDescription())
                .thenReturn(expected.get(1).getDescription());

        when(resultSet.getInt("price"))
                .thenReturn(expected.get(0).getPrice())
                .thenReturn(expected.get(1).getPrice());

        Group<ArtifactModel> group = dao.getUserArtifacts(4);
        for (int i = 0; i < group.size(); i++) {
            checkIfIsEqualArtifact(expected.get(i), group.get(i));
        }
    }

    private void checkIfIsEqualArtifact(ArtifactModel a, ArtifactModel b) {
        assertEquals(a.getName(), b.getName());
        assertEquals(a.getDescription(), b.getDescription());
        assertEquals(a.getPrice(), b.getPrice());
    }
}