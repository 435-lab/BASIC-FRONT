package MainPanel;

import javax.swing.*;
import java.awt.*;

public class GoBoardPanel extends JButton {

    // 생성자에서 CardLayout과 mainPanel을 받아옴
    public GoBoardPanel(JPanel mainPanel, CardLayout cardLayout) {
        // 버튼의 레이아웃 설정
        setLayout(new BorderLayout());

        // 라벨 추가 ("제보 게시판 바로가기")
        JLabel label = new JLabel("제보 게시판 바로가기", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setForeground(Color.WHITE);

        // 라벨을 버튼 중앙에 추가
        add(label, BorderLayout.CENTER);

        // 버튼 외관 설정 (패널처럼 보이도록)
        setBackground(new Color(3, 108, 211));  // 파란색 배경
        setOpaque(true);  // 불투명하게 설정
        setBorderPainted(false);  // 버튼 테두리 제거
        setFocusPainted(false);  // 포커스 표시 제거
        setContentAreaFilled(true);  // 버튼 배경 채우기
        setMargin(new Insets(0, 0, 0, 0));  // 텍스트와 경계 사이 여백 제거

        // 패널 크기 설정
        setPreferredSize(new Dimension(1000, 50));

        // 마우스 커서를 손가락 모양으로 변경
        setCursor(new Cursor(Cursor.HAND_CURSOR));  // 손가락 커서 설정

        // 버튼 클릭 이벤트 리스너 추가
        addActionListener(e -> cardLayout.show(mainPanel, "BoardUI"));  // "BoardUI"로 전환
    }
}