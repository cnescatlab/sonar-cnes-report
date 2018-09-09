package fr.cnes.sonar.tests;/*
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

import fr.cnes.sonar.report.exceptions.MalformedParameterException;
import fr.cnes.sonar.report.exceptions.MissingParameterException;
import fr.cnes.sonar.report.exceptions.UnknownParameterException;
import fr.cnes.sonar.report.utils.Params;
import fr.cnes.sonar.report.utils.ParamsFactory;
import org.junit.Test;

/**
 * Tests for Params and ParamsFactory classes
 * @author garconb
 */
public class ParamsTest {

    /**
     * Property for the id of the project
     */
    private static final String SONAR_PROJECT_ID = "--sonar.project.id";

    /**
     * Classical case
     * @throws Exception ...
     */
    @Test
    public void paramsCreationTest() throws Exception {
        final String [] args = {SONAR_PROJECT_ID, "bob", "--sonar.server", "alice"};

        final ParamsFactory paramsFactory = new ParamsFactory();

        final Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a mandatory parameter is missing, an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = MissingParameterException.class)
    public void paramsCreationMissingMandatoryParameterTest() throws Exception {
        final String [] args = {SONAR_PROJECT_ID, "bob"};

        final ParamsFactory paramsFactory = new ParamsFactory();

        final Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a parameter is malformed, for example, -- are missing
     * an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = MalformedParameterException.class)
    public void paramsCreationMalformedParameterTest() throws Exception {
        final String [] args = {"sonar.project.id", "bob"};

        final ParamsFactory paramsFactory = new ParamsFactory();

        final Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a parameter has no value,
     * an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = UnknownParameterException.class)
    public void paramsCreationMissingValueParameterTest() throws Exception {
        final String [] args = {SONAR_PROJECT_ID, "bob", "--sonar.server"};

        final ParamsFactory paramsFactory = new ParamsFactory();

        final Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

    /**
     * When a parameter is unknown,
     * an exception should be thrown
     * @throws Exception ...
     */
    @Test(expected = UnknownParameterException.class)
    public void paramsCreationUnknownParameterTest() throws Exception {
        final String [] args = {"--alice", "bob"};

        final ParamsFactory paramsFactory = new ParamsFactory();

        final Params params = paramsFactory.create(args);

        assert(params.isReliable());
    }

}
