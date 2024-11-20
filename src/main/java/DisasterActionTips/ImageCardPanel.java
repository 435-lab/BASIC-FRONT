package DisasterActionTips;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ImageCardPanel extends JPanel {
    private CardLayout cardLayout;
    private Map<String, JPanel> disasterPanels;

    public ImageCardPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);
        disasterPanels = new HashMap<>();
    }

    public void addImagePanel(String disasterType, String[] imagePaths) {
        if (disasterPanels.containsKey(disasterType)) {
            System.out.println(disasterType + " 패널이 이미 존재합니다.");
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(179, 224, 255));
        panel.setPreferredSize(new Dimension(800, 600)); // 패널의 고정 크기 설정

        for (String imagePath : imagePaths) {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                // 이미지 크기 조정
                Image scaledImage = icon.getImage().getScaledInstance(700, 500, Image.SCALE_SMOOTH); // 원하는 크기로 조정
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                imageLabel.setPreferredSize(new Dimension(700, 500)); // 이미지 레이블의 고정 크기 설정
                panel.add(imageLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10))); // 이미지 사이 간격 설정
            } else {
                System.err.println("이미지를 로드할 수 없습니다: " + imagePath);
            }
        }

        add(panel, disasterType);
        disasterPanels.put(disasterType, panel);
        System.out.println("패널 추가됨: " + disasterType);
    }

    public void showPanel(String disasterType) {
        if (disasterPanels.containsKey(disasterType)) {
            cardLayout.show(this, disasterType);
            System.out.println("ImageCardPanel에서 패널 전환: " + disasterType);
        } else {
            System.err.println("해당 재난 패널이 없습니다: " + disasterType);
        }
    }
}
