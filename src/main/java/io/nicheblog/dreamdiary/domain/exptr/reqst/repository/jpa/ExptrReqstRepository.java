package io.nicheblog.dreamdiary.domain.exptr.reqst.repository.jpa;

import io.nicheblog.dreamdiary.domain.exptr.reqst.entity.ExptrReqstEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * ExptrReqstRepository
 * <pre>
 *  물품구매/경조사비 신청 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("exptrReqstRepository")
public interface ExptrReqstRepository
        extends BaseStreamRepository<ExptrReqstEntity, Integer> {
    //
}