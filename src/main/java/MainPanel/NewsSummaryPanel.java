package MainPanel;

import news.NewsItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NewsSummaryPanel extends JPanel {
    private static final int MAX_NEWS_ITEMS = 5; // 최대 뉴스 항목 개수 설정
    private Properties dbProperties;

    public NewsSummaryPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        loadDatabaseProperties();
        displayLatestNews();
    }

    private void loadDatabaseProperties() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                dbProperties.load(input);
            } else {
                JOptionPane.showMessageDialog(this, "설정 파일을 찾을 수 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "설정 파일을 읽을 수 없습니다: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayLatestNews() {
        List<NewsItem> newsItems = fetchLatestNews();
        for (NewsItem item : newsItems) {
            JLabel titleLabel = new JLabel(item.getTitle(), SwingConstants.CENTER); // 가운데 정렬
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
            titleLabel.setForeground(new Color(3, 108, 211).darker());
            titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 손가락 커서 설정
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // 컴포넌트 가운데 정렬

            titleLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new URI(item.getLink()));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(NewsSummaryPanel.this, "링크 열기 오류: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            add(titleLabel);
            add(Box.createVerticalStrut(10)); // 뉴스 제목 간격 설정
        }
    }

    private List<NewsItem> fetchLatestNews() {
        List<NewsItem> newsItems = new ArrayList<>();
        String query = "SELECT title, link FROM disaster_news ORDER BY date_time DESC LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, MAX_NEWS_ITEMS);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    newsItems.add(new NewsItem(
                            null, // dateTime은 필요하지 않으므로 null로 설정
                            rs.getString("title"),
                            rs.getString("link"),
                            null // content는 필요하지 않으므로 null로 설정
                    ));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "데이터베이스 오류: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return newsItems;
    }

    private Connection getConnection() throws SQLException {
        String jdbcUrl = dbProperties.getProperty("jdbc.url");
        String dbUser = dbProperties.getProperty("db.user");
        String dbPassword = dbProperties.getProperty("db.password");
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }
}
