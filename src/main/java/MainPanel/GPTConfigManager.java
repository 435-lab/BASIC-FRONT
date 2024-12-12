package MainPanel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GPTConfigManager {
    private Properties properties;

    public GPTConfigManager() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("설정 파일을 찾을 수 없습니다.");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("설정 파일을 로드하는 중 오류가 발생했습니다.");
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
