package cn.it.koe_spider.repository;

import cn.it.koe_spider.domain.KoeInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KoeInfoRepository extends JpaRepository<KoeInfo, Integer> {

    List<KoeInfo> findFirst100ByDownload(Integer download);

    long countByDownload(Integer download);

    @Query("select k from KoeInfo k order by k.id desc")
    List<KoeInfo> getLatest(Pageable pageable);
}
