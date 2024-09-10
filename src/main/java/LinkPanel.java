import javax.swing.*;
import java.awt.*;

public class LinkPanel extends JPanel {

    public LinkPanel(JPanel mainPanel, CardLayout cardLayout) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        setBackground(Color.WHITE);

        // 버튼과 이미지의 목표 크기 설정
        int buttonWidth = 200;
        int buttonHeight = 60;

        // 로고 버튼 설정 (이미지 아이콘 사용)
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Image/mainLogo.jpeg"));
        Image logoImage = logoIcon.getImage();

        // 이미지의 원본 비율 유지하여 크기 조정
        double aspectRatio = (double) logoImage.getWidth(null) / logoImage.getHeight(null);
        int scaledWidth = buttonWidth;
        int scaledHeight = (int) (buttonWidth / aspectRatio);

        if (scaledHeight > buttonHeight) {
            scaledHeight = buttonHeight;
            scaledWidth = (int) (buttonHeight * aspectRatio);
        }

        Image scaledLogoImage = logoImage.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        JButton logoButton = new JButton(new ImageIcon(scaledLogoImage));
        logoButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
        logoButton.setFocusPainted(false);
        logoButton.setBorderPainted(false);
        logoButton.setContentAreaFilled(false);
        logoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 메인 페이지로 이동하는 액션 리스너 추가
        logoButton.addActionListener(e -> cardLayout.show(mainPanel, "WeatherPanel"));
        add(logoButton);

        // 링크 버튼들 생성
        String[] linkTexts = {"최신 기사", "재난행동요령", "재난 지도", "제보 게시판"};
        for (String text : linkTexts) {
            JButton linkButton = new JButton(text);
            linkButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
            linkButton.setFocusPainted(false);
            linkButton.setBorderPainted(false); // 테두리 제거
            linkButton.setContentAreaFilled(false); // 버튼 배경 제거
            linkButton.setFont(new Font("Arial", Font.BOLD, 18)); // 글씨 크기와 굵기 설정
            linkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // 특정 링크에 대해 별도의 액션 리스너 추가
            if (text.equals("재난행동요령")) {
                linkButton.addActionListener(e -> cardLayout.show(mainPanel, "DisasterActionPanel"));
            }

            // 마우스 커서 올렸을 때 밑줄 표시
            linkButton.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    linkButton.setText("<html><u>" + text + "</u></html>"); // 밑줄 추가
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    linkButton.setText(text); // 밑줄 제거
                }
            });

            add(linkButton);
        }
    }
}
