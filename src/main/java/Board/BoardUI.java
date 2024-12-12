package Board;

import MainPanel.MainApp;
import login.LoginUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Vector;

public class BoardUI extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton searchButton;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private int currentPage = 1;
    private int totalPages = 1;
    private int itemsPerPage = 10;
    private Vector<Vector<Object>> allData = new Vector<>();
    private Vector<Vector<Object>> filteredData = new Vector<>();
    private ReportApiClient apiClient;
    private JButton loginButton;
    private JButton writeButton;
    private JButton updateButton;
    private JButton deleteButton;
    private boolean isLoggedIn = false;
    private String currentUser = null;
    private String jwToken;
    private MainApp app;

    public BoardUI(MainApp app) {
        this.app = app;
        apiClient = new ReportApiClient();
        initComponents();
        setLayout(new BorderLayout());
        setBackground(new Color(179, 224, 255));

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("제보 게시판", SwingConstants.CENTER);
        topPanel.setBackground(new Color(179, 224, 255));
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 35));
        titleLabel.setForeground(new Color(3, 108, 211));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // 검색 패널
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        searchField = new JTextField(40);
//        searchField.setPreferredSize(new Dimension(70,40));
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
        searchButton = new JButton("검색");
        searchPanel.setFont(new Font("맑은 고딕", Font.PLAIN, 22));
        searchButton.setPreferredSize(new Dimension(100,40));

        searchPanel.setBorder(BorderFactory.createEmptyBorder(40, 10, 0, 10));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.setBackground(new Color(179, 224, 255));

        topPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // 테이블 모델 설정
        String[] columnNames = {"번호", "제목", "내용", "작성자", "작성일"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        Font tableFont = new Font("맑은 고딕",Font.PLAIN, 19);
        table.setFont(tableFont);
        table.setRowHeight(25);

        Font headerFont = new Font("맑은고딕", Font.BOLD, 20);
        table.getTableHeader().setFont(headerFont);

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(50, 200, 100, 200));
        tablePanel.setBackground(new Color(179, 224, 255));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // 게시글 더블 클릭 리스너 추가
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        showPostDetails(selectedRow);
                    }
                }
            }
        });

        // 페이지 네비게이션 패널
        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        prevButton = new JButton("이전");
        nextButton = new JButton("다음");
        pageLabel = new JLabel("1 / 1");
        pagePanel.setBackground(Color.WHITE);
        pagePanel.add(prevButton);
        pagePanel.add(pageLabel);
        pagePanel.add(nextButton);
        mainPanel.add(pagePanel, BorderLayout.SOUTH);

        // 게시글 작성 버튼
        writeButton = new JButton("게시글 작성");
        writeButton.setPreferredSize(new Dimension(100, 40));
        writeButton.setEnabled(isLoggedIn);
        writeButton.addActionListener(e -> {
            if (isLoggedIn) {
                showWriteDialog();
            } else {
                JOptionPane.showMessageDialog(BoardUI.this, "로그인이 필요합니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 게시글 수정 버튼
        updateButton = new JButton("게시글 수정");
        updateButton.setPreferredSize(new Dimension(100, 40));
        updateButton.setEnabled(false);
        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String author = (String) model.getValueAt(selectedRow, 3);
                if (isLoggedIn && currentUser.equals(author)) {
                    showUpdateDialog(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(BoardUI.this, "수정 권한이 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(BoardUI.this, "수정할 게시글을 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 게시글 삭제 버튼
        deleteButton = new JButton("게시글 삭제");
        deleteButton.setPreferredSize(new Dimension(100, 40));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String author = (String) model.getValueAt(selectedRow, 3);
                if (isLoggedIn && currentUser.equals(author)) {
                    int confirm = JOptionPane.showConfirmDialog(BoardUI.this, "선택한 게시글을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int postId = (int) model.getValueAt(selectedRow, 0);
                        apiClient.deleteReport(postId);
                        loadAllData();
                    }
                } else {
                    JOptionPane.showMessageDialog(BoardUI.this, "삭제 권한이 없습니다.", "알림", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(BoardUI.this, "삭제할 게시글을 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.addActionListener(e -> {
            if (isLoggedIn) {
                setLoggedIn(false, null, null);
                JOptionPane.showMessageDialog(null, "로그아웃 하였습니다.");
            } else {
                showLoginDialog();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(writeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 215));
        buttonPanel.setBackground(new Color(179, 224, 255));
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadAllData();

        searchButton.addActionListener(e -> performSearch());

        prevButton.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateTable();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateTable();
            }
        });

        // 테이블 선택 리스너 추가
        table.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String author = (String) model.getValueAt(selectedRow, 3);
                updateButton.setEnabled(isLoggedIn && currentUser.equals(author));
                deleteButton.setEnabled(isLoggedIn && currentUser.equals(author));
            }
        });
    }

    private void initComponents() {
        searchField = new JTextField(20);
        searchButton = new JButton("검색");
        prevButton = new JButton("이전");
        nextButton = new JButton("다음");
        pageLabel = new JLabel("1 / 1");
    }

    private void loadAllData() {
        allData = apiClient.getAllReports();
        Collections.sort(allData, (o1, o2) -> {
            int reportId1 = Integer.parseInt(o1.get(0).toString());
            int reportId2 = Integer.parseInt(o2.get(0).toString());
            return Integer.compare(reportId2, reportId1);
        });
        filteredData = new Vector<>(allData);
        updatePageInfo();
        updateTable();
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        filteredData.clear();
        for (Vector<Object> row : allData) {
            String title = row.get(1).toString().toLowerCase();
            String content = row.get(2).toString().toLowerCase();
            String author = row.get(3).toString().toLowerCase();
            if (title.contains(searchText) || content.contains(searchText) || author.contains(searchText)) {
                filteredData.add(row);
            }
        }
        currentPage = 1;
        updatePageInfo();
        updateTable();
    }

    private void updatePageInfo() {
        totalPages = (int) Math.ceil(filteredData.size() / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        pageLabel.setText(currentPage + " / " + totalPages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < totalPages);
    }

    private void updateTable() {
        model.setRowCount(0);
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredData.size());
        for (int i = start; i < end; i++) {
            Vector<Object> row = filteredData.get(i);
            model.addRow(row);
            String author = (String) row.get(3);
            boolean isAuthor = author.equals(currentUser);
            updateButton.setEnabled(isLoggedIn && isAuthor);
            deleteButton.setEnabled(isLoggedIn && isAuthor);
        }
        updatePageInfo();
    }

    private void showPostDetails(int selectedRow) {
        Vector<Object> rowData = filteredData.get((currentPage - 1) * itemsPerPage + selectedRow);
        String title = (String) rowData.get(1);
        String content = (String) rowData.get(2);

        JDialog detailDialog = new JDialog((Frame) null, "게시글 상세", true);
        detailDialog.setSize(600, 400);
        detailDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(179, 224, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("제목:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField titleField = new JTextField(title);
        titleField.setEditable(false);
        contentPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        contentPanel.add(new JLabel("작성자:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField authorField = new JTextField(rowData.get(3).toString());
        authorField.setEditable(false);
        contentPanel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        contentPanel.add(new JLabel("작성일:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JTextField dateField = new JTextField(formatDate(rowData.get(4).toString()));
        dateField.setEditable(false);
        contentPanel.add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        contentPanel.add(new JLabel("내용:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea contentArea = new JTextArea(content);
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentPanel.add(new JScrollPane(contentArea), gbc);

        detailDialog.add(contentPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> detailDialog.dispose());
        detailDialog.add(closeButton, BorderLayout.SOUTH);

        detailDialog.setLocationRelativeTo(null);
        detailDialog.setVisible(true);
    }

    private void showUpdateDialog(int selectedRow) {
        JDialog updateDialog = new JDialog((Frame) null, "게시글 수정", true);
        updateDialog.setSize(600, 400);
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(179, 224, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String currentTitle = (String) model.getValueAt(selectedRow, 1);
        String currentContent = (String) model.getValueAt(selectedRow, 2);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("제목:"), gbc);
        JTextField titleField = new JTextField(currentTitle, 20);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("내용:"), gbc);
        JTextArea contentArea = new JTextArea(currentContent, 10, 20);
        gbc.gridx = 1;
        panel.add(new JScrollPane(contentArea), gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(179, 224, 255));
        JButton confirmButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        confirmButton.addActionListener(e -> {
            String newTitle = titleField.getText();
            String newContent = contentArea.getText();
            if (!newTitle.isEmpty() && !newContent.isEmpty()) {
                int postId = (int) model.getValueAt(selectedRow, 0);
                apiClient.updateReport(postId, newTitle, newContent);
                updateDialog.dispose();
                loadAllData();
            } else {
                JOptionPane.showMessageDialog(updateDialog, "제목과 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> updateDialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateDialog.add(panel);
        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }


    private void showWriteDialog() {
        // 부모 없이 새로운 JDialog 생성하여 독립적인 게시물 작성 팝업 창 생성
        JDialog writeDialog = new JDialog((Frame) null, "게시글 작성", true); // 모달 다이얼로그로 설정
        writeDialog.setSize(600, 400);
        writeDialog.setBackground(new Color(179, 224, 255));
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(179, 224, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("제목:"), gbc);
        JTextField titleField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("내용:"), gbc);
        JTextArea contentArea = new JTextArea(10, 20);
        gbc.gridx = 1;
        panel.add(new JScrollPane(contentArea), gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(179, 224, 255));
        JButton confirmButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // 확인 버튼 클릭 시 새 게시물 생성 및 다이얼로그 종료
        confirmButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            if (!title.isEmpty() && !content.isEmpty()) {
                // API를 통해 게시물 생성 요청
                apiClient.createReport(title, content);
                writeDialog.dispose();
                loadAllData();
            } else {
                JOptionPane.showMessageDialog(writeDialog, "제목과 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 취소 버튼 클릭 시 다이얼로그 종료
        cancelButton.addActionListener(e -> writeDialog.dispose());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        writeDialog.add(panel);
        writeDialog.setLocationRelativeTo(this); // BoardUI 중앙에 표시
        writeDialog.setVisible(true);
    }


    private void showLoginDialog() {
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        LoginUI loginUI = new LoginUI(parentFrame, app, this); // MainApp 인스턴스 전달
        loginUI.setVisible(true);
    }

    public void setLoggedIn(boolean isLoggedIn, String currentUser, String jwtToken) {
        this.isLoggedIn = isLoggedIn;
        this.currentUser = currentUser;
        this.jwToken = jwtToken;

        if (jwtToken != null) {
            apiClient.setJwtToken(jwtToken);
        }

        SwingUtilities.invokeLater(() -> {
            updateLoginButton();
            writeButton.setEnabled(isLoggedIn);
            updateButton.setEnabled(isLoggedIn);
            deleteButton.setEnabled(isLoggedIn);

            revalidate();
            repaint();
        });
    }

    private void updateLoginButton() {
        if (isLoggedIn) {
            loginButton.setText(currentUser + " (로그아웃)");
        } else {
            loginButton.setText("로그인");
        }
    }

    private String formatDate(String originalDate) {
        return LocalDate.parse(originalDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
    }
}
