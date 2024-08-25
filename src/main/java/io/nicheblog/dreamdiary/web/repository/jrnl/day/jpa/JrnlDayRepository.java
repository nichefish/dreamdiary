package io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * JrnlDayRepository
 * <pre>
 *  꿈일자 Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("jrnlDayRepository")
public interface JrnlDayRepository
        extends BaseStreamRepository<JrnlDayEntity, Integer> {

    /** 중복 체크 */
    @Query("SELECT COUNT(day.jrnlDt) " +
            "FROM JrnlDayEntity day " +
            "WHERE day.jrnlDt = :jrnlDt")
    Integer countByJrnlDt(@Param("jrnlDt") Date jrnlDt);

    @Query("SELECT day " +
            "FROM JrnlDayEntity day " +
            "WHERE day.jrnlDt = :jrnlDt")
    JrnlDayEntity findByJrnlDt(@Param("jrnlDt") Date jrnlDt);
}