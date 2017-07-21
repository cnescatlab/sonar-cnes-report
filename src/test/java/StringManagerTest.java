import fr.cnes.sonar.report.input.StringManager;
import org.junit.Test;

import java.util.MissingResourceException;

import static org.junit.Assert.assertEquals;

/**
 * Test for the StringManager class
 * @author begarco
 */
public class StringManagerTest {

    /**
     * Test string to verify bad inputs
     */
    private static final String NOT_EXIST = "I_DO_NOT_EXIST";

    /**
     * Assert that the same StringManager instance is returned each time
     * StringManager.getInstance() is called.
     */
    @Test
    public void singletonUniquenessTest() {
        StringManager sm1 = StringManager.getInstance();
        StringManager sm2 = StringManager.getInstance();

        assertEquals(sm1, sm2);
    }

    /**
     * Assert that you can use the main StringManager method 'string()' statically
     * without prior initialization.
     */
    @Test
    public void simpleStringRequestTest() {
        assertEquals("Type", StringManager.string("header.type"));
    }

    /**
     * Assert that an incorrect string requested to the StringManager
     * returned a MissingResourceException.
     */
    @Test(expected= MissingResourceException.class)
    public void unknownStringRequestTest() {
        StringManager.string(NOT_EXIST);
    }

}
