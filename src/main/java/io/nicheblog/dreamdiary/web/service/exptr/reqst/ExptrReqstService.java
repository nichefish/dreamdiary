package io.nicheblog.dreamdiary.web.service.exptr.reqst;

import io.nicheblog.dreamdiary.global.intrfc.service.BasePostService;
import io.nicheblog.dreamdiary.web.entity.exptr.reqst.ExptrReqstEntity;
import io.nicheblog.dreamdiary.web.mapstruct.exptr.reqst.ExptrReqstMapstruct;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstDto;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstListDto;
import io.nicheblog.dreamdiary.web.repository.exptr.reqst.ExptrReqstRepository;
import io.nicheblog.dreamdiary.web.spec.exptr.reqst.ExptrReqstSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * ExptrReqstService
 * <pre>
 *  물품구매/경조사비 신청 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BasePostService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("exptrReqstService")
@Log4j2
public class ExptrReqstService
        implements BasePostService<ExptrReqstDto, ExptrReqstListDto, Integer, ExptrReqstEntity, ExptrReqstRepository, ExptrReqstSpec, ExptrReqstMapstruct> {

    @Resource(name = "exptrReqstRepository")
    private ExptrReqstRepository exptrReqstRepository;
    @Resource(name = "exptrReqstSpec")
    public ExptrReqstSpec exptrReqstSpec;

    private final ExptrReqstMapstruct exptrReqstMapstruct = ExptrReqstMapstruct.INSTANCE;

    @Override
    public ExptrReqstRepository getRepository() {
        return this.exptrReqstRepository;
    }

    @Override
    public ExptrReqstSpec getSpec() {
        return this.exptrReqstSpec;
    }

    @Override
    public ExptrReqstMapstruct getMapstruct() {
        return this.exptrReqstMapstruct;
    }

    /**
     * 경비 관리 > 물품구매/경조사비 신청 > 처리완료
     */
    public ExptrReqstDto exptrReqstCf(final Integer key) throws Exception {
        // Entity 레벨 조회
        ExptrReqstEntity entity = this.getDtlEntity(key);
        entity.setCfYn("Y");
        // update
        ExptrReqstEntity rsltEntity = exptrReqstRepository.save(entity);
        ExptrReqstDto rsltDto = exptrReqstMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }

    /**
     * 경비 관리 > 물품구매/경조사비 신청 > 처리안함
     */
    public ExptrReqstDto exptrReqstDismiss(final Integer key) throws Exception {
        // Entity 레벨 조회
        ExptrReqstEntity entity = this.getDtlEntity(key);
        entity.setCfYn("X");
        // update
        ExptrReqstEntity rsltEntity = exptrReqstRepository.save(entity);
        ExptrReqstDto rsltDto = exptrReqstMapstruct.toDto(rsltEntity);
        rsltDto.setIsSuccess(rsltEntity.getPostNo() != null);
        return rsltDto;
    }
}