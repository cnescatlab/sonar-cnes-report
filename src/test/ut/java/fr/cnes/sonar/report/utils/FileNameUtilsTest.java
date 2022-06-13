package fr.cnes.sonar.report.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;

public class FileNameUtilsTest {

    @Test
    public void validFilename() {
        final String expected = "Project_test@S0,a)";
        final String actual = FileNameUtils.replaceNonValidFileNameCharacter("Project_test@S0,a)");
        assertEquals(expected, actual);
    }

    @Test
    public void invalidFilename() {
        final String expected = "--------";
        final String actual = FileNameUtils.replaceNonValidFileNameCharacter("/<>?*|:\\");
        assertEquals(expected, actual);
    }
}
