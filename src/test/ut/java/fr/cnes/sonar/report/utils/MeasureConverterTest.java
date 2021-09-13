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

package fr.cnes.sonar.report.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class MeasureConverterTest {

    @Test
    public void parseableStringMeasure() {
        String measure = "35";
        assertEquals(35, MeasureConverter.getIntMeasureFromString(measure));
    }

    @Test
    public void notParseableMeasure() {
        String measure = "abc";
        assertEquals(0, MeasureConverter.getIntMeasureFromString(measure));
        measure = "";
        assertEquals(0, MeasureConverter.getIntMeasureFromString(measure));
    }
    
}
