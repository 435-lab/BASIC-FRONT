package org.example.Board;

import org.example.login.LoginUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class BoardUI extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton searchButton;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private int currentPage = 1; // 현재 페이지
    private int totalPages = 1; // 총 페이지 수
    private int itemsPerPage = 10; // 페이지당 게시글 수
    private Vector<Vector<Object>> allData = new Vector<>();
    private Vector<Vector<Object>> filteredData = new Vector<>(); // 검색된 데이터를 저장하는 벡터
    private ReportApiClient apiClient;
    private JButton loginButton;
    private JButton writeButton;
    private JButton updateButton;  // 게시글 수정 버튼
    private JButton deleteButton;  // 게시글 삭제 버튼
    private boolean isLoggedIn = false; // 로그인 상태
    private String currentUser = null; // 현재 로그인한 사용자 ID
    private String jwToken;

    public BoardUI() {
        apiClient = new ReportApiClient();
        initComponents();
        setTitle("제보 게시판");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("제보 게시판", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // 검색 패널
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));
        searchField = new JTextField(50);
        searchButton = new JButton("검색");
        searchPanel.setBorder(BorderFactory.createEmptyBorder(40, 10, 0, 10));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
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

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(400);
        table.getColumnModel().getColumn(2).setPreferredWidth(600);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.setRowHeight(40);

        JScrollPane scrollPane = new JScrollPane(table);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(50, 200, 100, 200));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // 게시글 더블 클릭 리스너 추가
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        showPostDetails(selectedRow);  // 게시글 상세 보기 다이얼로그 표시
                    }
                }
            }
        });

        // 페이지 네비게이션 패널
        JPanel pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        prevButton = new JButton("이전");
        nextButton = new JButton("다음");
        pageLabel = new JLabel("1 / 1");
        pagePanel.add(prevButton);
        pagePanel.add(pageLabel);
        pagePanel.add(nextButton);
        mainPanel.add(pagePanel, BorderLayout.SOUTH);

        // 게시글 작성 버튼
        writeButton = new JButton("게시글 작성");
        writeButton.setEnabled(isLoggedIn);
        writeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoggedIn) {
                    showWriteDialog();
                } else {
                    JOptionPane.showMessageDialog(BoardUI.this, "로그인이 필요합니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 게시글 수정 버튼
        updateButton = new JButton("게시글 수정");
        updateButton.setEnabled(isLoggedIn);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    showUpdateDialog(selectedRow); // 게시글 수정 다이얼로그 표시
                } else {
                    JOptionPane.showMessageDialog(BoardUI.this, "수정할 게시글을 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 게시글 삭제 버튼
        deleteButton = new JButton("게시글 삭제");
        deleteButton.setEnabled(isLoggedIn);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int confirm = JOptionPane.showConfirmDialog(BoardUI.this, "선택한 게시글을 삭제하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int postId = (int) model.getValueAt(selectedRow, 0); // 번호 가져오기
                        apiClient.deleteReport(postId); // 게시글 삭제 API 호출
                        loadAllData(); // 데이터 다시 불러오기
                    }
                } else {
                    JOptionPane.showMessageDialog(BoardUI.this, "삭제할 게시글을 선택하세요.", "알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        // 로그인 버튼
        loginButton = new JButton("로그인");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoggedIn) {
                    setLoggedIn(false,null , null);  // 로그아웃 처리
                    JOptionPane.showMessageDialog(null, "로그아웃 하였습니다" +
                            ".");
                } else {
                    dispose();
                    showLoginDialog();  // 로그인 다이얼로그 표시
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(writeButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 50));
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        loadAllData();

        // 검색 버튼 리스너
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // 페이지 버튼 리스너
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    updateTable();
                }
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentPage < totalPages) {
                    currentPage++;
                    updateTable();
                }
            }
        });
    }

    private void loadAllData() {
        allData = apiClient.getAllReports();

        // report_id를 기준으로 내림차순 정렬 (가장 큰 report_id가 먼저 나오도록)
        Collections.sort(allData, new Comparator<Vector<Object>>() {
            @Override
            public int compare(Vector<Object> o1, Vector<Object> o2) {
                int reportId1 = Integer.parseInt(o1.get(0).toString()); // report_id는 0번째 인덱스에 있다고 가정
                int reportId2 = Integer.parseInt(o2.get(0).toString());

                return Integer.compare(reportId2, reportId1); // 내림차순 정렬
            }
        });

        filteredData = new Vector<>(allData);  // 검색을 위한 복사본
        updatePageInfo();
        updateTable();
    }


    private void performSearch() {
        String searchText = searchField.getText().toLowerCase();
        filteredData.clear();  // 검색 결과 초기화

        for (Vector<Object> row : allData) {
            String title = row.get(1).toString().toLowerCase();  // 제목 컬럼
            String content = row.get(2).toString().toLowerCase();  // 내용 컬럼
            String author = row.get(3).toString().toLowerCase();  // 작성자 컬럼

            // 검색어가 제목, 내용, 작성자에 포함된 경우
            if (title.contains(searchText) || content.contains(searchText) || author.contains(searchText)) {
                filteredData.add(row);
            }
        }

        currentPage = 1;  // 검색 후 첫 페이지로 이동
        updatePageInfo();
        updateTable();
    }

    private void updatePageInfo() {
        totalPages = (int) Math.ceil(filteredData.size() / (double) itemsPerPage);
        if (totalPages == 0) totalPages = 1; // 최소 한 페이지는 있어야 함
        pageLabel.setText(currentPage + " / " + totalPages);
        prevButton.setEnabled(currentPage > 1); // 첫 페이지에서 이전 버튼 비활성화
        nextButton.setEnabled(currentPage < totalPages); // 마지막 페이지에서 다음 버튼 비활성화
    }

    private void updateTable() {
        model.setRowCount(0);  // 테이블 초기화
        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, filteredData.size());
        for (int i = start; i < end; i++) {
            Vector<Object> row = filteredData.get(i);

            // 작성일을 YYYY-MM-DD 형식으로 포맷
            String originalDate = row.get(4).toString();
            String formattedDate = formatDate(originalDate);
            row.set(4, formattedDate);

            model.addRow(row);
        }
        updatePageInfo();
    }

    // 작성일을 YYYY-MM-DD로 포맷하는 메서드
    private String formatDate(String originalDate) {
        return LocalDate.parse(originalDate.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
    }

    // 게시글 상세보기 다이얼로그 표시
    private void showPostDetails(int selectedRow) {
        Vector<Object> rowData = filteredData.get((currentPage - 1) * itemsPerPage + selectedRow);
        String title = (String) rowData.get(1);
        String content = (String) rowData.get(2);

        JDialog detailDialog = new JDialog(this, "게시글 상세", true);
        detailDialog.setSize(600, 400);
        detailDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel(new GridBagLayout());
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
        JScrollPane scrollPane = new JScrollPane(contentArea);
        contentPanel.add(scrollPane, gbc);

        detailDialog.add(contentPanel, BorderLayout.CENTER);

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> detailDialog.dispose());
        detailDialog.add(closeButton, BorderLayout.SOUTH);

        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }

    // 게시글 수정 다이얼로그 표시
    private void showUpdateDialog(int selectedRow) {
        JDialog updateDialog = new JDialog(this, "게시글 수정", true);
        updateDialog.setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 현재 게시글의 제목과 내용 불러오기
        String currentTitle = (String) model.getValueAt(selectedRow, 1);
        String currentContent = (String) model.getValueAt(selectedRow, 2);

        // 제목 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("제목:"), gbc);
        JTextField titleField = new JTextField(currentTitle, 20);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // 내용 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("내용:"), gbc);
        JTextArea contentArea = new JTextArea(currentContent, 10, 20);
        gbc.gridx = 1;
        panel.add(new JScrollPane(contentArea), gbc);

        // 확인 및 취소 버튼
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // 버튼 이벤트 처리
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTitle = titleField.getText();
                String newContent = contentArea.getText();
                if (!newTitle.isEmpty() && !newContent.isEmpty()) {
                    int postId = (int) model.getValueAt(selectedRow, 0); // 수정할 게시글 ID 가져오기
                    apiClient.updateReport(postId, newTitle, newContent);  // 3개의 인자만 전달
                    updateDialog.dispose();
                    loadAllData(); // 새로운 데이터를 불러와 테이블 업데이트
                } else {
                    JOptionPane.showMessageDialog(updateDialog, "제목과 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        updateDialog.add(panel);
        updateDialog.setLocationRelativeTo(this);
        updateDialog.setVisible(true);
    }

    private void showWriteDialog() {
        // 게시글 작성 다이얼로그 구현
        JDialog writeDialog = new JDialog(this, "게시글 작성", true);
        writeDialog.setSize(600, 400);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 제목 필드
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("제목:"), gbc);
        JTextField titleField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(titleField, gbc);

        // 내용 필드
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("내용:"), gbc);
        JTextArea contentArea = new JTextArea(10, 20);
        gbc.gridx = 1;
        panel.add(new JScrollPane(contentArea), gbc);

        // 확인 및 취소 버튼
        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("확인");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        // 버튼 이벤트 처리
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                String content = contentArea.getText();

                // 현재 로그인된 사용자의 ID를 작성자 정보로 사용
                String author = currentUser;  // 로그인된 사용자 정보

                if (!title.isEmpty() && !content.isEmpty() && author != null) {
                    // 게시글 생성 시 로그인된 사용자의 ID를 작성자(author)로 설정
                    apiClient.createReport(title, content);  // 서버로 전달
                    writeDialog.dispose();
                    loadAllData(); // 새로운 데이터를 불러와 테이블 업데이트
                } else {
                    JOptionPane.showMessageDialog(writeDialog, "제목, 내용 및 작성자 정보가 필요합니다.", "경고", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                writeDialog.dispose();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        writeDialog.add(panel);
        writeDialog.setLocationRelativeTo(this);
        writeDialog.setVisible(true);
    }


    private void showLoginDialog() {
        // 로그인 다이얼로그 표시 (LoginUI 실행)
        LoginUI loginUI = new LoginUI();
        loginUI.setVisible(true);
    }

    public void setLoggedIn(boolean isLoggedIn, String currentUser, String jwtToken) {
        this.isLoggedIn = isLoggedIn;
        this.currentUser = currentUser;
        this.jwToken = jwtToken;

        // JWT 토큰이 null이 아닐 경우에만 설정
        if (jwtToken != null) {
            apiClient.setJwtToken(jwtToken);  // JWT 토큰 설정
        }

        updateLoginButton();
        writeButton.setEnabled(isLoggedIn);
        updateButton.setEnabled(isLoggedIn);
        deleteButton.setEnabled(isLoggedIn);
    }

    private void handleLogout() {
        setLoggedIn(false, null, null);
        apiClient.clearJwtToken();  // JWT 토큰 삭제
    }

    private void initComponents(){
        loginButton = new JButton("로그인");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isLoggedIn) {
                    handleLogout();
                } else {
                    dispose();
                    showLoginDialog();
                }
            }
        });
    }

    private void updateLoginButton() {
        if (isLoggedIn) {
            loginButton.setText(currentUser + " (로그아웃)");
        } else {
            loginButton.setText("로그인");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BoardUI().setVisible(true));
    }
}
