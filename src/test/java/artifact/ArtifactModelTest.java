package artifact;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArtifactModelTest {

    private ArtifactModel artifact;

    @Before
    public void setup() {
        artifact = new ArtifactModel("testName", "testDescription", 15);
    }

    @Test
    public void constructorShouldInitializeCorrectFields() {
        assertEquals("testName", artifact.getName());
        assertEquals("testDescription", artifact.getDescription());
        assertEquals(new Integer(15), artifact.getPrice());
        assertEquals(false, artifact.getUsageStatus());
    }

    @Test
    public void settersShouldChangeValuesProperly() {
        artifact.setName("Spoon");
        artifact.setDescription("Golden spoon");
        artifact.setPrice(40);
        artifact.setUsageStatus(true);

        assertEquals("Spoon", artifact.getName());
        assertEquals("Golden spoon", artifact.getDescription());
        assertEquals(new Integer(40), artifact.getPrice());
        assertEquals(true, artifact.getUsageStatus());
    }

    @Test
    public void toStringShouldFormatProperly() {
        String expected = String.format("%s\n%s", artifact.getName(), artifact.getDescription());
        assertEquals(expected, artifact.toString());
    }
}