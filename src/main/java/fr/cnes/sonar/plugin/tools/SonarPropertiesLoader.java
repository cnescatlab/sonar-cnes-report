package fr.cnes.sonar.plugin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.cnes.sonar.report.exceptions.InvalidSonarPropertiesException;

public class SonarPropertiesLoader {

    /** Logger of this class */
    private static final Logger LOGGER = Logger.getLogger(FileTools.class.getName());

    public Properties loadSonarProperties(Path sonarPropertiesPath)
            throws IOException, InvalidSonarPropertiesException {

        Properties properties = new Properties();
        try {
            InputStream sonar = Files.newInputStream(sonarPropertiesPath);
            if (sonar != null) {
                boolean hasCorrectProperties = false;
                properties.load(sonar);
                // Mandatory properties
                if ((properties.getProperty("sonar.host.url") != null)
                        && (properties.getProperty("sonar.projectKey") != null)) {
                    hasCorrectProperties = true;
                } else {
                    throw new InvalidSonarPropertiesException("sonar.host.url or sonar.projectKey missing.");
                }
                // Optional properties (Generally still wanted)
                properties.getProperty("sonar.projectName");
                properties.getProperty("sonar.login");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return properties;
    }
}