package login;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private String loginUrl;

    public ConfigManager() {
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("설정 파일을 찾을 수 없습니다.");
                return;
            }

            Properties prop = new Properties();
            prop.load(input);
            loginUrl = prop.getProperty("login.url");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getLoginUrl() {
        return loginUrl;
    }
}