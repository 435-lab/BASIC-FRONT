package MainPanel;

import Board.BoardUI;
import DisasterActionTips.DisasterActionPanel;
import DisasterActionTips.DisasterImagePanel;
import news.NewsPanel;

import javax.swing.*;
import java.awt.*;

public class MainApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private BoardUI boardUIPanel;
    private DisasterImagePanel disasterImagePanel;
    private DisasterActionPanel disasterActionPanel;

    public MainApp() {
        setTitle("SFD");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(179, 224, 225));



        // 재난행동요령 패널 및 ImageCardPanel 생성
        disasterActionPanel = new DisasterActionPanel(this, mainPanel, cardLayout);
//        ImageCardPanel imageCardPanel = disasterActionPanel.getImageCardPanel(); // ImageCardPanel 가져오기

        // 재난 이미지 패널 생성 및 ImageCardPanel 전달
        disasterImagePanel = new DisasterImagePanel(disasterActionPanel, mainPanel, cardLayout);

        // WeatherPanel과 JScrollPane 설정
        JLayeredPane rightPanel = WeatherUIBuilder.createRightPanel();
        WeatherPanel weatherPanel = new WeatherPanel(rightPanel, mainPanel, cardLayout, disasterImagePanel);
        weatherPanel.setPreferredSize(new Dimension(1400, 2715));

        JScrollPane scrollPane = new JScrollPane(weatherPanel);
        scrollPane.setPreferredSize(new Dimension(1400, 1440));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(new Color(179, 224, 255));
        scrollPane.setBackground(new Color(179, 224, 255));
        weatherPanel.setBackground(new Color(179, 224, 255));

        // 제보 게시판 패널 생성
        boardUIPanel = new BoardUI(this);

        // 메인 패널에 패널들 추가
        mainPanel.add(scrollPane, "WeatherPanel");
        mainPanel.add(disasterImagePanel, "DisasterImagePanel");
        mainPanel.add(disasterActionPanel, "DisasterActionPanel");
        mainPanel.add(boardUIPanel, "BoardUI");

        // 뉴스 패널 추가
        NewsPanel newsPanel = new NewsPanel();
        mainPanel.add(newsPanel, "NewsPanel");

        // 링크 패널 생성 및 추가 (항상 상단에 위치)
        LinkPanel linkPanel = new LinkPanel(mainPanel, cardLayout);
        add(linkPanel, BorderLayout.NORTH);

        // 메인 패널 추가
        add(mainPanel, BorderLayout.CENTER);
        pack();
        setSize(1700, 1080);

        // 초기 화면을 WeatherPanel로 설정
        cardLayout.show(mainPanel, "WeatherPanel");
    }

    // BoardUI의 로그인 상태를 업데이트하는 메서드
    public void updateBoardUIWithLogin(String userId, String jwtToken) {
        boardUIPanel.setLoggedIn(true, userId, jwtToken);
        boardUIPanel.revalidate();
        boardUIPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }

    public void showDisasterActionPanel(String disasterType) {
        disasterActionPanel.showDisasterPanel(disasterType);
        cardLayout.show(mainPanel, "DisasterActionPanel");
    }

    public static class LinkPanel extends JPanel {
        public LinkPanel(JPanel mainPanel, CardLayout cardLayout) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));
            setBackground(Color.WHITE);

            int buttonWidth = 220;
            int buttonHeight = 100;

            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Image/mainLogo.jpeg"));
            Image logoImage = logoIcon.getImage();
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

            logoButton.addActionListener(e -> cardLayout.show(mainPanel, "WeatherPanel"));
            add(logoButton);

            String[] linkTexts = {"최신 기사", "재난행동요령", "재난 지도", "제보 게시판"};
            for (String text : linkTexts) {
                JButton linkButton = new JButton(text);
                linkButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
                linkButton.setFocusPainted(false);
                linkButton.setBorderPainted(false);
                linkButton.setContentAreaFilled(false);
                linkButton.setFont(new Font("Arial", Font.BOLD, 20));
                linkButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

                if (text.equals("재난행동요령")) {
                    linkButton.addActionListener(e -> cardLayout.show(mainPanel, "DisasterActionPanel"));
                } else if (text.equals("최신 기사")) {
                    linkButton.addActionListener(e -> cardLayout.show(mainPanel, "NewsPanel"));
                } else if (text.equals("제보 게시판")) {
                    linkButton.addActionListener(e -> cardLayout.show(mainPanel, "BoardUI"));
                }

                linkButton.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        linkButton.setText("<html><u>" + text + "</u></html>");
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        linkButton.setText(text);
                    }
                });

                add(linkButton);
            }
        }
    }
}
