package MainPanel;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ReportsPanel extends JPanel {

    private JLabel summaryLabel;
    private JLabel subtitleLabel;

    public ReportsPanel() {
        setLayout(new GridLayout(2, 1));  // 상위 패널을 2행 1열로 설정
        setBackground(Color.WHITE);
        // 부제목 라벨을 위한 패널
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setPreferredSize(new Dimension(1000, 60));
        subtitlePanel.setLayout(new BorderLayout());  // 레이아웃 설정
        subtitleLabel = new JLabel("재난 제보 게시판 AI 기반 요약", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        subtitleLabel.setForeground(new Color(3, 108, 211));

        // 여기서 EmptyBorder로 여백 설정
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));  // 상단 10px, 하단 10px 여백

        subtitlePanel.setBackground(Color.WHITE);
        subtitlePanel.add(subtitleLabel, BorderLayout.CENTER);  // 부제목 라벨 추가


        // 요약 데이터를 표시할 라벨을 위한 패널
        JPanel summaryPanel = new JPanel();
        subtitlePanel.setPreferredSize(new Dimension(1000, 150));
        summaryPanel.setLayout(new BorderLayout());  // 레이아웃 설정
        summaryLabel = new JLabel("요약 데이터를 불러오는 중...", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 23));
        summaryLabel.setForeground(new Color(3, 108, 211));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);  // 요약 라벨 추가

        // 두 패널을 상위 패널에 추가
        add(subtitlePanel);
        add(summaryPanel);

        // 데이터를 받아오는 메서드 호출
        getSummaryData();
    }

    private void getSummaryData() {
        try {
            // GET 요청
            URL url = new URL("http://192.168.0.101:8081/summarize");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            // JSON 응답 파싱
            JSONObject jsonResponse = new JSONObject(content.toString());
            JSONObject choicesObject = jsonResponse.getJSONArray("choices").getJSONObject(0);
            String summary = choicesObject.getJSONObject("message").getString("content");

            // UI 스레드에서 라벨 업데이트
            // 기존의 summary 데이터를 HTML로 변환하는 예제
            SwingUtilities.invokeLater(() -> {
                String formattedSummary = "<html><center>" + summary.replace("\n", "<br>") + "</center></html>";
                summaryLabel.setText(formattedSummary);
            });

        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> summaryLabel.setText("데이터를 불러오지 못했습니다."));
        }
    }

    // summaryLabel을 외부에서 접근할 수 있도록 getter 메서드 추가
    public JLabel getSummaryLabel() {
        return summaryLabel;
    }
}
