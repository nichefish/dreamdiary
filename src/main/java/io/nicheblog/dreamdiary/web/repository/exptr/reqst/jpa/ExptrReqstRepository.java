package io.nicheblog.dreamdiary.web.repository.exptr.reqst.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.exptr.reqst.ExptrReqstEntity;
import org.springframework.stereotype.Repository;

/**
 * ExptrReqstRepository
 * <pre>
 *  물품구매/경조사비 신청 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("exptrReqstRepository")
public interface ExptrReqstRepository
        extends BaseStreamRepository<ExptrReqstEntity, Integer> {
    //
}