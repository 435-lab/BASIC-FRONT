package org.example.register;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.login.LoginUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Register {
    private RegisterUI ui;
    private NetworkManager networkManager;
    private ConfigManager configManager;

    public Register() {
        configManager = new ConfigManager();
        String serverUrl = configManager.getProperty("server.url");
        networkManager = new NetworkManager(serverUrl);

        ui = new RegisterUI();
        ui.setRegisterButtonListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterButtonClick();
            }
        });
    }

    private void handleRegisterButtonClick() {
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
                new LoginUI(); // LoginScreen 클래스가 있다고 가정
            } else {
                JOptionPane.showMessageDialog(null, "회원가입 실패: " + message, "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "회원가입 중 오류가 발생했습니다: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Register());
    }
}