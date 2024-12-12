package register;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.out.println("설정 파일을 찾을 수 없습니다: " + CONFIG_FILE);
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}