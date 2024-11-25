package MainPanel;

public class WeatherIconUpdater {

    // 날씨 설명에 따라 적절한 날씨 아이콘을 패널에 업데이트하는 메서드
    public static void updateWeatherIcon(WeatherPanel panel, String description) {
        if (description.contains("구름") || description.contains("흐림")) {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/cloud.png", 100, 100).getIcon());
        } else if (description.contains("맑음")) {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/sunny.png", 100, 100).getIcon());
        } else if (description.contains("비")) {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/rain.png", 100, 100).getIcon());
        } else if (description.contains("눈")) {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/snow.png", 100, 100).getIcon());
        } else if (description.contains("번개") || description.contains("뇌우")) {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/lightning.png", 100, 100).getIcon());
        } else if (description.contains("안개") || description.contains("박무")) {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/fog.png", 100, 100).getIcon());
        } else {
            panel.getWeatherIconLabel().setIcon(WeatherUIBuilder.createLargeScaledImageLabel("/Image/weather/cloud.png", 100, 100).getIcon());
        }
    }
}
