package news;

public class NewsItem {
    private String dateTime;
    private String title;
    private String link;
    private String content;

    public NewsItem(String dateTime, String title, String link, String content) {
        this.dateTime = dateTime;
        this.title = title;
        this.link = link;
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getContent() {
        return content;
    }
}