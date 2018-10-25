package cn.it.koe_spider.crawl;

import cn.it.koe_spider.domain.KoeInfo;
import cn.it.koe_spider.repository.KoeInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component
public class KoePipeLine implements Pipeline {

    @Autowired
    KoeInfoRepository koeInfoRepository;

    @Override
    public void process(ResultItems resultItems, Task task) {

        KoeInfo info = resultItems.get("koeInfo");
        if (info != null) {

            KoeInfo one = koeInfoRepository.findOne(info.getId());
            //新内容，或未保存到数据库的内容
            if (one == null) {

                if (StringUtils.isEmpty(info.getTitle())) {
                    info.setTitle(info.getId().toString());
                }
                try {
                    koeInfoRepository.saveAndFlush(info);
                } catch (Exception e) {
                    info.setDescriptions(info.getAuthor());
                    try {
                        koeInfoRepository.saveAndFlush(info);
                    } catch (Exception e1) {
                        System.out.println("save " + info.getId() + " fail");
                    }
                }

            }
        }
    }

}
