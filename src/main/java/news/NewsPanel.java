package news;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.net.URI;

public class NewsPanel extends JPanel {
    private JPanel newsPanel;
    private JPanel paginationPanel;
    private int currentPage = 1;
    private final int recordsPerPage = 10;
    private int totalPages;
    private NewsDataManager dataManager;

    public NewsPanel() {
        initializeUI();
        dataManager = new NewsDataManager(this);
        loadNews();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 배경색 설정 (RGB: 179, 224, 255)
        Color backgroundColor = new Color(179, 224, 255);
        setBackground(backgroundColor);

        newsPanel = new JPanel();
        newsPanel.setLayout(new BoxLayout(newsPanel, BoxLayout.Y_AXIS));
        newsPanel.setBackground(backgroundColor); // 뉴스 패널의 배경색도 동일하게 설정
        newsPanel.setBorder(BorderFactory.createEmptyBorder(40, 10, 10, 10)); // 상단 여백 30px 추가

        paginationPanel = new JPanel();
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        paginationPanel.setBackground(Color.WHITE); // 페이지네이션 패널의 배경색 설정

        JScrollPane scrollPane = new JScrollPane(newsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(backgroundColor); // 스크롤 패널의 배경색 설정

        add(scrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
    }

    public void loadNews() {
        newsPanel.removeAll();
        paginationPanel.removeAll();
        dataManager.fetchNews(currentPage, recordsPerPage);
    }

    public void displayNews(java.util.List<NewsItem> newsItems) {
        for (NewsItem item : newsItems) {
            JPanel newsCard = createNewsCard(item);
            newsPanel.add(newsCard);
            newsPanel.add(Box.createRigidArea(new Dimension(0, 15))); // 5픽셀 간격 추가
        }
        updatePagination();
        newsPanel.revalidate();
        newsPanel.repaint();
    }

    private JPanel createNewsCard(NewsItem item) {
        JPanel newsCard = new JPanel(new BorderLayout());
        newsCard.setBorder(BorderFactory.createLineBorder(Color.white, 6));
        newsCard.setBackground(Color.WHITE); // 카드의 배경색은 흰색으로 유지
        newsCard.setPreferredSize(new Dimension(1000, 120)); // 높이를 150에서 120으로 줄임
        newsCard.setMaximumSize(new Dimension(1000, 120)); // 높이를 150에서 120으로 줄임
        newsCard.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 제목 패널
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(179, 224, 255)); // 연한 파란색 배경
        titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 패딩 약간 줄임

        // 시간 레이블
        JLabel timeLabel = new JLabel(item.getDateTime());
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 11)); // 폰트 크기 약간 줄임
        timeLabel.setForeground(Color.DARK_GRAY);
        timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        titlePanel.add(timeLabel, BorderLayout.NORTH);

        // 제목 레이블
        JLabel titleLabel = new JLabel(item.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // 폰트 크기 약간 줄임
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.setForeground(Color.black);
        titleLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(item.getLink()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        newsCard.add(titlePanel, BorderLayout.NORTH);

        // 내용 패널
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // 패딩 약간 줄임

        JTextArea contentArea = new JTextArea(item.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setBackground(Color.WHITE);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 13)); // 폰트 크기 약간 줄임

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createEmptyBorder());

        MouseWheelListener mouseWheelListener = e -> {
            JScrollPane parentScrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, newsCard);
            if (parentScrollPane != null) {
                parentScrollPane.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, parentScrollPane));
            }
        };
        // 내용 패널과 스크롤 패널에 마우스 휠 리스너 추가
        contentScrollPane.addMouseWheelListener(mouseWheelListener);
        contentPanel.addMouseWheelListener(mouseWheelListener);

        contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        newsCard.add(contentPanel, BorderLayout.CENTER);

        return newsCard;
    }

    private void updatePagination() {
        if (totalPages > 0) {
            if (currentPage > 1) {
                addPageButton("<", currentPage - 1);
            }
            for (int i = 1; i <= totalPages; i++) {
                addPageButton(String.valueOf(i), i);
            }
            if (currentPage < totalPages) {
                addPageButton(">", currentPage + 1);
            }
        }
        paginationPanel.revalidate();
        paginationPanel.repaint();
    }

    private void addPageButton(String label, int page) {
        JButton pageButton = new JButton(label);
        pageButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pageButton.setForeground(Color.BLACK);
        pageButton.setContentAreaFilled(false);
        pageButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        if (page == currentPage) {
            pageButton.setForeground(new Color(179, 224, 255));
            pageButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            pageButton.setText(label);
        }
        pageButton.addActionListener(e -> {
            currentPage = page;
            loadNews();
        });
        paginationPanel.add(pageButton);
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
