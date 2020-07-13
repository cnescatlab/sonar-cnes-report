package fr.cnes.sonar.report.utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UrlEncoderTest {

    /**
     * Test the encoding process & whitespaces partcular management
     */
    @Test
    public void encodeSpecialCharsAndWhitespace() {
        String initialString = "Test 4 Q&A ++";
        String expectedResult = "Test%204%20Q%26A%20%2B%2B";
        String urlEncodedString = UrlEncoder.urlEncodeString(initialString);
        assertEquals(urlEncodedString, expectedResult);
    }

}