package MainPanel;

import java.util.Map;

public class RegionUtil {
    public static final Map<String, String> REGION_MAP = Map.ofEntries(
            Map.entry("서울특별시", "Seoul"),
            Map.entry("부산광역시", "Busan"),
            Map.entry("대구광역시", "Daegu"),
            Map.entry("인천광역시", "Incheon"),
            Map.entry("광주광역시", "Gwangju"),
            Map.entry("대전광역시", "Daejeon"),
            Map.entry("울산광역시", "Ulsan"),
            Map.entry("세종특별자치시", "Sejong"),
            Map.entry("경기도", "Gyeonggi-do"),
            Map.entry("강원도", "Gangwon-do"),
            Map.entry("충청북도", "Chungcheongbuk-do"),
            Map.entry("충청남도", "Chungcheongnam-do"),
            Map.entry("전라북도", "Jeollabuk-do"),
            Map.entry("전라남도", "Jeollanam-do"),
            Map.entry("경상북도", "Gyeongsangbuk-do"),
            Map.entry("경상남도", "Gyeongsangnam-do"),
            Map.entry("제주특별자치도", "Jeju-do")
    );
}
