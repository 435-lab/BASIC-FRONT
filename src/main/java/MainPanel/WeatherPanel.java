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
    private ReportsPanel reportsPanel;
    private JPanel imagePanel;
    private GoBoardPanel goBoardPanel;
    private JPanel daTitlePanel;
    private DisasterImagePanel disasterImagePanel;

    public WeatherPanel(JLayeredPane rightPanel, JPanel mainPanel, CardLayout cardLayout, DisasterImagePanel disasterImagePanel) {
        temperatureLabel = new JLabel("온도: 불러오는 중...");
        weatherDescriptionLabel = new JLabel("날씨: 불러오는 중...");
        humidityLabel = new JLabel("습도: 불러오는 중...");
        weatherIconLabel = new JLabel();

        DisasterActionPanel disasterActionPanel = new DisasterActionPanel(null, mainPanel, cardLayout);
        this.disasterImagePanel = new DisasterImagePanel(disasterActionPanel, mainPanel, cardLayout);
        this.disasterImagePanel.setPreferredSize(new Dimension(1000, 400));

        regionComboBox = new JComboBox<>(RegionUtil.REGION_MAP.keySet().toArray(new String[0]));
        regionComboBox.setMaximumSize(new Dimension(300, 40));
        regionComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        regionComboBox.setSelectedItem("서울특별시");

        regionComboBox.addActionListener(e -> {
            String selectedRegion = (String) regionComboBox.getSelectedItem();
            String city = RegionUtil.REGION_MAP.get(selectedRegion);
            updateWeather(city);
        });

        setLayout(new GridBagLayout());
        setBackground(new Color(179, 224, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);

        // ReportsPanel
        reportsPanel = new ReportsPanel();
        reportsPanel.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.NORTH;
//        gbc.weighty = 30;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 50, 0);
        add(reportsPanel, gbc);

        // WeatherTitlePanel, Left Panel, Right Panel, and GoBoardPanel in one container
        JPanel weatherContentPanel = new JPanel(new GridBagLayout());
        weatherContentPanel.setBackground(new Color(179, 224, 255));
        GridBagConstraints gbcContent = new GridBagConstraints();

        // WeatherTitlePanel
        WeatherTitlePanel = new WeatherTitlePanel();
        gbcContent.gridx = 0;
        gbcContent.gridy = 0;
        gbcContent.gridwidth = 2;
        gbcContent.fill = GridBagConstraints.HORIZONTAL;
        weatherContentPanel.add(WeatherTitlePanel, gbcContent);

        // Left Panel
        JPanel leftPanel = WeatherUIBuilder.createLeftPanel(this);
        gbcContent.gridx = 0;
        gbcContent.gridy = 1;
        gbcContent.gridwidth = 1;
        gbcContent.anchor = GridBagConstraints.EAST;
        gbcContent.fill = GridBagConstraints.NONE;
        weatherContentPanel.add(leftPanel, gbcContent);

        // Right Panel
        gbcContent.gridx = 1;
        gbcContent.anchor = GridBagConstraints.WEST;
        weatherContentPanel.add(rightPanel, gbcContent);

        // GoBoardPanel
        goBoardPanel = new GoBoardPanel(mainPanel, cardLayout);
        gbcContent.gridx = 0;
        gbcContent.gridy = 2;
        gbcContent.gridwidth = 2;
        gbcContent.weighty = 0;
        gbcContent.anchor = GridBagConstraints.CENTER;
        gbcContent.fill = GridBagConstraints.NONE;
        gbcContent.insets = new Insets(0, 0, 10, 0);
        weatherContentPanel.add(goBoardPanel, gbcContent);

        // Add weatherContentPanel to main panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(weatherContentPanel, gbc);

        reportsPanel.getSummaryLabel().setText("Weather summary data loaded!");

        // News Title Panel
        newsTitlePanel = createTitlePanel("최신 기사");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(30, 0, 0, 0);
        add(newsTitlePanel, gbc);

        // NewsSummaryPanel
        NewsSummaryPanel newsSummaryPanel = new NewsSummaryPanel();
        newsSummaryPanel.setPreferredSize(new Dimension(1000, 200));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 30, 0);

        add(newsSummaryPanel, gbc);

        // Disaster Action Title Panel
        daTitlePanel = createTitlePanel("재난행동요령 바로가기");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(daTitlePanel, gbc);

        // DisasterImagePanel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weighty = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(this.disasterImagePanel, gbc);

        revalidate();
        repaint();

        updateWeather("Seoul");
    }

    private JPanel createTitlePanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(1000, 60));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setForeground(new Color(3, 108, 211));
        label.setVerticalAlignment(SwingConstants.CENTER); // 상하 중앙 정렬
        label.setHorizontalAlignment(SwingConstants.CENTER); // 좌우 중앙 정렬

        panel.setBackground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER); // 중앙에 배치

        return panel;
    }


    public void updateWeather(String city) {
        WeatherUpdater.updateWeather(this, city);
    }

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