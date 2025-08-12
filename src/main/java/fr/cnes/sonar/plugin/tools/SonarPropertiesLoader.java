package fr.cnes.sonar.plugin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

public class SonarPropertiesLoader {

    /** Logger of this class */
    private static final Logger LOGGER = Logger.getLogger(FileTools.class.getName());

    public Properties loadSonarProperties(Path sonarPropertiesPath)
            throws IOException {

        Properties properties = new Properties();
        try {
            InputStream sonar = Files.newInputStream(sonarPropertiesPath);
            if (sonar != null) {
                properties.load(sonar);
            }
        } catch (IOException e) {
            //LOGGER.log(Level.SEVERE, "-i : Could not find or open provided sonar.properties at: " + e.getMessage(), e);
            throw e;
        }
        return properties;
    }
}