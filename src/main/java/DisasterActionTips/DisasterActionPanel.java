package DisasterActionTips;

import MainPanel.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class DisasterActionPanel extends JPanel {
    private ImageCardPanel imageCardPanel;
    private JLabel titleLabel;

    public DisasterActionPanel(MainApp app, JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new BorderLayout());
        setBackground(new Color(179, 224, 255));

        // 타이틀 패널 추가
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(179, 224, 255));
        titleLabel = new JLabel("재난 행동 요령", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // 이미지 버튼 패널 추가
        addButtonsPanel();

        // ImageCardPanel 추가
        imageCardPanel = new ImageCardPanel();
        add(imageCardPanel, BorderLayout.CENTER);

        // 재난 패널 초기화
        initializeDisasterPanels();
    }

    private void addButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 8, 10, 5));
        buttonsPanel.setBackground(Color.WHITE);

        String[] disasterKRNames = {"한파", "호우", "지진", "화재", "산불", "폭염", "감염병", "태풍"};
        String[] disasterNames = {"ColdWave", "Downpour", "Earthquake", "Fire", "ForestFires", "HeatWave", "InfectiousDiseases", "Typhoon"};

        for (int i = 0; i < disasterNames.length; i++) {
            JPanel itemPanel = createImageButtonWithLabel(disasterNames[i], disasterKRNames[i]);
            buttonsPanel.add(itemPanel);
        }

        add(buttonsPanel, BorderLayout.NORTH);
    }

    private JPanel createImageButtonWithLabel(String disasterName, String disasterKRName) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);

        JButton imageButton = createImageButton(disasterName);

        JLabel disasterLabel = new JLabel(disasterKRName, SwingConstants.CENTER);
        disasterLabel.setFont(new Font("Arial", Font.BOLD, 17));
        disasterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        itemPanel.add(disasterLabel, BorderLayout.NORTH);
        itemPanel.add(imageButton, BorderLayout.CENTER);

        return itemPanel;
    }

    private JButton createImageButton(String disasterName) {
        String imagePathJpg = "/Image/DActionImages/" + disasterName + ".jpg";
        String imagePathPng = "/Image/DActionImages/" + disasterName + ".png";

        URL imageUrl = getClass().getResource(imagePathJpg);
        if (imageUrl == null) {
            imageUrl = getClass().getResource(imagePathPng);
        }

        ImageIcon icon;
        if (imageUrl != null) {
            icon = new ImageIcon(imageUrl);
        } else {
            System.err.println("이미지를 로드할 수 없습니다: " + disasterName);
            icon = new ImageIcon(new BufferedImage(150, 150, BufferedImage.TYPE_INT_RGB));
        }

        Image image = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JButton imageButton = new JButton(new ImageIcon(image));
        imageButton.setBorderPainted(false);
        imageButton.setContentAreaFilled(false);
        imageButton.setFocusPainted(false);
        imageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imageButton.setBackground(Color.WHITE);

        imageButton.addActionListener(e -> showDisasterPanel(disasterName));

        return imageButton;
    }

    public void initializeDisasterPanels() {
        // 재난 패널을 ImageCardPanel에 추가
        imageCardPanel.addImagePanel("ColdWave", new String[]{"/Image/TipsImages/ColdWaveTips.jpg"});
        imageCardPanel.addImagePanel("Downpour", new String[]{"/Image/TipsImages/DownpourTips.jpg"});
        imageCardPanel.addImagePanel("Earthquake", new String[]{"/Image/TipsImages/Earthquake1.jpg", "/Image/TipsImages/Earthquake2.jpg"});
        imageCardPanel.addImagePanel("Fire", new String[]{"/Image/TipsImages/FireTips1.jpg", "/Image/TipsImages/FireTips2.jpg", "/Image/TipsImages/FireTips3.jpg", "/Image/TipsImages/FireTips4.jpg", "/Image/TipsImages/FireTips5.jpg"});
        imageCardPanel.addImagePanel("ForestFires", new String[]{"/Image/TipsImages/ForestFire1.png", "/Image/TipsImages/ForestFire2.png"});
        imageCardPanel.addImagePanel("HeatWave", new String[]{"/Image/TipsImages/HeatWaveTips.jpg"});
        imageCardPanel.addImagePanel("InfectiousDiseases", new String[]{"/Image/TipsImages/InfectiousDiseases1.jpg", "/Image/TipsImages/InfectiousDiseases2.jpg", "/Image/TipsImages/InfectiousDiseasesTips1.jpg"});
        imageCardPanel.addImagePanel("Typhoon", new String[]{"/Image/TipsImages/TyphoonTips.jpg"});
    }

    public void showDisasterPanel(String disasterType) {
        System.out.println("DisasterActionPanel에서 패널 전환 시도: " + disasterType);
        imageCardPanel.showPanel(disasterType);
        titleLabel.setText(disasterType + " 행동 요령");
        titleLabel.revalidate();
        titleLabel.repaint();
        imageCardPanel.revalidate();
        imageCardPanel.repaint();
        System.out.println("DisasterActionPanel에서 패널 전환 완료: " + disasterType);
    }
}
