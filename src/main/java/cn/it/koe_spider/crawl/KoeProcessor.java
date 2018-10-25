package cn.it.koe_spider.crawl;

import cn.it.koe_spider.domain.KoeInfo;
import cn.it.koe_spider.repository.KoeInfoRepository;
import cn.it.koe_spider.util.FileNameUtils;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class KoeProcessor implements PageProcessor {

    public static final String TARGET_PAGE = "https://koe-koe\\.com/detail\\.php\\?n=\\w+";
    public static final String LIST_PAGE = "https://koe-koe\\.com/archive\\.php(.*)";
    public static final String URL_DOMAIN = "koe-koe.com";
    public static final String URL_AUDIO = "//file.koe-koe\\.com/sound/upload/[0-9]+\\.mp3";
    @Autowired
    KoeInfoRepository koeInfoRepository;
    private boolean init = false;
    @Value("${video.nums}")
    private Integer nums;
    private Integer addNums;

    private Site site = Site
            .me()
            .setDomain(URL_DOMAIN)
            .setCharset("UTF-8")
            .setSleepTime(100)
            .setRetryTimes(6)
            .setUserAgent(
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

    private synchronized List<String> init(Page page) {
        ArrayList<String> requests = new ArrayList<>(nums);
        if (init) return requests;
        List<KoeInfo> koeInfoList = koeInfoRepository.getLatest(new PageRequest(0, 1));
        /*int start = nums;
        if(koeInfoList != null && koeInfoList.size()>0){
            start = koeInfoList.get(0).getId();
        }*/
        for (int i = 1; i < nums; i++) {
            KoeInfo info = koeInfoRepository.findOne(i);
            if (info == null) {
                String listPage = "https://koe-koe.com/detail.php?n=" + i;
                requests.add(listPage);
            }
        }
        init = true;
        return requests;
    }

    private synchronized List<String> add(Page page) {
        ArrayList<String> requests = new ArrayList<>();
        if (this.addNums == null) return requests;
        List<KoeInfo> info = koeInfoRepository.getLatest(new PageRequest(0, 1));
        if (info != null && info.size() > 0) {
            Integer id = info.get(0).getId();
            for (int i = 1; i < this.addNums + 1; i++) {
                int i1 = id + i;
                String listPage = "https://koe-koe.com/detail.php?n=" + i1;
                requests.add(listPage);
            }
        }
        this.addNums = null;
        return requests;
    }

    @Override
    public void process(Page page) {
        if (!init) {
            List<String> list = init(page);
            page.addTargetRequests(list);
            list = null;
        }
        if (this.addNums != null) {
            List<String> add = add(page);
            page.addTargetRequests(add);
            add = null;
        }
        KoeInfo koeInfo = new KoeInfo();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (page.getUrl().regex(TARGET_PAGE).match()) {
            Html html = page.getHtml();
            String content = html.xpath("//*[@id=\"content_body\"]/text()").get();

            try {
                String title = html.xpath("//*[@id=\"content_body\"]/h2[1]/allText()").toString();
                String filter_t = FileNameUtils.fileNameFilter(title);
                String til = EmojiParser.removeAllEmojis(filter_t).trim();
                koeInfo.setTitle(til);
                koeInfo.setAuthor(EmojiParser.removeAllEmojis(html.xpath("//*[@id=\"text\"]/div[2]/p[1]/span/a/text()").toString()).trim());
                koeInfo.setDescriptions(EmojiParser.removeAllEmojis(html.xpath("//*[@id=\"text\"]/div[2]/p[1]/outerHtml()").toString()).trim());

                String audio = html.regex(URL_AUDIO).get();
                koeInfo.setAudio("https:" + audio);

                String id = audio.substring(audio.lastIndexOf("/") + 1, audio.lastIndexOf("."));
                koeInfo.setId(Integer.parseInt(id));

                String ty = html.xpath("//*[@id=\"text\"]/div[2]/p[3]/a/@href").toString();
                String type = null;
                if (ty.contains("g=1")) {
                    type = "f";
                } else if (ty.contains("g=2")) {
                    type = "m";
                } else if (ty.contains("g=3")) {
                    type = "fm";
                } else {
                    type = "fm";
                }
                koeInfo.setType(type);
                koeInfo.setCategory(html.xpath("//*[@id=\"text\"]/div[2]/p[3]/a/text()").toString());

                String listenT = html.xpath("//*[@id=\"clap-inner\"]/p/text()").toString().split(":")[1].trim();
                koeInfo.setListenTimes(Integer.parseInt(listenT));

                String timeLong = html.xpath("//*[@id=\"text\"]/div[2]/p[2]/text()").toString().split(" ")[0];
                koeInfo.setTimeLong(timeLong);

                String publishStr = html.xpath("//*[@id=\"text\"]/div[2]/p[2]/text()")
                        .toString().split(" ")[1].replace("@", "");

                LocalDate now = LocalDate.now();
                LocalDate publishDate = null;
                if (publishStr.contains("前")) {
                    if (publishStr.contains("日")) {
                        String t_dat = publishStr.substring(0, publishStr.lastIndexOf("日"));
                        publishDate = now.plusDays(-Long.parseLong(t_dat));
                    } else {
                        publishDate = now;
                    }
                } else {
                    publishStr = "20" + publishStr;
                    publishDate = LocalDate.parse(publishStr, DateTimeFormatter.ofPattern("y/M/d"));
                }

                koeInfo.setPublishDate(simpleDateFormat.parse(publishDate.format(DateTimeFormatter.ISO_DATE)));

                koeInfo.setDownload(0);
                page.putField("koeInfo", koeInfo);
                System.out.println("parse:" + page.getUrl() + " ok");
            } catch (Exception e) {
                System.out.println("parse:" + page.getUrl() + " error");
            }
            html = null;
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public Integer getAddNums() {
        return addNums;
    }

    public void setAddNums(Integer addNums) {
        this.addNums = addNums;
    }

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }
}
