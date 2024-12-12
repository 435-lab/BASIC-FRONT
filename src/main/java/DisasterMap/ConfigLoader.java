package DisasterMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("config.properties 파일을 찾을 수 없습니다.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("설정 파일 로드 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
