import javax.swing.*;
import java.awt.*;

public class DisasterActionPanel extends JPanel {

    private JPanel tipsImagePanel; // 큰 이미지를 표시하는 패널
    private JButton[] imageButtons; // 이미지 버튼 배열
    private Color backgroundColor; // 고정된 배경색

    public DisasterActionPanel() {
        setLayout(new BorderLayout());
        backgroundColor = new Color(173, 216, 230); // 고정된 배경색 (하늘색)

        setBackground(backgroundColor); // 메인 페이지와 동일한 배경색 설정

        // 테이블 생성
        DisasterActionTipsImagePanel tablePanel = new DisasterActionTipsImagePanel(this::updateDisplayedImages, backgroundColor);
        imageButtons = tablePanel.getImageButtons(); // 이미지 버튼 배열 가져오기
        add(tablePanel, BorderLayout.NORTH);

        // 큰 이미지를 표시하는 패널 생성
        tipsImagePanel = new TipsImagePanel("/Image/TipsImages/ColdWaveTips.jpg", backgroundColor);
        JScrollPane scrollPane = new JScrollPane(tipsImagePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 스크롤 속도 조정
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setUnitIncrement(16); // 기본 값보다 빠르게 설정 (8이 기본값)

        add(scrollPane, BorderLayout.CENTER);

        // 처음 로드될 때 "한파" 이미지를 기본으로 설정
        updateDisplayedImages(new String[]{"/Image/TipsImages/ColdWaveTips.jpg"});
    }

    private void updateDisplayedImages(String[] imagePaths) {
        tipsImagePanel.removeAll(); // 기존 이미지를 제거

        // 새로운 큰 이미지를 표시
        for (String imagePath : imagePaths) {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                // 이미지 크기 설정 (이미지마다 다른 크기 설정 가능)
                int newWidth = 600; // 기본 너비 설정
                int newHeight = (newWidth * icon.getIconHeight()) / icon.getIconWidth(); // 비율에 따른 높이 계산

                // 특정 이미지에 대해 크기 조정
                if (imagePath.contains("ForestFire")) {
                    newWidth = 1400; // 예시: 산불 관련 이미지는 1200px로 설정
                } else if (imagePath.contains("InfectiousDiseases")) {
                    newWidth = 1000; // 예시: 감염병 관련 이미지는 1000px로 설정
                }
                newHeight = (newWidth * icon.getIconHeight()) / icon.getIconWidth(); // 비율에 따른 높이 계산

                // 이미지 크기 조정
                Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                // 조정된 이미지를 JLabel에 설정
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                tipsImagePanel.add(imageLabel);
                tipsImagePanel.add(Box.createRigidArea(new Dimension(0, 20))); // 이미지 사이의 간격 추가
            } else {
                System.err.println("이미지를 로드할 수 없습니다: " + imagePath);
            }
        }
        revalidate();
        repaint();
    }
}
