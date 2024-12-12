package news;

import javax.swing.*;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class NewsDataManager {
    private NewsPanel ui;
    private Properties dbProperties;

    public NewsDataManager(NewsPanel ui) {
        this.ui = ui;
        loadDatabaseProperties();
    }

    private void loadDatabaseProperties() {
        dbProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                JOptionPane.showMessageDialog(ui, "설정 파일을 찾을 수 없습니다.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dbProperties.load(input);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ui, "설정 파일을 읽을 수 없습니다: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void fetchNews(int currentPage, int recordsPerPage) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int start = (currentPage - 1) * recordsPerPage;

        try {
            conn = getConnection();
            int totalRecords = getTotalRecords(conn);
            int totalPages = (int) Math.ceil(totalRecords * 1.0 / recordsPerPage);
            ui.setTotalPages(totalPages);

            String query = "SELECT date_time, title, link, content FROM disaster_news ORDER BY date_time DESC LIMIT ?, ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, start);
            pstmt.setInt(2, recordsPerPage);
            rs = pstmt.executeQuery();

            List<NewsItem> newsItems = new ArrayList<>();
            while (rs.next()) {
                newsItems.add(new NewsItem(
                        rs.getString("date_time"),
                        rs.getString("title"),
                        rs.getString("link"),
                        rs.getString("content")
                ));
            }
            ui.displayNews(newsItems);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ui, "데이터베이스 오류: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    private Connection getConnection() throws SQLException {
        String jdbcUrl = dbProperties.getProperty("jdbc.url");
        String dbUser = dbProperties.getProperty("db.user");
        String dbPassword = dbProperties.getProperty("db.password");
        return DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    }

    private int getTotalRecords(Connection conn) throws SQLException {
        String countQuery = "SELECT COUNT(*) AS count FROM disaster_news";
        try (Statement countStmt = conn.createStatement();
             ResultSet countRs = countStmt.executeQuery(countQuery)) {
            countRs.next();
            return countRs.getInt("count");
        }
    }

    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(ui, "연결 종료 오류: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}