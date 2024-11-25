package MainPanel;

import javax.swing.*;
import java.awt.*;

public class WeatherTitlePanel extends JPanel {

    private JLabel subtitleLabel;

    public WeatherTitlePanel() {
        // 상위 패널 레이아웃을 2행 1열로 설정
        setLayout(new GridLayout(2, 1));
        setBackground(Color.WHITE);

        // 부제목 라벨을 위한 패널 생성
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setPreferredSize(new Dimension(1000, 60));
        subtitlePanel.setLayout(new BorderLayout());  // BorderLayout 설정
        subtitleLabel = new JLabel("현재 날씨 정보", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        subtitleLabel.setForeground(new Color(3, 108, 211));
        subtitlePanel.setBackground(Color.WHITE);
        subtitlePanel.add(subtitleLabel, BorderLayout.CENTER);  // 부제목 라벨 추가

        // subtitlePanel을 WeatherTitlePanel에 추가
        add(subtitlePanel);
    }

    // 부제목 라벨을 반환하는 메서드
    public JLabel getSubtitleLabel() {
        return subtitleLabel;
    }

    // 부제목 텍스트를 설정하는 메서드
    public void setSubtitleText(String text) {
        subtitleLabel.setText(text);
    }
}
