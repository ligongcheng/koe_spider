package cn.it.koe_spider.controller;

import cn.it.koe_spider.crawl.KoePipeLine;
import cn.it.koe_spider.crawl.KoeProcessor;
import cn.it.koe_spider.domain.KoeInfo;
import cn.it.koe_spider.repository.KoeInfoRepository;
import cn.it.koe_spider.util.DownLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.MonitorableScheduler;
import us.codecraft.webmagic.scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.it.koe_spider.util.DownLoad.waitForDown;

@RestController
@RequestMapping("koe")
public class KoeController {

    private static Boolean quick = false;
    private static Boolean daily = false;
    private final String newLine = System.getProperty("line.separator");
    @Autowired
    KoeInfoRepository koeInfoRepository;
    @Autowired
    KoeProcessor koeProcessor;
    @Autowired
    KoePipeLine koePipeLine;
    private Spider spider;
    private Scheduler scheduler;

    public void start(int add) {
        if (spider != null) {
            if (spider.getStatus() == Spider.Status.Stopped) {
                koeProcessor.setAddNums(add);
                System.out.println("spider add " + add);
                this.spider();
            } else {
                System.out.println("please wait spider stop");
            }
        } else {
            this.spider();
        }
    }

    @Scheduled(fixedDelay = 1000 * 5)
    public void scheduler() {
        if (quick) {
            System.out.println("scheduler start");
            start(5000);
            System.out.println("scheduler end");
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void dailyScheduler() {
        if (daily) {
            System.out.println("dailyScheduler start");
            start(200);
            System.out.println("dailyScheduler end");
        }
    }

    @RequestMapping("/full")
    public String full() {
        ArrayList<String> list = new ArrayList<>();
        List<KoeInfo> koeInfoList = koeInfoRepository.getLatest(new PageRequest(0, 1));
        if (koeInfoList != null && koeInfoList.size() > 0) {
            Integer id = koeInfoList.get(0).getId();
            for (int i = 1; i < id; i++) {
                KoeInfo info = koeInfoRepository.findOne(i);
                if (info == null) {
                    String listPage = "https://koe-koe.com/detail.php?n=" + i;
                    list.add(listPage);
                }
            }
        }
        Spider spider = Spider.create(koeProcessor)
                .addPipeline(koePipeLine)
                .startUrls(list)
                .thread(8);
        spider.run();
        return "full ok";
    }

    public void spider() {
        Spider spider = Spider.create(koeProcessor)
                .addPipeline(koePipeLine)
                .addUrl("https://koe-koe.com")
                .thread(8);
        Scheduler scheduler = spider.getScheduler();
        this.scheduler = scheduler;
        this.spider = spider;
        spider.run();
        //down();
    }

    @RequestMapping("/param")
    public String daily(@RequestParam(required = false) Integer daily, @RequestParam(required = false) Integer quick) {
        if (daily == null || quick == null) {
            System.out.println("daily use /param?daily=0 关闭 /param?daily=1 开启 ");
            System.out.println("quick use /param?quick=0 关闭 /param?quick=1 开启 ");
        }
        if (daily != null) {
            if (daily == 0) {
                KoeController.daily = false;
            } else if (daily == 1) {
                KoeController.daily = true;
            }
        }
        if (quick != null) {
            if (quick == 0) {
                KoeController.quick = false;
            } else if (quick == 1) {
                KoeController.quick = true;
            }
        }
        return info();
    }

    @RequestMapping("/stop")
    public String stop() {
        if (spider != null) {
            if (spider.getStatus() != Spider.Status.Stopped) {
                spider.stop();
            } else {
                return "spider is running please use stop";
            }
        } else {
            return "spider is never running please use start";
        }
        return this.info();
    }

    @RequestMapping("/info")
    public String info() {
        if (spider == null) return "spider is never running please use start";
        Spider.Status status = spider.getStatus();
        long pageCount = spider.getPageCount();
        Integer threadAlive = spider.getThreadAlive();
        MonitorableScheduler monitorableScheduler = (MonitorableScheduler) this.scheduler;
        int totalRequestsCount = monitorableScheduler.getTotalRequestsCount(spider);
        int leftRequestsCount = monitorableScheduler.getLeftRequestsCount(spider);
        long count = koeInfoRepository.count();
        return "status:" + status + "<br>"
                + " pageCount:" + pageCount + "<br>"
                + " totalRequestsCount:" + totalRequestsCount + "<br>"
                + " leftRequestsCount:" + leftRequestsCount + "<br>"
                + " DBCount:" + count + "<br>"
                + " threadAlive:" + threadAlive + "<br>"
                + "quick:" + quick + "<br>"
                + "daily:" + daily + "<br>";
    }


    @RequestMapping("/downinfo")
    public String downInfo() {
        long count = koeInfoRepository.count();
        long unDownload = koeInfoRepository.countByDownload(0);
        long download = koeInfoRepository.countByDownload(1);
        long downloadFail = koeInfoRepository.countByDownload(2);
        return "count:" + count
                + "<br>" + "unDownload:" + unDownload
                + "<br>" + "download:" + download
                + "<br>" + "downloadFail:" + downloadFail;
    }

    @RequestMapping("/fail")
    public String dl() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        List<KoeInfo> infoList = koeInfoRepository.findFirst100ByDownload(2);
        downList(threadPool, infoList);
        waitForDown(threadPool);
        return "ok";
    }


    public String down() {
        while (true) {
            ExecutorService threadPool = Executors.newFixedThreadPool(5);
            List<KoeInfo> infoList = koeInfoRepository.findFirst100ByDownload(0);
            if (infoList.size() == 0) break;
            downList(threadPool, infoList);
            waitForDown(threadPool);
        }
        return "down ok";
    }

    private void downList(ExecutorService threadPool, List<KoeInfo> infoList) {
        System.out.println("dl: " + infoList.size());
        for (KoeInfo koeInfo : infoList) {
            threadPool.execute(new Thread() {
                @Override
                public void run() {
                    KoeInfo info = DownLoad.dlKoe(koeInfo);
                    koeInfoRepository.saveAndFlush(info);
                }
            });
        }

    }

}
