package io.nicheblog.dreamdiary.domain.exptr.reqst.service;

import io.nicheblog.dreamdiary.domain.exptr.reqst.entity.ExptrReqstEntity;
import io.nicheblog.dreamdiary.domain.exptr.reqst.mapstruct.ExptrReqstMapstruct;
import io.nicheblog.dreamdiary.domain.exptr.reqst.model.ExptrReqstDto;
import io.nicheblog.dreamdiary.domain.exptr.reqst.repository.jpa.ExptrReqstRepository;
import io.nicheblog.dreamdiary.domain.exptr.reqst.spec.ExptrReqstSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * ExptrReqstService
 * <pre>
 *  물품구매/경조사비 신청 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("exptrReqstService")
@RequiredArgsConstructor
@Log4j2
public class ExptrReqstService
        implements BasePostService<ExptrReqstDto.DTL, ExptrReqstDto.LIST, Integer, ExptrReqstEntity, ExptrReqstRepository, ExptrReqstSpec, ExptrReqstMapstruct> {

    @Getter
    private final ExptrReqstRepository repository;
    @Getter
    private final ExptrReqstSpec spec;
    @Getter
    private final ExptrReqstMapstruct mapstruct = ExptrReqstMapstruct.INSTANCE;

    /**
     * 경비 관리 > 물품구매/경조사비 신청 > 처리완료 처리
     *
     * @param key 식별자
     * @return {@link ExptrReqstDto.DTL} -- 처리 결과
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public ExptrReqstDto.DTL cf(final Integer key) throws Exception {
        // Entity 레벨 조회
        ExptrReqstEntity entity = this.getDtlEntity(key);
        entity.setCfYn("Y");
        // update
        ExptrReqstEntity rsltEntity = repository.save(entity);
        ExptrReqstDto.DTL rsltDto = mapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }

    /**
     * 경비 관리 > 물품구매/경조사비 신청 > 처리안함 처리
     *
     * @param key 식별자
     * @return {@link ExptrReqstDto.DTL} -- 처리 결과
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public ExptrReqstDto.DTL dismiss(final Integer key) throws Exception {
        // Entity 레벨 조회
        ExptrReqstEntity entity = this.getDtlEntity(key);
        entity.setCfYn("X");
        // update
        ExptrReqstEntity rsltEntity = repository.save(entity);
        ExptrReqstDto.DTL rsltDto = mapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }
}