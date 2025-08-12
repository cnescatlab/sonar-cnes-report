package fr.cnes.sonar.plugin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.Logger;

public class SonarPropertiesLoader {

    Properties properties;

    public SonarPropertiesLoader(Path path) throws IOException{

        loadSonarProperties(path);
    }

    /** Logger of this class */
    private static final Logger LOGGER = Logger.getLogger(FileTools.class.getName());

    public final Properties loadSonarProperties(Path sonarPropertiesPath)
            throws IOException {

        this.properties = new Properties();
        try {
            InputStream sonar = Files.newInputStream(sonarPropertiesPath);
            if (sonar != null) {
                this.properties.load(sonar);
            }
        } catch (IOException e) {
            // LOGGER.log(Level.SEVERE, "-i : Could not find or open provided
            // sonar.properties at: " + e.getMessage(), e);
            throw e;
        }
        return this.properties;
    }

    public String getSonarProperty(String propName) {
        String prop = this.properties.getProperty(propName);
        // We check that the property existed and was actually returned. If it is we
        // keep it as is.
        // If it's not, we assign "" to it for the following checks.
        prop = prop != null ? prop : "";
        return prop;
    }
}