package fr.cnes.sonar.plugin.tools;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SonarPropertiesLoader {

    Properties properties;
    InputStream sonar;

    public SonarPropertiesLoader(Path path) throws IOException {
        this.properties = new Properties();
        loadSonarProperties(path);
    }

    public SonarPropertiesLoader() throws IOException{
        this.properties = new Properties();
    }
    public void getInputStream(Path path) throws IOException {
        this.sonar = Files.newInputStream(path);
    }

    public void loadProperties(InputStream input) throws IOException {
        this.properties.load(input);
    }

    public final Properties loadSonarProperties(Path sonarPropertiesPath)
            throws IOException {
        try {
            getInputStream(sonarPropertiesPath);
            if (this.sonar != null) {
                loadProperties(this.sonar);
            }
        } catch (IOException e) {
            // Could not read given path
            throw new IOException("-i : Could not find or open provided sonar.properties at: " + e.getMessage());
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