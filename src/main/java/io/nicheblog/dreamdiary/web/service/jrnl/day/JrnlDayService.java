package io.nicheblog.dreamdiary.web.service.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.day.JrnlDayMapstruct;
import io.nicheblog.dreamdiary.web.model.jrnl.day.JrnlDayDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.day.JrnlDayRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.day.JrnlDaySpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * 캐시 사용 위해 구현체로 pullUp
     */
    @Override
    @Cacheable(value="jrnlDayList", key="#searchParam.getYy() + \"_\" + #searchParam.getMnth()")
    public List<JrnlDayDto> getListDto(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListDto(searchParamMap);
    }

    /**
     * 중복 체크 (정상시 true / 중복시 false)
     */
    public boolean dupChck(JrnlDayDto jrnlDay) throws Exception {
        boolean isDtUnknown = "Y".equals(jrnlDay.getDtUnknownYn());
        if (isDtUnknown) return false;
        Date jrnlDt = DateUtils.asDate(jrnlDay.getJrnlDt());
        Integer isDup = jrnlDayRepository.countByJrnlDt(jrnlDt);
        return isDup > 0;
    }

    /**
     * 중복시 해당하는 키값 반환
     */
    public Integer getDupKey(JrnlDayDto jrnlDay) throws Exception {
        Date jrnlDt = DateUtils.asDate(jrnlDay.getJrnlDt());
        JrnlDayEntity existingEntity = jrnlDayRepository.findByJrnlDt(jrnlDt);
        return existingEntity.getPostNo();
    }

    @CacheEvict(value="jrnlDayList", key="#jrnlDay.getYy() + '_' + #jrnlDay.getMnth()")
    public JrnlDayDto registWithCache(JrnlDayDto jrnlDay, MultipartHttpServletRequest request) throws Exception {
        return this.regist(jrnlDay, request);
    }

    /**
     * 신청 전처리:: 메소드 분리
     */
    @Override
    public void preRegist(final JrnlDayDto jrnlDay) throws Exception {
        // 년도/월 세팅:: 메소드 분리
        this.setYyMnth(jrnlDay);
    }

    @CacheEvict(value="jrnlDayList", key="#jrnlDay.getYy() + '_' + #jrnlDay.getMnth()")
    public JrnlDayDto modifyWithCache(JrnlDayDto jrnlDay, MultipartHttpServletRequest request) throws Exception {
        return this.modify(jrnlDay, request);
    }

    /**
     * 수정 전처리:: 메소드 분리
     */
    @Override
    public void preModify(final JrnlDayDto jrnlDay) throws Exception {
        // 년도/월 세팅:: 메소드 분리
        this.setYyMnth(jrnlDay);
    }

    /**
     * 년도/월 세팅:: 메소드 분리
     */
    public void setYyMnth(final JrnlDayDto jrnlDay) throws Exception {
        // 날짜미상여부 N시 대략일자 무효화
        if ("Y".equals(jrnlDay.getDtUnknownYn())) {
            jrnlDay.setJrnlDt("");
            jrnlDay.setYy(jrnlDay.getAprxmtDt().substring(0, 4));
            jrnlDay.setMnth(jrnlDay.getAprxmtDt().substring(5, 7));
        }
        if ("N".equals(jrnlDay.getDtUnknownYn())) {
            jrnlDay.setAprxmtDt("");
            jrnlDay.setYy(jrnlDay.getJrnlDt().substring(0, 4));
            jrnlDay.setMnth(jrnlDay.getJrnlDt().substring(5, 7));
        }
    }

    /**
     * 되나? 확인하기
     */
    @Override
    @CacheEvict(value="jrnlDayList", key="#rslt.getYy() + \"_\" + #rslt.getMnth()")
    public void postDelete(final JrnlDayEntity rslt) throws Exception {
        log.info("evict evict key: {}", rslt.getYy() + "_" + rslt.getMnth());
    }
}