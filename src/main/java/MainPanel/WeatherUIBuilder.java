package MainPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;

public class WeatherUIBuilder {

    private static final Map<String, ImageIcon> imageCache = new HashMap<>();

    // 왼쪽 패널을 위한 메서드 (큰 이미지)
    static JLabel createLargeScaledImageLabel(String resourcePath, int width, int height) {
        try {
            if (!imageCache.containsKey(resourcePath + "_large")) {
                ImageIcon icon = new ImageIcon(WeatherUIBuilder.class.getResource(resourcePath));
                Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(image);
                imageCache.put(resourcePath + "_large", scaledIcon);
            }

            JLabel label = new JLabel(imageCache.get(resourcePath + "_large"));
            label.setPreferredSize(new Dimension(width, height));  // 크기 정확히 일치
            label.setMinimumSize(new Dimension(width, height));
            label.setMaximumSize(new Dimension(width, height));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            return label;
        } catch (Exception e) {
            e.printStackTrace();
            return new JLabel("이미지를 불러올 수 없습니다.");
        }
    }

    // 오른쪽 패널을 위한 메서드 (작은 이미지)
    static JLabel createSmallScaledImageLabel(String resourcePath, int width, int height) {
        try {
            if (!imageCache.containsKey(resourcePath + "_small")) {
                ImageIcon icon = new ImageIcon(WeatherUIBuilder.class.getResource(resourcePath));
                Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(image);
                imageCache.put(resourcePath + "_small", scaledIcon);
            }

            JLabel label = new JLabel(imageCache.get(resourcePath + "_small"));
            label.setPreferredSize(new Dimension(width, height));  // 크기 정확히 일치
            label.setMinimumSize(new Dimension(width, height));
            label.setMaximumSize(new Dimension(width, height));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.CENTER);
            return label;
        } catch (Exception e) {
            e.printStackTrace();
            return new JLabel("이미지를 불러올 수 없습니다.");
        }
    }

    // 왼쪽 패널 생성 (큰 이미지를 사용)
    public static JPanel createLeftPanel(WeatherPanel panel) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        leftPanel.setBackground(Color.WHITE);

        // 패널의 크기 고정
        Dimension fixedSize = new Dimension(500, 700);
        leftPanel.setPreferredSize(fixedSize);
        leftPanel.setMinimumSize(fixedSize);
        leftPanel.setMaximumSize(fixedSize);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.anchor = GridBagConstraints.CENTER;
        // 콤보박스 추가
        JComboBox<String> regionComboBox = panel.getRegionComboBox();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        leftPanel.add(regionComboBox, gbc);

        // 날씨 아이콘과 설명 추가 (큰 이미지)
        JLabel weatherIcon = panel.getWeatherIconLabel();
        panel.getWeatherDescriptionLabel().setFont(new Font("Arial", Font.PLAIN, 30));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        leftPanel.add(weatherIcon, gbc);

        gbc.gridx = 1;
        leftPanel.add(panel.getWeatherDescriptionLabel(), gbc);

        // 온도 아이콘 및 레이블 추가 (큰 이미지)
        JLabel temperatureIcon = createLargeScaledImageLabel("/Image/temperature.png", 30, 30);
        panel.getTemperatureLabel().setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridy = 2;
        gbc.gridx = 0;
        leftPanel.add(temperatureIcon, gbc);

        gbc.gridx = 1;
        leftPanel.add(panel.getTemperatureLabel(), gbc);

        // 습도 아이콘 및 레이블 추가 (큰 이미지)
        JLabel humidityIcon = createLargeScaledImageLabel("/Image/humidity.png", 30, 30);
        panel.getHumidityLabel().setFont(new Font("Arial", Font.PLAIN, 20));
        gbc.gridy = 3;
        gbc.gridx = 0;
        leftPanel.add(humidityIcon, gbc);

        gbc.gridx = 1;
        leftPanel.add(panel.getHumidityLabel(), gbc);

        return leftPanel;
    }

    // 오른쪽 패널 생성 (작은 이미지를 사용)
    public static JLayeredPane createRightPanel() {
        JLayeredPane layeredPane = new JLayeredPane();
        Dimension fixedSize = new Dimension(500, 700);
        layeredPane.setPreferredSize(fixedSize);
        layeredPane.setMinimumSize(fixedSize);
        layeredPane.setMaximumSize(fixedSize);

        JPanel backgroundPanel = new JPanel();
        backgroundPanel.setBounds(0, 0, 500, 700);
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBackground(Color.WHITE);
        JLabel backgroundLabel = createLargeScaledImageLabel("/Image/KoreaMapImage.png", 480, 660);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(backgroundLabel, gbc);

        layeredPane.add(backgroundPanel, JLayeredPane.DEFAULT_LAYER);

        // 날씨 정보 초기화
        Map<String, String> regionWeatherDescriptions = new HashMap<>();
        regionWeatherDescriptions.put("서울특별시", "");
        regionWeatherDescriptions.put("부산광역시", "");
        regionWeatherDescriptions.put("대구광역시", "");
        regionWeatherDescriptions.put("광주광역시", "");
        regionWeatherDescriptions.put("인천광역시", "");
        regionWeatherDescriptions.put("강원도", "");
        regionWeatherDescriptions.put("경상남도", "");
        regionWeatherDescriptions.put("경상북도", "");
        regionWeatherDescriptions.put("울산광역시", "");
        regionWeatherDescriptions.put("대전광역시", "");
        regionWeatherDescriptions.put("전라남도", "");
        regionWeatherDescriptions.put("전라북도", "");
        regionWeatherDescriptions.put("충청남도", "");
        regionWeatherDescriptions.put("충청북도", "");
        regionWeatherDescriptions.put("제주특별자치도", "");
        regionWeatherDescriptions.put("세종특별자치시", "");
        regionWeatherDescriptions.put("경기도", "");

        // 날씨 정보를 업데이트하고 UI 갱신
        RegionalWeatherUpdater.updateWeatherForRegions(regionWeatherDescriptions, () -> {
            for (Map.Entry<String, String> entry : regionWeatherDescriptions.entrySet()) {
                String regionName = entry.getKey();
                String weatherDescription = entry.getValue();
//               날씨 설명을 콘솔에 출력
//                System.out.println("Region: " + regionName + ", Weather: " + weatherDescription);

                // 날씨 설명에 따라 적절한 아이콘 선택 (작은 이미지 사용)
                // 날씨 설명에 따라 적절한 아이콘 선택
                Icon weatherIcon;
                if (weatherDescription.contains("구름") || weatherDescription.contains("흐림")) {
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/cloud.png", 25, 25).getIcon();
                } else if (weatherDescription.contains("맑음")) {
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/sunny.png", 25, 25).getIcon();
                } else if (weatherDescription.contains("비") || weatherDescription.contains("소나기") || weatherDescription.contains("실 비")) {  // "비"가 포함된 경우
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/rain.png", 25, 25).getIcon();
                } else if (weatherDescription.contains("눈")) {  // "눈"이 포함된 경우
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/snow.png", 25, 25).getIcon();
                } else if (weatherDescription.contains("번개") || weatherDescription.contains("뇌우")) {  // "번개"가 포함된 경우
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/lightning.png", 25, 25).getIcon();
                } else if (weatherDescription.contains("안개") || weatherDescription.contains("박무")) {  // "안개"가 포함된 경우
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/fog.png", 25, 25).getIcon();
                } else {
                    weatherIcon = createSmallScaledImageLabel("/Image/weather/cloud.png", 25, 25).getIcon();  // 기본 구름 이미지
                }
                // 아이콘을 지도 위에 추가
                addOverlayImage(layeredPane, regionName, weatherIcon, 60, 60, getCoordinatesForRegion(regionName));
            }

            layeredPane.revalidate();
            layeredPane.repaint();
        });

        return layeredPane;
    }

    // 오버레이 이미지를 지도에 추가하는 메서드
    private static void addOverlayImage(JLayeredPane layeredPane, String regionName, Icon icon, int width, int height, Point coordinates) {
        JLabel overlayImage = new JLabel(icon);
        overlayImage.setText(regionName);
        overlayImage.setHorizontalTextPosition(JLabel.CENTER);
        overlayImage.setVerticalTextPosition(JLabel.BOTTOM);
        overlayImage.setBounds(coordinates.x, coordinates.y, width + 30, height);
        overlayImage.setPreferredSize(new Dimension(width, height));  // 크기 정확히 맞춤
        overlayImage.setHorizontalAlignment(SwingConstants.CENTER);
        overlayImage.setVerticalAlignment(SwingConstants.CENTER);
        layeredPane.add(overlayImage, JLayeredPane.PALETTE_LAYER);
    }

    // 지역별 좌표 반환
    private static Point getCoordinatesForRegion(String regionName) {
        switch (regionName) {
            case "서울특별시": return new Point(125, 180);
            case "부산광역시": return new Point(318, 442);
            case "대구광역시": return new Point(270, 370);
            case "광주광역시": return new Point(103, 450);
            case "인천광역시": return new Point(87, 195);
            case "대전광역시": return new Point(159, 320);
            case "울산광역시": return new Point(330, 400);
            case "세종특별자치시": return new Point(149, 275);
            case "경기도": return new Point(162, 191);
            case "강원도": return new Point(240, 143);
            case "충청북도": return new Point(195, 250);
            case "충청남도": return new Point(87, 284);
            case "전라북도": return new Point(144, 380);
            case "전라남도": return new Point(113, 495);
            case "경상북도": return new Point(270, 295);
            case "경상남도": return new Point(240, 420);
            case "제주특별자치도": return new Point(88, 593);
            default: return new Point(0, 0);
        }
    }
}
