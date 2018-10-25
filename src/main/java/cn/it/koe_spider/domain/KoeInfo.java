package cn.it.koe_spider.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class KoeInfo {

    @Id
    private Integer id;
    private String title;
    private String author;
    private String descriptions;
    private String audio;
    private String type;
    private String category;
    private Integer listenTimes;
    private String timeLong;
    private Date publishDate;
    private Integer download;

    public KoeInfo() {
    }

    public KoeInfo(Integer id, String title, String author, String descriptions, String audio, String type, String category, Integer listenTimes, String timeLong, Date publishDate, Integer download) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.descriptions = descriptions;
        this.audio = audio;
        this.type = type;
        this.category = category;
        this.listenTimes = listenTimes;
        this.timeLong = timeLong;
        this.publishDate = publishDate;
        this.download = download;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getListenTimes() {
        return listenTimes;
    }

    public void setListenTimes(Integer listenTimes) {
        this.listenTimes = listenTimes;
    }

    public String getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(String timeLong) {
        this.timeLong = timeLong;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }
}
