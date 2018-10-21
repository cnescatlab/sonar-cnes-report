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

package fr.cnes.sonar.report.exceptions;

import org.junit.Test;

public class ExceptionsTest {

    private static final String TEXT = "blabla";

    @Test(expected = BadExportationDataTypeException.class)
    public void BadExportationDataTypeExceptionTest() throws BadExportationDataTypeException {
        throw new BadExportationDataTypeException();
    }

    @Test(expected = BadSonarQubeRequestException.class)
    public void BadSonarQubeRequestExceptionTest() throws BadSonarQubeRequestException {
        throw new BadSonarQubeRequestException(TEXT);
    }

    @Test(expected = SonarQubeException.class)
    public void SonarQubeExceptionTest() throws SonarQubeException {
        throw new SonarQubeException(TEXT);
    }

    @Test(expected = UnknownQualityGateException.class)
    public void UnknownQualityGateExceptionTest() throws UnknownQualityGateException {
        throw new UnknownQualityGateException(TEXT);
    }

}
