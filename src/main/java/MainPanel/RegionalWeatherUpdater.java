package MainPanel;

import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class RegionalWeatherUpdater {

    public static void updateWeatherForRegions(Map<String, String> regionWeatherDescriptions, Runnable updateUI) {
        String apiKey = "4b6191fd5f560ca39c383c6fa2c64e6d";
        String lang = "kr";

        // 날씨를 업데이트할 지역의 개수를 카운트
        CountDownLatch latch = new CountDownLatch(regionWeatherDescriptions.size());

        // 각 지역의 날씨 정보를 가져오기
        for (String city : regionWeatherDescriptions.keySet()) {
            // 한글 도시명을 영문 도시명으로 변환
            String englishCity = RegionUtil.REGION_MAP.get(city);

            if (englishCity == null) {
                // 영문 도시명을 찾을 수 없는 경우 로그 출력하고 다음 도시로 넘어감
                System.out.println("영문 도시명을 찾을 수 없습니다: " + city);
                latch.countDown();  // 카운트 다운
                continue;
            }

            // API 요청 URL 생성
            String urlString = String.format(
                    "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=%s&units=metric",
                    englishCity, apiKey, lang
            );

            // 새 스레드에서 API 호출
            new Thread(() -> {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/json");

                    // 응답 상태 코드 확인
                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // 정상 응답을 받은 경우
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder content = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();
                        conn.disconnect();

                        // JSON 응답 파싱
                        JSONObject jsonObject = new JSONObject(content.toString());
                        String weatherDescription = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                        // 지역 날씨 설명 업데이트
                        regionWeatherDescriptions.put(city, weatherDescription.toLowerCase());
                    } else {
                        // 오류 응답 처리
                        System.out.println("API 요청 실패: " + city + ", 응답 코드: " + responseCode);
                        regionWeatherDescriptions.put(city, "날씨 정보를 불러올 수 없습니다.");
                    }

                } catch (Exception e) {
                    // 예외 발생 시 로그 출력
                    System.out.println("API 요청 중 오류 발생: " + city);
                    e.printStackTrace();
                    regionWeatherDescriptions.put(city, "오류 발생");

                } finally {
                    // 작업 완료 후 카운트 다운
                    latch.countDown();
                }
            }).start();
        }

        // 모든 스레드가 완료되면 UI 업데이트 호출
        new Thread(() -> {
            try {
                latch.await();  // 모든 작업이 완료될 때까지 대기
                SwingUtilities.invokeLater(updateUI);  // UI 업데이트
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
