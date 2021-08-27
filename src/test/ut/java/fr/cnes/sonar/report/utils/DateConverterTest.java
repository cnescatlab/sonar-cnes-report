package fr.cnes.sonar.report.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.format.DateTimeParseException;

import org.junit.Test;

public class DateConverterTest {

    @Test
    public void validSonarQubeDateToExcelDateTest() {
        final double expected = 44419.50796296296;
        final double actual = DateConverter.sonarQubeDateToExcelDate("2021-08-11T12:11:28+0000");
        assertEquals(expected, actual, 0.1);
    }

    @Test(expected = DateTimeParseException.class)
    public void faultySonarQubeDateToExcelDateTest() {
        DateConverter.sonarQubeDateToExcelDate("2021-08-11 12:11:28+0000");
    }
}
