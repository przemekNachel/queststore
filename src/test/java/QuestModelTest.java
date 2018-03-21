
import org.junit.Before;
import org.junit.Test;
import quest.QuestModel;

import static org.junit.Assert.*;

public class QuestModelTest {

    private QuestModel quest;

    @Before
    public void setup() {
        quest = new QuestModel("Eat pillow", "Yummy", 20);
    }

    @Test
    public void constructorShouldSetValuesCorrectly() {
        assertEquals("Eat pillow", quest.getName());
        assertEquals("Yummy", quest.getDescription());
        assertEquals(new Integer(20), quest.getReward());
    }

    @Test
    public void settersShouldSetProperValue() {
        quest.setName("Clean up a room");
        quest.setDescription("Deep scrub");
        quest.setReward(66);

        assertEquals("Clean up a room", quest.getName());
        assertEquals("Deep scrub", quest.getDescription());
        assertEquals(new Integer(66), quest.getReward());
    }

}