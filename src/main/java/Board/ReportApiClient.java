package Board;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;


public class ReportApiClient {
    private String jwtToken;  // JWT 토큰을 저장할 변수
    private String baseUrl;   // 서버 URL

    // ConfigManager를 사용하여 URL을 설정
    public ReportApiClient() {
        ConfigManager configManager = new ConfigManager();
        this.baseUrl = configManager.getProperty("board.url");
        if (this.baseUrl == null || this.baseUrl.isEmpty()) {
            throw new RuntimeException("서버 URL이 설정 파일에 없습니다.");
        }
    }

    // JWT 토큰 설정 메서드
    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    // JWT 토큰 삭제 메서드 (로그아웃 시 호출)
    public void clearJwtToken() {
        this.jwtToken = null;  // JWT 토큰을 null로 설정
    }

    // 게시글 목록을 가져오는 메서드 (JWT 포함)
    public Vector<Vector<Object>> getAllReports() {
        Vector<Vector<Object>> reportData = new Vector<>();
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            // Authorization 헤더에 JWT 토큰 추가
            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONArray jsonArray = new JSONArray(response.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject report = jsonArray.getJSONObject(i);

                    int reportId = report.optInt("report_id");
                    String title = report.optString("title", "");
                    String content = report.optString("content", "");
                    String createId = report.optString("create_id", "");
                    String createAt = report.optString("create_at", "");

                    Vector<Object> row = new Vector<>();
                    row.add(reportId);  // report_id로 사용
                    row.add(title);
                    row.add(content);
                    row.add(createId);
                    row.add(createAt);

                    reportData.add(row);
                }
            } else {
                System.out.println("GET 요청 실패: 응답 코드 = " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reportData;
    }

    // 게시글 작성 메서드 (JWT 포함)
    public void createReport(String title, String content) {
        try {
            JSONObject reportData = new JSONObject();
            reportData.put("title", title);
            reportData.put("content", content);

            URL url = new URL(baseUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            // Authorization 헤더에 JWT 토큰 추가
            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = reportData.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                JOptionPane.showMessageDialog(null, "게시글 작성 성공!");
            } else {
                System.out.println("POST 요청 실패: 응답 코드 = " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 게시글 수정 메서드 (JWT 포함)
    public void updateReport(int postId, String title, String content) {
        try {
            URL url = new URL(baseUrl + "/" + postId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json");

            // Authorization 헤더에 JWT 토큰 추가
            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            conn.setDoOutput(true);
            JSONObject reportJson = new JSONObject();
            reportJson.put("title", title);
            reportJson.put("content", content);

            OutputStream os = conn.getOutputStream();
            os.write(reportJson.toString().getBytes("utf-8"));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                JOptionPane.showMessageDialog(null, "게시글 수정 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                JOptionPane.showMessageDialog(null, "수정 권한이 없습니다.", "권한 오류", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "수정 실패: 응답 코드 = " + responseCode, "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "귀하는 게시글 수정 권한이 없습니다.");
        }
    }

    // 게시글 삭제 메서드 (JWT 포함)
    public void deleteReport(int postId) {
        try {
            URL url = new URL(baseUrl + "/" + postId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Content-Type", "application/json");

            // Authorization 헤더에 JWT 토큰 추가
            if (jwtToken != null && !jwtToken.isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            int responseCode = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                JOptionPane.showMessageDialog(null, "게시글 삭제 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                JOptionPane.showMessageDialog(null, "삭제 권한이 없습니다.", "권한 오류", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "삭제 실패: 응답 코드 = " + responseCode, "오류", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "귀하는 해당 게시물 삭제 권한이 없습니다");
        }
    }

    // POST 요청을 보내는 메서드 (JWT 토큰 추가)
    private String sendPostRequest(String endpoint, String jsonData) throws Exception {
        URL url = new URL(baseUrl+ endpoint);  // 서버의 전체 URL 생성
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // JWT 토큰을 Authorization 헤더에 추가
        if (jwtToken != null) {
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
        }

        // JSON 데이터를 서버로 전송
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 서버 응답 받기
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED) {
            return "게시글이 성공적으로 등록되었습니다.";
        } else {
            return "오류 발생: " + responseCode;
        }
    }
}
