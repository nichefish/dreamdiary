package io.nicheblog.dreamdiary.web.repository.schdul;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

/**
 * SchdulRepository
 * <pre>
 *  일정 상세 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("schdulRepository")
public interface SchdulRepository
        extends BaseStreamRepository<SchdulEntity, Integer> {

    /**
     * 날짜(date)에 대하여 휴일 해당 여부 조회
     */
    Optional<SchdulEntity> findBySchdulCdAndBgnDt(
            final String groupId,
            final Date date
    );
}