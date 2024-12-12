package DisasterActionTips;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ImageCardPanel extends JScrollPane {
    private CardLayout cardLayout;
    private JPanel cardContainer;
    private Map<String, JPanel> disasterPanels;

    public ImageCardPanel() {
        // CardLayout을 사용할 JPanel 생성
        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        disasterPanels = new HashMap<>();

        // JScrollPane 설정
        setViewportView(cardContainer);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
    }

    public void addImagePanel(String disasterType, String[] imagePaths) {
        if (disasterPanels.containsKey(disasterType)) {
            System.out.println(disasterType + " 패널이 이미 존재합니다.");
            return;
        }
        String[] lengthImagePaths = {
                "/Image/TipsImages/ForestFire1.png",
                "/Image/TipsImages/ForestFire2.png",
                "/Image/TipsImages/InfectiousDiseases1.jpg",
                "/Image/TipsImages/InfectiousDiseases2.jpg"
        };
        // 이미지들을 담을 JPanel 생성
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(179, 224, 255));

        for (String imagePath : imagePaths) {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                // 이미지 크기 조정
                int imageWidth = 900;
                int imageHeight = 1200;
                for (String lengthImagePath : lengthImagePaths){
                    if (imagePath.equals(lengthImagePath)){
                        imageWidth = 1400;
                        imageHeight = 900;
                    }
                }
                Image scaledImage = icon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                imageLabel.setPreferredSize(new Dimension(imageWidth, imageHeight)); // 고정 크기 설정
                panel.add(imageLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 10))); // 이미지 사이 간격
            } else {
                System.err.println("이미지를 로드할 수 없습니다: " + imagePath);
            }
        }

        // disasterType과 연결
        cardContainer.add(panel, disasterType);
        disasterPanels.put(disasterType, panel);
        System.out.println("스크롤 가능한 패널 추가됨: " + disasterType);
    }

    public void showPanel(String disasterType) {
        if (disasterPanels.containsKey(disasterType)) {
            cardLayout.show(cardContainer, disasterType);
            System.out.println("ImageCardPanel에서 패널 전환: " + disasterType);
        } else {
            System.err.println("해당 재난 패널이 없습니다: " + disasterType);
        }
    }
}
