/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report;

import fr.cnes.sonar.report.utils.StringManager;
import org.junit.Test;

import java.util.MissingResourceException;

import static org.junit.Assert.assertEquals;

/**
 * Test for the StringManager class
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
        final StringManager sm1 = StringManager.getInstance();
        final StringManager sm2 = StringManager.getInstance();

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
