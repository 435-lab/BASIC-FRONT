package DisasterMap;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class maptotal extends JPanel {
    private static final String KAKAO_API_KEY = ConfigLoader.getProperty("KAKAO_API_KEY");
    private static final String DISASTER_SERVER_URL = ConfigLoader.getProperty("SERVER_URL");
    private static final String MISSING_SERVER_URL = ConfigLoader.getProperty("MISSING_SERVER_URL");

    private JFXPanel jfxPanel;
    private WebView webView;
    private JTable disasterMessageTable;
    private JTable missingMessageTable;
    private DefaultTableModel disasterTableModel;
    private DefaultTableModel missingTableModel;
    private JTextArea logTextArea;

    public maptotal() {
        setLayout(new BorderLayout());

        JPanel mapPanel = new JPanel(new BorderLayout());
        jfxPanel = new JFXPanel();
        mapPanel.add(jfxPanel, BorderLayout.CENTER);

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2));
        tablesPanel.setPreferredSize(new Dimension(1200, 200));

        JPanel disasterPanel = new JPanel(new BorderLayout());
        JLabel disasterLabel = new JLabel("경고", JLabel.CENTER);
        JScrollPane disasterScrollPane = new JScrollPane();
        disasterPanel.add(disasterLabel, BorderLayout.NORTH);
        disasterPanel.add(disasterScrollPane, BorderLayout.CENTER);

        JPanel missingPanel = new JPanel(new BorderLayout());
        JLabel missingLabel = new JLabel("기타", JLabel.CENTER);
        JScrollPane missingScrollPane = new JScrollPane();
        missingPanel.add(missingLabel, BorderLayout.NORTH);
        missingPanel.add(missingScrollPane, BorderLayout.CENTER);

        tablesPanel.add(disasterPanel);
        tablesPanel.add(missingPanel);

        String[] disasterColumnNames = {"발생일시", "메시지 내용", "수신 지역", "재난 종류"};
        disasterTableModel = new DefaultTableModel(disasterColumnNames, 0);
        disasterMessageTable = new JTable(disasterTableModel);
        disasterScrollPane.setViewportView(disasterMessageTable);

        String[] missingColumnNames = {"발생일시", "메시지 내용", "수신 지역", "재난 종류"};
        missingTableModel = new DefaultTableModel(missingColumnNames, 0);
        missingMessageTable = new JTable(missingTableModel);
        missingScrollPane.setViewportView(missingMessageTable);

//        logTextArea = new JTextArea(5, 20);
//        logTextArea.setEditable(false);
//        JScrollPane logScrollPane = new JScrollPane(logTextArea);
//        logScrollPane.setBorder(BorderFactory.createTitledBorder("로그"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mapPanel, tablesPanel);
        splitPane.setResizeWeight(1);
        splitPane.setDividerLocation(0.85);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
        //add(logScrollPane, BorderLayout.SOUTH);

        Platform.runLater(() -> initFX(jfxPanel));
    }

    private void initFX(JFXPanel jfxPanel) {
        webView = new WebView();
        Scene scene = new Scene(webView);
        jfxPanel.setScene(scene);

        webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                try {
                    JSObject window = (JSObject) webView.getEngine().executeScript("window");
                    window.setMember("javaConnector", this);
                    System.out.println("JavaConnector successfully set in JavaScript.");
                    Platform.runLater(() -> webView.getEngine().executeScript("console.log('javaConnector is set in JS');"));
                } catch (Exception e) {
                    System.err.println("Error setting JavaConnector: " + e.getMessage());
                }
            }
        });

        String mapHtml = buildMapHtml(DISASTER_SERVER_URL, MISSING_SERVER_URL, KAKAO_API_KEY);
        webView.getEngine().loadContent(mapHtml);
    }

    private String buildMapHtml(String disasterServerUrl, String missingServerUrl, String kakaoApiKey) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                " <meta charset='utf-8'>" +
                " <title>재난 및 실종 메시지 지도</title>" +
                "</head>" +
                "<body>" +
                " <div id=\"map\" style=\"width:100%;height:800px;\"></div>" +
                " <script type='text/javascript' src='https://dapi.kakao.com/v2/maps/sdk.js?appkey=" + kakaoApiKey + "&libraries=services'></script>" +
                " <script>" +
                " let javaConnector;" +
                " window.addEventListener('DOMContentLoaded', () => {" +
                "   console.log('DOMContentLoaded event fired');" +
                "   setTimeout(() => {" +
                "       if (window.javaConnector) {" +
                "           console.log('javaConnector is available');" +
                "           javaConnector = window.javaConnector;" +
                "       } else {" +
                "           console.error('javaConnector is not available');" +
                "       }" +
                "       initMap();" +
                "   }, 1000);" +  // 1초 대기 후 확인
                " });" +
                " function initMap() {" +
                "   const mapContainer = document.getElementById('map')," +
                "       mapOption = {" +
                "           center: new kakao.maps.LatLng(36.79839104943888, 127.0752077392241)," +
                "           level:11" + //11 또는 12
                "       };" +
                "   const map = new kakao.maps.Map(mapContainer, mapOption);" +
                "   const marker = new kakao.maps.Marker({position: map.getCenter()});" +
                "   marker.setMap(map);" +
                "   sendCoordinatesToServer(36.79839104943888, 127.0752077392241, 'disaster');" +
                "   sendCoordinatesToServer(36.79839104943888, 127.0752077392241, 'missing');" +
                "   kakao.maps.event.addListener(map, 'click', function(mouseEvent) {" +
                "       const latlng = mouseEvent.latLng;" +
                "       marker.setPosition(latlng);" +
                "       marker.setMap(map);" +
                "       sendCoordinatesToServer(latlng.getLat(), latlng.getLng(), 'disaster');" +
                "       sendCoordinatesToServer(latlng.getLat(), latlng.getLng(), 'missing');" +
                "   });" +
                " }" +
                " async function sendCoordinatesToServer(lat, lng, tableType) {" +
                "   const serverUrl = tableType === 'disaster' ? '" + disasterServerUrl + "' : '" + missingServerUrl + "';" +
                "   try {" +
                "       const response = await fetch(`${serverUrl}?latitude=${lat}&longitude=${lng}`);" +
                "       if (response.ok) {" +
                "           const responseText = await response.text();" +
                "           console.log('Raw response:', responseText);" +  // 서버 응답 확인
                "           try {" +
                "               const data = JSON.parse(responseText);" +
                "               console.log('Response received:', data);" +
                "               if (javaConnector) {" +
                "                   console.log('javaConnector is defined and ready to use.');" +
                "                   try {" +
                "                       javaConnector.updateTableFromServerResponse(JSON.stringify(data), tableType);" +
                "                   } catch (e) {" +
                "                       console.error('Error while calling javaConnector.updateTableFromServerResponse:', e);" +
                "                   }" +
                "               } else {" +
                "                   console.error('javaConnector is not available in sendCoordinatesToServer');" +
                "               }" +
                "           } catch (jsonError) {" +
                "               console.error('Invalid JSON response:', responseText);" +
                "           }" +
                "       } else {" +
                "           console.error('Error in response:', response.status);" +
                "       }" +
                "   } catch (error) {" +
                "       console.error('Error in sendCoordinatesToServer:', error);" +
                "   }" +
                " }" +
                " </script>" +
                "</body>" +
                "</html>";
    }

    public void updateTableFromServerResponse(String jsonResponse, String tableType) {
        System.out.println("updateTableFromServerResponse called with data: " + jsonResponse + ", tableType: " + tableType);
        SwingUtilities.invokeLater(() -> {
            try {
                DefaultTableModel model = "disaster".equals(tableType) ? disasterTableModel : missingTableModel;
                model.setRowCount(0);
                JSONArray jsonArray = new JSONArray(jsonResponse);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    model.addRow(new Object[]{
                            obj.optString("crt_dt"),
                            obj.optString("msg_cn"),
                            obj.optString("rcptn_rgn_nm"),
                            obj.optString("dst_se_nm")
                    });
                }
            } catch (Exception e) {
                System.err.println("Error updating table from server response: " + e.getMessage());
            }
        });
    }

    public void simpleTest(String message) {
        System.out.println("simpleTest called with message: " + message);
    }
}
