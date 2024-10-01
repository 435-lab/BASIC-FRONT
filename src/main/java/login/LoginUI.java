package org.example.login;

import org.example.Board.BoardUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.register.Register;

public class LoginUI extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;
    private ConfigManager configManager;
    private NetworkManager networkManager;
    private String jwtToken;  // JWT 토큰 저장 변수

    public LoginUI() {
        configManager = new ConfigManager();
        networkManager = new NetworkManager(configManager.getLoginUrl());
        initializeUI();
    }

    private void initializeUI() {
        setTitle("로그인");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(173, 216, 230));

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(173, 216, 230));

        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, new GridBagConstraints());

        return mainPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.WEST;

        addComponentsToFormPanel(formPanel, gbc);

        return formPanel;
    }

    private void addComponentsToFormPanel(JPanel formPanel, GridBagConstraints gbc) {
        Font labelFont = new Font("맑은 고딕", Font.PLAIN, 20);

        addTitleToFormPanel(formPanel, gbc);
        addIdFieldToFormPanel(formPanel, gbc, labelFont);
        addPasswordFieldToFormPanel(formPanel, gbc, labelFont);
        addButtonsToFormPanel(formPanel, gbc);
    }

    private void addTitleToFormPanel(JPanel formPanel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("로그인");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);
    }

    private void addIdFieldToFormPanel(JPanel formPanel, GridBagConstraints gbc, Font labelFont) {
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setFont(labelFont);
        formPanel.add(idLabel, gbc);

        idField = new JTextField(30);
        idField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1;
        formPanel.add(idField, gbc);
    }

    private void addPasswordFieldToFormPanel(JPanel formPanel, GridBagConstraints gbc, Font labelFont) {
        gbc.gridy = 2;
        gbc.gridx = 0;
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setFont(labelFont);
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(30);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
    }

    private void addButtonsToFormPanel(JPanel formPanel, GridBagConstraints gbc) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = createStyledButton("로그인", new Color(173, 216, 230));
        loginButton.addActionListener(e -> loginUser());
        buttonPanel.add(loginButton);

        JButton registerButton = createStyledButton("회원가입", new Color(181, 181, 181));
        registerButton.addActionListener(e -> {
            dispose();  // 현재 창 닫기
            new Register();  // 회원가입 창 열기
        });
        buttonPanel.add(registerButton);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isArmed()) {
                    g.setColor(bgColor.darker());
                } else {
                    g.setColor(bgColor);
                }
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("맑은 고딕", Font.BOLD, 15));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    private void loginUser() {
        String id = idField.getText();
        String password = new String(passwordField.getPassword());

        Map<String, String> loginData = new HashMap<>();
        loginData.put("id", id);
        loginData.put("password", password);

        networkManager.sendDataToServer(loginData, this::handleLoginResponse);
    }

    // 로그인 응답을 처리하는 메서드
    private void handleLoginResponse(int responseCode, String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            boolean result = jsonNode.get("result").asBoolean();
            if (result) {
                String jwtToken = jsonNode.get("data").get("token").asText();  // JWT 토큰 가져오기
                String userId = jsonNode.get("data").get("id").asText();

                JOptionPane.showMessageDialog(null, "로그인 성공! 환영합니다, " + userId + "님", "성공", JOptionPane.INFORMATION_MESSAGE);

                SwingUtilities.invokeLater(() -> {
                    try {
                        BoardUI boardApp = new BoardUI();
                        boardApp.setLoggedIn(true, userId, jwtToken);  // JWT 토큰 전달
                        boardApp.setVisible(true);
                        dispose();  // LoginUI 창 닫기
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "게시판 화면을 열 수 없습니다: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(null, "로그인 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "응답 처리 중 오류 발생: " + e.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
