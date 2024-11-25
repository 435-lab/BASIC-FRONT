package MainPanel;

import DisasterActionTips.DisasterActionPanel;
import DisasterActionTips.DisasterImagePanel;

import javax.swing.*;
import java.awt.*;

public class WeatherPanel extends JPanel {
    private JPanel imageCardPanel;
    private final JPanel newsTitlePanel;
    private JPanel WeatherTitlePanel;
    private JLabel temperatureLabel;
    private JLabel weatherDescriptionLabel;
    private JLabel humidityLabel;
    private JLabel weatherIconLabel;
    private JComboBox<String> regionComboBox;
    private ReportsPanel reportsPanel;  // ReportsPanel 인스턴스
    private JPanel imagePanel;
    private GoBoardPanel goBoardPanel;
    private JPanel daTitlePanel;
    private DisasterImagePanel disasterImagePanel;

    public WeatherPanel(JLayeredPane rightPanel, JPanel mainPanel, CardLayout cardLayout, DisasterImagePanel disasterImagePanel) {

        // 날씨 정보를 표시할 라벨 초기화
        temperatureLabel = new JLabel("온도: 불러오는 중...");
        weatherDescriptionLabel = new JLabel("날씨: 불러오는 중...");
        humidityLabel = new JLabel("습도: 불러오는 중...");
        weatherIconLabel = new JLabel();

        // DisasterActionPanel 및 DisasterImagePanel 생성
        DisasterActionPanel disasterActionPanel = new DisasterActionPanel(null, mainPanel, cardLayout);
        disasterImagePanel = new DisasterImagePanel(disasterActionPanel, mainPanel, cardLayout);
        disasterImagePanel.setPreferredSize(new Dimension(1000, 400));

        // 지역 선택 콤보박스 초기화
        regionComboBox = new JComboBox<>(RegionUtil.REGION_MAP.keySet().toArray(new String[0]));
        regionComboBox.setMaximumSize(new Dimension(300, 40));
        regionComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        regionComboBox.setSelectedItem("서울특별시");

        // 콤보박스의 이벤트 리스너 설정
        regionComboBox.addActionListener(e -> {
            String selectedRegion = (String) regionComboBox.getSelectedItem();
            String city = RegionUtil.REGION_MAP.get(selectedRegion);
            updateWeather(city); // 지역이 선택될 때 날씨 업데이트
        });

        // 레이아웃 설정 및 패널 구성
        setLayout(new GridBagLayout());
        setBackground(new Color(179, 224, 255));

        // 상단에 reportsPanel 추가 (상단에서 30픽셀 떨어지게 설정)
        reportsPanel = new ReportsPanel();  // ReportsPanel 인스턴스 생성
        reportsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcReports = new GridBagConstraints();
        gbcReports.gridx = 0;  // 첫 번째 열
        gbcReports.gridy = 0;  // 상단에 배치
        gbcReports.gridwidth = 2;  // 두 열을 모두 차지
        gbcReports.anchor = GridBagConstraints.NORTH;
        gbcReports.weightx = 1.0;
        gbcReports.fill = GridBagConstraints.NONE;
        gbcReports.insets = new Insets(20, 0, 20, 0);  // 상단20픽셀, 하단 20픽셀 여백 설정
        add(reportsPanel, gbcReports);

        // 왼쪽 패널 생성 및 추가
        JPanel leftPanel = WeatherUIBuilder.createLeftPanel(this);
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 2;
        gbcLeft.weightx = 0.5;
        gbcLeft.weighty = 1.0;
        gbcLeft.anchor = GridBagConstraints.EAST;
        gbcLeft.fill = GridBagConstraints.NONE;
        gbcLeft.insets = new Insets(0, 0, 0, 0);
        add(leftPanel, gbcLeft);

        // 오른쪽 패널 추가
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 1;
        gbcRight.gridy = 2;
        gbcRight.weightx = 0.5;
        gbcRight.weighty = 1.0;
        gbcRight.anchor = GridBagConstraints.WEST;
        gbcRight.fill = GridBagConstraints.NONE;
        gbcRight.insets = new Insets(0, 0, 0, 0);
        add(rightPanel, gbcRight);

        // 2번째 행에 상단에 부제목 추가
        WeatherTitlePanel = new WeatherTitlePanel();
        GridBagConstraints gbcWeatherTitle = new GridBagConstraints();
        gbcWeatherTitle.gridx = 0;
        gbcWeatherTitle.gridy = 2;
        gbcWeatherTitle.gridwidth = 2;
        gbcWeatherTitle.weightx = 1.0;
        gbcWeatherTitle.weighty = 1.0;
        gbcWeatherTitle.anchor = GridBagConstraints.NORTH;
        gbcWeatherTitle.insets = new Insets(0, 0, 0, 0);
        gbcWeatherTitle.fill = GridBagConstraints.NONE;
        add(WeatherTitlePanel, gbcWeatherTitle);

        // 2번째 행 하단에 GoBoardPanel 추가
        goBoardPanel = new GoBoardPanel(mainPanel, cardLayout);
        GridBagConstraints gbcGoBoard = new GridBagConstraints();
        gbcGoBoard.gridx = 0;
        gbcGoBoard.gridy = 2;
        gbcGoBoard.gridwidth = 2;
        gbcGoBoard.weightx = 1.0;
        gbcGoBoard.anchor = GridBagConstraints.SOUTH;
        gbcGoBoard.insets = new Insets(20, 0, 0, 0);
        gbcGoBoard.fill = GridBagConstraints.NONE;
        add(goBoardPanel, gbcGoBoard);

        // reportsPanel의 summaryLabel에 접근하여 텍스트 설정
        reportsPanel.getSummaryLabel().setText("Weather summary data loaded!");

        // 최신 기사 부제목 타이틀 패널 생성
        newsTitlePanel = new JPanel(new BorderLayout());
        GridBagConstraints gbcNewsTitle = new GridBagConstraints();
        newsTitlePanel.setPreferredSize(new Dimension(1000, 60));
        JLabel NewsTitleLabel = new JLabel("최신 기사", SwingConstants.CENTER);
        NewsTitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        NewsTitleLabel.setForeground(new Color(3, 108, 211));
        gbcNewsTitle.gridx = 0;
        gbcNewsTitle.gridy = 3;
        gbcNewsTitle.gridwidth = 2;
        gbcNewsTitle.weightx = 0;
        gbcNewsTitle.weighty = 0;
        gbcNewsTitle.insets = new Insets(20, 0, 0, 0);
        gbcNewsTitle.fill = GridBagConstraints.NONE;
        newsTitlePanel.setBackground(Color.WHITE);
        newsTitlePanel.add(NewsTitleLabel, BorderLayout.CENTER);
        add(newsTitlePanel, gbcNewsTitle);

        // NewsSummaryPanel 추가
        NewsSummaryPanel newsSummaryPanel = new NewsSummaryPanel();
        GridBagConstraints gbcNewsSummary = new GridBagConstraints();
        newsSummaryPanel.setPreferredSize(new Dimension(1000, 200));
        gbcNewsSummary.gridx = 0;
        gbcNewsSummary.gridy = 4;
        gbcNewsSummary.gridwidth = 2;
        gbcNewsSummary.insets = new Insets(0, 0, 10, 0);
        gbcNewsSummary.fill = GridBagConstraints.NONE;
        add(newsSummaryPanel, gbcNewsSummary);

        // DisasterActionImage의 부제목 타이틀 패널 생성
        daTitlePanel = new JPanel(new BorderLayout());
        GridBagConstraints gbcdaTitle = new GridBagConstraints();
        daTitlePanel.setPreferredSize(new Dimension(1000, 60));
        JLabel daTitleLabel = new JLabel("재난행동요령 바로가기", SwingConstants.CENTER);
        daTitleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        daTitleLabel.setForeground(new Color(3, 108, 211));
        gbcdaTitle.gridx = 0;
        gbcdaTitle.gridy = 5;
        gbcdaTitle.gridwidth = 2;
        gbcdaTitle.weightx = 0;
        gbcdaTitle.weighty = 0;
        gbcdaTitle.insets = new Insets(20, 0, 0, 0);
        gbcdaTitle.fill = GridBagConstraints.NONE;
        daTitlePanel.setBackground(Color.WHITE);
        daTitlePanel.add(daTitleLabel, BorderLayout.CENTER);
        add(daTitlePanel, gbcdaTitle);

        // DisasterImagePanel 추가
        GridBagConstraints gbcDisasterImages = new GridBagConstraints();
        gbcDisasterImages.gridx = 0;
        gbcDisasterImages.gridy = 7;
        gbcDisasterImages.gridwidth = 2;
        gbcDisasterImages.insets = new Insets(0, 0, 20, 0);
        gbcDisasterImages.fill = GridBagConstraints.NONE;
        add(disasterImagePanel, gbcDisasterImages);

        revalidate();
        repaint();

        // 기본적으로 서울특별시의 날씨 정보를 초기화
        updateWeather("Seoul");
    }

    // 날씨 정보를 업데이트하는 메서드
    public void updateWeather(String city) {
        WeatherUpdater.updateWeather(this, city);  // WeatherUpdater를 통해 날씨 정보 업데이트
    }

    // 라벨들을 반환하는 Getter 메서드
    public JLabel getTemperatureLabel() {
        return temperatureLabel;
    }

    public JLabel getWeatherDescriptionLabel() {
        return weatherDescriptionLabel;
    }

    public JLabel getHumidityLabel() {
        return humidityLabel;
    }

    public JLabel getWeatherIconLabel() {
        return weatherIconLabel;
    }

    public JComboBox<String> getRegionComboBox() {
        return regionComboBox;
    }
}
