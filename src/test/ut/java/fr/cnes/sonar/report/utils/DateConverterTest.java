package fr.cnes.sonar.report.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class DateConverterTest {

    @Test
    public void ValidSonarQubeDateToExcelDateTest() {
        final double expected = 44419.50796296296;
        final double actual = DateConverter.SonarQubeDateToExcelDate("2021-08-11T12:11:28+0000");
        assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void FaultySonarQubeDateToExcelDateTest() {
        DateConverter.SonarQubeDateToExcelDate("2021-08-11 12:11:28+0000");
    }
}
