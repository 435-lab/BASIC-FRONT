import javax.swing.*;
import java.awt.CardLayout;  // CardLayout 임포트
import java.awt.BorderLayout;
import java.awt.Dimension;

public class mainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public mainApp() {
        setTitle("SFD");
        setSize(1600, 900);  // 윈도우 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout 설정
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // WeatherPanel과 JScrollPane 설정
        WeatherPanel weatherPanel = new WeatherPanel();
        weatherPanel.setPreferredSize(new Dimension(1600, 900));  // 패널 크기 설정

        JScrollPane scrollPane = new JScrollPane(weatherPanel);  // 스크롤 가능한 패널
        scrollPane.setPreferredSize(new Dimension(1600, 900));   // 스크롤 가능한 패널 크기 설정

        DisasterActionPanel disasterActionPanel = new DisasterActionPanel(); // 재난행동요령 패널

        mainPanel.add(scrollPane, "WeatherPanel");
        mainPanel.add(disasterActionPanel, "DisasterActionPanel");

        // 링크 패널 생성 및 추가 (항상 상단에 위치)
        LinkPanel linkPanel = new LinkPanel(mainPanel, cardLayout);
        add(linkPanel, BorderLayout.NORTH);

        // 메인 패널 추가
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            mainApp app = new mainApp();
            app.setVisible(true);
        });
    }
}
