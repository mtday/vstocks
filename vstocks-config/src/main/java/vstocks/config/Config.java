package vstocks.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import static java.util.Locale.ENGLISH;
import static java.util.Optional.ofNullable;

public enum Config {
    SERVER_PORT,
    SERVER_CONTEXT_PATH,
    SERVER_API_PATH,

    DB_URL,
    DB_DRIVER,
    DB_USER,
    DB_PASS,
    DB_MIN_IDLE,
    DB_MAX_POOL_SIZE,
    DB_IDLE_TIMEOUT,
    DB_CONNECTION_TIMEOUT,

    TWITTER_API_KEY,
    TWITTER_API_SECRET,
    TWITTER_API_BEARER,
    TWITTER_API_CALLBACK

    ;

    private static final String PRODUCTION_LOCATION = "/opt/vstocks/conf/system.properties";
    private static final String DEVELOPMENT_RESOURCE = "system.properties";

    private static final Properties PROPERTIES = loadProperties();

    public String getString() {
        return ofNullable(PROPERTIES.getProperty(getKey()))
                .orElseThrow(() -> new RuntimeException("Key missing from config: " + getKey()));
    }

    public int getInt() {
        return ofNullable(PROPERTIES.getProperty(getKey()))
                .map(Integer::parseInt)
                .orElseThrow(() -> new RuntimeException("Key missing from config: " + getKey()));
    }

    public long getLong() {
        return ofNullable(PROPERTIES.getProperty(getKey()))
                .map(Long::parseLong)
                .orElseThrow(() -> new RuntimeException("Key missing from config: " + getKey()));
    }

    public boolean getBoolean() {
        return ofNullable(PROPERTIES.getProperty(getKey()))
                .map(Boolean::valueOf)
                .orElseThrow(() -> new RuntimeException("Key missing from config: " + getKey()));
    }

    private String getKey() {
        return name().toLowerCase(ENGLISH).replaceAll("_", ".");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = getConfigInputStream()) {
            properties.load(inputStream);
        } catch (IOException ioException) {
            throw new RuntimeException("Unable to load system configuration file", ioException);
        }
        return properties;
    }

    private static InputStream getConfigInputStream() throws IOException {
        File productionConfigFile = new File(PRODUCTION_LOCATION);
        if (productionConfigFile.exists()) {
            return new FileInputStream(productionConfigFile);
        } else {
            URL developmentConfigFile = Config.class.getClassLoader().getResource(DEVELOPMENT_RESOURCE);
            if (developmentConfigFile != null) {
                return developmentConfigFile.openStream();
            }
        }
        throw new RuntimeException("Unable to find system configuration file at "
                + PRODUCTION_LOCATION + " or on classpath as " + DEVELOPMENT_RESOURCE);
    }
}
