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

public class MeasureConverter {

    private MeasureConverter(){}
    
    /**
     * Parse a string measure into int
     * @param pMeasure the measure value in string
     * @return the measure value in int, or 0 if string is not parseable
     */
    public static int getIntMeasureFromString(String pMeasure) {
        int measure;

        try {
            measure = Integer.parseInt(pMeasure);
        } catch (NumberFormatException e) {
            measure = 0;
        }

        return measure;
    }

}
