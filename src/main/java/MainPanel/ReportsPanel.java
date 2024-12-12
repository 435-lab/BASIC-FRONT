package MainPanel;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReportsPanel extends JPanel {

    private JLabel summaryLabel;
    private JLabel subtitleLabel;
    private String gptUrl;

    public ReportsPanel() {
        // GPTConfigManager를 사용하여 gpt.url 값을 읽어옴
        GPTConfigManager configManager = new GPTConfigManager();
        gptUrl = configManager.getProperty("gpt.url");

        setLayout(new GridLayout(2, 1));  // 상위 패널을 2행 1열로 설정
        setBackground(Color.WHITE);
        Dimension fixedSize = new Dimension(1000, 450); // 원하는 크기로 조정
        setPreferredSize(fixedSize);
        setMinimumSize(fixedSize);
        setMaximumSize(fixedSize);

        // 부제목 라벨을 위한 패널
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setPreferredSize(new Dimension(1000, 50));
        subtitlePanel.setLayout(new BorderLayout());
        subtitleLabel = new JLabel("재난 제보 게시판 AI 기반 요약", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        subtitleLabel.setForeground(new Color(3, 108, 211));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        subtitlePanel.setBackground(Color.WHITE);
        subtitlePanel.add(subtitleLabel, BorderLayout.NORTH);

        // 요약 데이터를 표시할 라벨을 위한 패널
        JPanel summaryPanel = new JPanel();
        summaryPanel.setPreferredSize(new Dimension(1000, 400)); // 패널 크기 설정
        summaryPanel.setLayout(new BorderLayout());

        // 라벨 생성 및 텍스트 정렬 설정
        summaryLabel = new JLabel("요약 데이터를 불러오는 중...", SwingConstants.CENTER);
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 23));
        summaryLabel.setForeground(new Color(3, 108, 211));
        summaryLabel.setHorizontalAlignment(SwingConstants.CENTER); // 좌우 중앙 정렬
        summaryLabel.setVerticalAlignment(SwingConstants.CENTER);   // 상하 중앙 정렬


        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.add(summaryLabel, BorderLayout.NORTH);

        // 두 패널을 상위 패널에 추가
        add(subtitlePanel);
        add(summaryPanel);

        // 데이터를 받아오는 메서드 호출
        getSummaryData();
    }

    private void getSummaryData() {
        try {
            // `gptUrl`을 사용하여 요청
            URL url = new URL(gptUrl);
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
            SwingUtilities.invokeLater(() -> {
                String formattedSummary = "<html><center>" + summary.replace("\n", "<br>") + "</center></html>";
                summaryLabel.setText(formattedSummary);
            });

        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> summaryLabel.setText("데이터를 불러오지 못했습니다."));
        }
    }

    public JLabel getSummaryLabel() {
        return summaryLabel;
    }
}
