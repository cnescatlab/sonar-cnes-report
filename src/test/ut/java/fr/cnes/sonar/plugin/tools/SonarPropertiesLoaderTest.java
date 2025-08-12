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

package fr.cnes.sonar.plugin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.sonar.api.internal.apachecommons.io.IOUtils;

public class SonarPropertiesLoaderTest {

    Path testSonar;

    @Before
    public void createFakeProperties() {
        try {
            this.testSonar = Files.createTempFile("cnesreport", "");
            List<String> lines = List.of("sonar.host.url=http://biiiiiiiiiiim", "sonar.token=this_is_a_token",
                    "sonar.projectKey=this-is-a-project-key");
            Files.write(testSonar, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            assert (false);
        }
    }

    @Test
    public void testValidFile() throws IOException {
        try (InputStream input = Files.newInputStream(this.testSonar)) {
            assert (input != null);
        }
    }

    @Test(expected = IOException.class)
    public void testInvalidFile() throws IOException {
        SonarPropertiesLoader propLoader = new SonarPropertiesLoader(Paths.get("./this/is/a/nonsense/path"));
    }

    @Test
    public void testPropertiesLoading() throws IOException {
        SonarPropertiesLoader p = new SonarPropertiesLoader();

        InputStream in = IOUtils.toInputStream(
                "sonar.host.url=http://biiiiiiiiiiim\nsonar.token=this_is_a_token\nsonar.projectKey=this-is-a-project-key",
                "UTF-8");
        p.loadProperties(in);
        assertEquals("http://biiiiiiiiiiim", p.getSonarProperty("sonar.host.url"));
        assertEquals("this_is_a_token", (p.getSonarProperty("sonar.token")));
        assertEquals("this-is-a-project-key", (p.getSonarProperty("sonar.projectKey")));
        assertEquals("", (p.getSonarProperty("this-property-does-not-exist")));
    }
}
