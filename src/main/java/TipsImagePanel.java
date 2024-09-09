import javax.swing.*;
import java.awt.*;

public class TipsImagePanel extends JPanel {

    public TipsImagePanel(String initialImagePath, Color backgroundColor) {
        setBackground(backgroundColor); // 큰 이미지 패널의 배경색 설정
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // 초기 이미지를 설정
        JLabel imageLabel = createImageLabel(initialImagePath, 600);
        add(imageLabel);
        add(Box.createRigidArea(new Dimension(0, 20))); // 이미지 사이의 간격 추가
    }

    public static JLabel createImageLabel(String imagePath, int newWidth) {
        ImageIcon icon = new ImageIcon(TipsImagePanel.class.getResource(imagePath));
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            int newHeight = (newWidth * icon.getIconHeight()) / icon.getIconWidth(); // 비율에 따른 높이 계산

            // 이미지 크기 조정
            Image scaledImage = icon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

            // 조정된 이미지를 JLabel에 설정
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            return imageLabel;
        } else {
            System.err.println("이미지를 로드할 수 없습니다: " + imagePath);
            return new JLabel(); // 이미지 로드 실패 시 빈 레이블 반환
        }
    }
}
