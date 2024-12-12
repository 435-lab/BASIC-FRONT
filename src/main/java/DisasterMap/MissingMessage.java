package DisasterMap;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MissingMessage extends JPanel {
    private static final String KAKAO_API_KEY = ConfigLoader.getProperty("KAKAO_API_KEY");
    private static final String SERVER_URL = ConfigLoader.getProperty("MISSING_SERVER_URL"); // 서버 URL
    private JFXPanel jfxPanel;
    private WebView webView;
    private JTable messageTable;
    private DefaultTableModel tableModel;

    public MissingMessage() {
        setLayout(new BorderLayout());

        jfxPanel = new JFXPanel();
        JScrollPane scrollPane = new JScrollPane();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, jfxPanel, scrollPane);
        splitPane.setDividerLocation(700);
        splitPane.setResizeWeight(0.8);

        // 테이블 초기화
        String[] columnNames = {"발생일시", "메시지 내용", "수신 지역", "재난 종류"};
        tableModel = new DefaultTableModel(columnNames, 0);
        messageTable = new JTable(tableModel);
        scrollPane.setViewportView(messageTable);

        add(splitPane, BorderLayout.CENTER);

        // JavaFX 초기화
        Platform.runLater(() -> initFX(jfxPanel));
    }

    private void initFX(JFXPanel jfxPanel) {
        webView = new WebView();
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);

        // JavaScript와 Java 간의 통신 설정
        Platform.runLater(() -> {
            JSObject window = (JSObject) webView.getEngine().executeScript("window");
            window.setMember("javaConnector", this);
        });
        // 지도 및 JavaScript 설정
        String mapHtml = buildMapHtml(SERVER_URL, KAKAO_API_KEY);
        webView.getEngine().loadContent(mapHtml);
    }

    private String buildMapHtml(String serverUrl, String kakaoApiKey) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='utf-8'>" +
                "    <title>재난 메시지 지도</title>" +
                "</head>" +
                "<body>" +
                "    <div id=\"map\" style=\"width:100%;height:350px;\"></div>" +
                "    <p id=\"result\"></p>" +
                "    <script type='text/javascript' src='https://dapi.kakao.com/v2/maps/sdk.js?appkey=" + kakaoApiKey + "&libraries=services'></script>" +
                "    <script>" +
                "        const SERVER_URL = '" + serverUrl + "';" +
                "        async function sendCoordinatesToServer(lat, lng) {" +
                "            try {" +
                "                console.log('Preparing to send data to server...');" +
                "                const response = await fetch(`${SERVER_URL}?latitude=${lat}&longitude=${lng}`, {" +
                "                    method: 'GET'," +
                "                    headers: {" +
                "                        'Accept': 'application/json'" +
                "                    }" +
                "                });" +
                "                console.log('Response status:', response.status);" +
                "                if (response.ok) {" +
                "                    const responseData = await response.json();" +
                "                    console.log('Server response:', responseData);" +
                "                    if (window.javaConnector) { " +
                "                        window.javaConnector.updateTableFromServerResponse(JSON.stringify(responseData));" +
                "                    }" +
                "                } else {" +
                "                    const errorText = await response.text();" +
                "                    console.error('Failed to send data to server. Status:', response.status, 'Response:', errorText);" +
                "                    document.getElementById('result').innerText = `서버 응답 실패: ${response.status}`;" +
                "                }" +
                "            } catch (error) {" +
                "                console.error('Error while sending data:', error);" +
                "                document.getElementById('result').innerText = `서버 요청 중 오류 발생: ${error.message}`;" +
                "            }" +
                "        }" +
                "        function initMap() {" +
                "            const mapContainer = document.getElementById('map')," +
                "                mapOption = {" +
                "                    center: new kakao.maps.LatLng(36.6424341, 127.4890319)," +
                "                    level: 13" +
                "                };" +
                "            const map = new kakao.maps.Map(mapContainer, mapOption);" +
                "            const marker = new kakao.maps.Marker();" +
                "            kakao.maps.event.addListener(map, 'click', function(mouseEvent) {" +
                "                const latlng = mouseEvent.latLng;" +
                "                marker.setPosition(latlng);" +
                "                marker.setMap(map);" +
                "                sendCoordinatesToServer(latlng.getLat(), latlng.getLng());" +
                "            });" +
                "        }" +
                "        window.addEventListener('DOMContentLoaded', initMap);" +
                "    </script>" +
                "</body>" +
                "</html>";
    }

    // JavaScript와 Java 간 통신을 위한 연결 클래스 설정
    public void updateTableFromServerResponse(String jsonResponse) {
        JSONArray jsonArray = new JSONArray(jsonResponse);
        Set<String> uniqueIds = new LinkedHashSet<>();
        Set<Object[]> rowDataSet = StreamSupport.stream(jsonArray.spliterator(), false)
                .map(obj -> (JSONObject) obj)
                .filter(json -> uniqueIds.add(json.optString("id"))) // 중복 제거: 동일한 id가 있을 경우 제외
                .map(json -> new Object[]{
                        json.optString("crt_dt").split("T")[0], // 발생일시에서 연도, 월, 일만 가져오기
                        json.optString("msg_cn"),
                        json.optString("rcptn_rgn_nm"),
                        json.optString("dst_se_nm")
                }).collect(Collectors.toCollection(LinkedHashSet::new));

        // 기존 테이블 데이터 제거 후 새 데이터 추가
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (Object[] rowData : rowDataSet) {
                tableModel.addRow(rowData);
            }
        });
    }
}
