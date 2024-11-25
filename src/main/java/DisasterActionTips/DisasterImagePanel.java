package DisasterActionTips;

import MainPanel.MainApp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class DisasterImagePanel extends JPanel {
    private DisasterActionPanel disasterActionPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public DisasterImagePanel(DisasterActionPanel disasterActionPanel, JPanel mainPanel, CardLayout cardLayout) {
        System.out.println("DisasterImagePanel 생성됨");
        this.disasterActionPanel = disasterActionPanel;
        this.mainPanel = mainPanel;
        this.cardLayout = cardLayout;
        System.out.println("DisasterImagePanel 초기화 시작");
        setLayout(new GridLayout(2, 4, 10, 10));
        setPreferredSize(new Dimension(1000, 400));

        String[] disasterNames = {"ColdWave", "Downpour", "Earthquake", "Fire", "ForestFires", "HeatWave", "InfectiousDiseases", "Typhoon"};

        for (String disasterName : disasterNames) {
            JPanel itemPanel = createDisasterButton(disasterName);
            add(itemPanel);
        }
        System.out.println("DisasterImagePanel 초기화 완료");

    }

    private JPanel createDisasterButton(String disasterName) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setPreferredSize(new Dimension(200, 200));

        JLabel disasterLabel = new JLabel(disasterName, SwingConstants.CENTER);
        disasterLabel.setFont(new Font("Arial", Font.BOLD, 18));
        disasterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton imageButton = createImageButton(disasterName);
        // 버튼 클릭 시 DisasterActionPanel 전환
        imageButton.addActionListener(e -> {
            ((MainApp) SwingUtilities.getWindowAncestor(this)).showDisasterActionPanel(disasterName);
        });



        itemPanel.add(Box.createVerticalGlue());
        itemPanel.add(disasterLabel);
        itemPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        itemPanel.add(imageButton);
        itemPanel.add(Box.createVerticalGlue());

        return itemPanel;
    }

    private JButton createImageButton(String disasterName) {
        int fixedWidth = 150;
        int fixedHeight = 150;

        URL imageUrl = getClass().getResource("/Image/DActionImages/" + disasterName + ".jpg");
        if (imageUrl == null) {
            imageUrl = getClass().getResource("/Image/DActionImages/" + disasterName + ".png");
        }

        ImageIcon icon = (imageUrl != null) ? new ImageIcon(imageUrl) : new ImageIcon();
        if (icon.getIconWidth() == -1) {
            System.err.println("이미지를 로드할 수 없습니다: " + disasterName);
            icon = new ImageIcon(new BufferedImage(fixedWidth, fixedHeight, BufferedImage.TYPE_INT_RGB));
        }

        Image image = icon.getImage().getScaledInstance(fixedWidth, fixedHeight, Image.SCALE_SMOOTH);
        JButton imageButton = new JButton(new ImageIcon(image));
        imageButton.setBorderPainted(false);
        imageButton.setContentAreaFilled(false);
        imageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imageButton.setBackground(Color.WHITE);
        imageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        return imageButton;
    }
}