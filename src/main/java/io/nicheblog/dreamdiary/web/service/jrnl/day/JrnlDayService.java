package io.nicheblog.dreamdiary.web.service.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.day.JrnlDayMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.day.JrnlDayRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.day.JrnlDaySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * JrnlDayService
 * <pre>
 *  저널 일자 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDayService")
@Log4j2
public class JrnlDayService
        implements BaseMultiCrudService<JrnlDayDto, JrnlDayDto, Integer, JrnlDayEntity, JrnlDayRepository, JrnlDaySpec, JrnlDayMapstruct> {

    private final JrnlDayMapstruct jrnlDayMapstruct = JrnlDayMapstruct.INSTANCE;

    @Resource(name = "jrnlDayRepository")
    private JrnlDayRepository jrnlDayRepository;
    @Resource(name = "jrnlDaySpec")
    private JrnlDaySpec jrnlDaySpec;

    @Override
    public JrnlDayRepository getRepository() {
        return this.jrnlDayRepository;
    }
    @Override
    public JrnlDayMapstruct getMapstruct() {
        return this.jrnlDayMapstruct;
    }
    @Override
    public JrnlDaySpec getSpec() {
        return this.jrnlDaySpec;
    }

    /**
     * 신청 전처리:: 메소드 분리
     */
    @Override
    public void preRegist(final JrnlDayDto jrnlDay) throws Exception {
        // 날짜미상여부 N시 대략일자 무효화
        if ("Y".equals(jrnlDay.getDtUnknownYn())) jrnlDay.setJrnlDt("");
        if ("N".equals(jrnlDay.getDtUnknownYn())) jrnlDay.setAprxmtDt("");

    }

    /**
     * 수정 전처리:: 메소드 분리
     */
    @Override
    public void preModify(final JrnlDayDto jrnlDay) throws Exception {
        // 날짜미상여부 N시 대략일자 무효화
        if ("Y".equals(jrnlDay.getDtUnknownYn())) jrnlDay.setJrnlDt("");
        if ("N".equals(jrnlDay.getDtUnknownYn())) jrnlDay.setAprxmtDt("");
    }

}