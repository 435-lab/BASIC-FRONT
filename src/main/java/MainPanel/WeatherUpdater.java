package MainPanel;

import org.json.JSONObject;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherUpdater {

    // 주어진 도시의 날씨 정보를 업데이트하는 메서드
    public static void updateWeather(WeatherPanel panel, String city) {
        String apiKey = "4b6191fd5f560ca39c383c6fa2c64e6d";
        String lang = "kr";
        String urlString = String.format(
                "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=%s&units=metric",
                city, apiKey, lang
        );

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                conn.disconnect();

                JSONObject jsonObject = new JSONObject(content.toString());
                double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                int humidity = jsonObject.getJSONObject("main").getInt("humidity");
                String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                SwingUtilities.invokeLater(() -> {
                    panel.getTemperatureLabel().setText(String.format("온도: %.1f°C", temperature));
                    panel.getHumidityLabel().setText(String.format("습도: %d%%", humidity));
                    panel.getWeatherDescriptionLabel().setText(weatherDescription);

                    WeatherIconUpdater.updateWeatherIcon(panel, weatherDescription.toLowerCase());
                });

            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    panel.getTemperatureLabel().setText("온도: 정보를 불러올 수 없습니다.");
                    panel.getHumidityLabel().setText("습도: 정보를 불러올 수 없습니다.");
                    panel.getWeatherDescriptionLabel().setText("날씨: 정보를 불러올 수 없습니다.");
                    panel.getWeatherIconLabel().setIcon(null);
                });
            }
        }).start();
    }
}
