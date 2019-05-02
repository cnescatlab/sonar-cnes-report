package fr.cnes.sonar.plugin;

import fr.cnes.sonar.report.providers.AbstractDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PluginStringManager {
    private static Properties properties = new Properties();
    protected static final Logger LOGGER = Logger.getLogger(PluginStringManager.class.getCanonicalName());
    static {

        final ClassLoader classLoader = AbstractDataProvider.class.getClassLoader();

        // load properties file as a stream
                try (
        InputStream input = classLoader.getResourceAsStream("plugin.properties")){
            if(input!=null) {
                // load properties from the stream in an adapted structure
                properties.load(input);
            }
        } catch (
        IOException e) {
            // it logs all the stack trace
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static String getProperty(String p){
        return properties.getProperty(p);
    }
}
