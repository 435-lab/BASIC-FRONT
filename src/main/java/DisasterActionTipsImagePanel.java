import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class DisasterActionTipsImagePanel extends JPanel {

    private JButton[] imageButtons; // 이미지 버튼을 저장할 배열

    public DisasterActionTipsImagePanel(Consumer<String[]> updateImagesCallback, Color highlightColor) {
        setLayout(new GridLayout(1, 8));  // 1행 8열의 그리드 레이아웃
        setBackground(Color.WHITE); // 배경색을 흰색으로 설정

        String[] disasterNames = {"한파", "호우", "지진", "화재", "산불", "폭염", "감염병", "태풍"};
        String[] imagePaths = {
                "/Image/DActionImages/ColdWave.jpg", "/Image/DActionImages/Downpour.jpg",
                "/Image/DActionImages/Earthquake.jpg", "/Image/DActionImages/Fire.png",
                "/Image/DActionImages/ForestFires.jpg", "/Image/DActionImages/HeatWave.jpg",
                "/Image/DActionImages/InfectiousDiseases.jpg", "/Image/DActionImages/Typhoon.jpg"
        };

        String[][] tipsImagePaths = {
                {"/Image/TipsImages/ColdWaveTips.jpg"},  // 한파
                {"/Image/TipsImages/DownpourTips.jpg"},  // 호우
                {
                        "/Image/TipsImages/Earthquake1.jpg",
                        "/Image/TipsImages/Earthquake2.jpg"   // 지진
                },
                {
                        "/Image/TipsImages/FireTips1.jpg",
                        "/Image/TipsImages/FireTips2.jpg",
                        "/Image/TipsImages/FireTips3.jpg",
                        "/Image/TipsImages/FireTips4.jpg",
                        "/Image/TipsImages/FireTips5.jpg"    // 화재
                },
                {
                        "/Image/TipsImages/ForestFire1.png",
                        "/Image/TipsImages/ForestFire2.png"   // 산불
                },
                {"/Image/TipsImages/HeatWaveTips.jpg"},  // 폭염
                {
                        "/Image/TipsImages/InfectiousDiseases1.jpg",
                        "/Image/TipsImages/InfectiousDiseases2.jpg",
                        "/Image/TipsImages/InfectiousDiseasesTips1.jpg" // 감염병
                },
                {"/Image/TipsImages/TyphoonTips.jpg"}  // 태풍
        };

        imageButtons = new JButton[disasterNames.length]; // 배열 크기 설정

        int fixedWidth = 150;
        int fixedHeight = 100;

        for (int i = 0; i < disasterNames.length; i++) {
            JPanel itemPanel = new JPanel();
            itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
            itemPanel.setBackground(Color.WHITE);

            JLabel disasterLabel = new JLabel(disasterNames[i], SwingConstants.CENTER);
            disasterLabel.setFont(new Font("Arial", Font.BOLD, 18));
            disasterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            ImageIcon icon = new ImageIcon(getClass().getResource(imagePaths[i]));  // 이미지 로드
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                System.err.println("이미지를 로드할 수 없습니다: " + imagePaths[i]);
            } else {
                // 고정된 크기로 이미지 조정
                Image image = icon.getImage();
                Image scaledImage = image.getScaledInstance(fixedWidth, fixedHeight, Image.SCALE_SMOOTH);
                JButton imageButton = new JButton(new ImageIcon(scaledImage));
                imageButton.setBorderPainted(false);
                imageButton.setFocusPainted(false);
                imageButton.setContentAreaFilled(false);
                imageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                imageButton.setBackground(Color.WHITE); // 버튼 배경색을 흰색으로 설정
                imageButton.setAlignmentX(Component.CENTER_ALIGNMENT);

                // 버튼의 ActionCommand로 이미지 경로 설정
                imageButton.setActionCommand(imagePaths[i]);

                // 이미지 버튼 클릭 시 큰 이미지로 업데이트
                String[] currentTipsImagePaths = tipsImagePaths[i];
                imageButton.addActionListener(e -> updateImagesCallback.accept(currentTipsImagePaths));

                JPanel borderPanel = new JPanel(new BorderLayout()); // 테두리 역할을 할 패널
                borderPanel.setBackground(Color.WHITE);
                borderPanel.add(imageButton, BorderLayout.CENTER);

                itemPanel.add(Box.createVerticalGlue()); // 상단 여백
                itemPanel.add(disasterLabel);
                itemPanel.add(Box.createRigidArea(new Dimension(0, 5))); // 텍스트와 이미지 사이의 간격
                itemPanel.add(borderPanel); // 테두리 패널 추가
                itemPanel.add(Box.createVerticalGlue()); // 하단 여백

                imageButtons[i] = imageButton; // 배열에 버튼 저장
            }

            add(itemPanel);
        }
    }

    public JButton[] getImageButtons() {
        return imageButtons;
    }
}