package register;

import Board.BoardUI;
import MainPanel.MainApp;
import com.fasterxml.jackson.databind.JsonNode;
import login.LoginUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Register {
    private RegisterUI ui;
    private NetworkManager networkManager;
    private ConfigManager configManager;
    private BoardUI boardUI;
    private MainApp mainApp;

    public Register(Frame parent, MainApp mainApp, BoardUI boardUI) {
        System.out.println("Register 생성자 호출됨");
        this.mainApp = mainApp;
        this.boardUI = boardUI;
        configManager = new ConfigManager();
        String serverUrl = configManager.getProperty("server.url");
        networkManager = new NetworkManager(serverUrl);

        ui = new RegisterUI(parent, mainApp, boardUI); // MainApp, BoardUI 전달
//        ui.setRegisterButtonListener(e -> handleRegisterButtonClick());
        ui.setVisible(true);
    }

    private void handleRegisterButtonClick() {
        System.out.println("handleRegisterButtonClick 메소드 호출 됨");
        String password = new String(ui.getPassword());
        String passwordConfirm = new String(ui.getPasswordConfirm());

        if (!password.equals(passwordConfirm)) {
            JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, String> formData = new HashMap<>();
        formData.put("id", ui.getId());
        formData.put("password", password);
        formData.put("name", ui.getName());
        formData.put("birth", ui.getBirthDate());
        formData.put("gender", ui.getGender());

        try {
            JsonNode response = networkManager.sendPostRequest(formData);
            boolean result = response.get("result").asBoolean();
            String message = response.get("message").asText();

            if (result) {
                JOptionPane.showMessageDialog(null, "회원가입이 완료되었습니다.", "회원가입 성공", JOptionPane.INFORMATION_MESSAGE);
                ui.dispose();
                new LoginUI((Frame) ui.getParent(), mainApp, boardUI).setVisible(true); // MainApp, BoardUI 전달
            } else {
                JOptionPane.showMessageDialog(null, "회원가입 실패: " + message, "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "회원가입 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
