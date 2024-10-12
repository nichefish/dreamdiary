package io.nicheblog.dreamdiary.domain.jrnl.day.service;

import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntity;
import io.nicheblog.dreamdiary.domain.jrnl.day.mapstruct.JrnlDayMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayDto;
import io.nicheblog.dreamdiary.domain.jrnl.day.repository.jpa.JrnlDayRepository;
import io.nicheblog.dreamdiary.domain.jrnl.day.repository.mybatis.JrnlDayMapper;
import io.nicheblog.dreamdiary.domain.jrnl.day.spec.JrnlDaySpec;
import io.nicheblog.dreamdiary.domain.jrnl.diary.model.JrnlDiaryDto;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common.cache.event.EhCacheEvictEvent;
import io.nicheblog.dreamdiary.global._common.cache.util.EhCacheUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 */
@Service("jrnlDayService")
@RequiredArgsConstructor
@Log4j2
public class JrnlDayService
        implements BaseMultiCrudService<JrnlDayDto, JrnlDayDto, Integer, JrnlDayEntity, JrnlDayRepository, JrnlDaySpec, JrnlDayMapstruct> {

    @Getter
    private final JrnlDayRepository repository;
    @Getter
    private final JrnlDaySpec spec;
    @Getter
    private final JrnlDayMapstruct mapstruct = JrnlDayMapstruct.INSTANCE;

    private final JrnlDayMapper jrnlDayMapper;
    private final ApplicationEventPublisher publisher;

    private final String JRNL_DAY = ContentType.JRNL_DAY.key;

    /**
     * 목록 조회 (dto level) :: 캐시 처리
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 조회된 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Cacheable(value="jrnlDayList", key="#searchParam.getYy() + \"_\" + #searchParam.getMnth()")
    public List<JrnlDayDto> getListDtoWithCache(final BaseSearchParam searchParam) throws Exception {
        return this.getListDto(searchParam);
    }

    /**
     * 중복 체크 (정상시 true / 중복시 false)
     *
     * @param jrnlDay {@link JrnlDayDto} -- 중복 여부를 확인할 {@link JrnlDayDto} 객체
     * @return {@link boolean} -- 정상 시 true, 중복 시 false 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public boolean dupChck(final JrnlDayDto jrnlDay) throws Exception {
        final boolean isDtUnknown = "Y".equals(jrnlDay.getDtUnknownYn());
        if (isDtUnknown) return false;

        final Date jrnlDt = DateUtils.asDate(jrnlDay.getJrnlDt());
        final Integer isDup = repository.countByJrnlDt(jrnlDt);

        return isDup > 0;
    }

    /**
     * 날짜 기준으로 중복(해당 데이터 존재)시 해당 키값 반환
     *
     * @param jrnlDay {@link JrnlDayDto} -- 중복 여부를 확인할 {@link JrnlDayDto} 객체
     * @return {@link Integer} -- 중복되는 경우 해당하는 키값 (게시글 번호)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public Integer getDupKey(final JrnlDayDto jrnlDay) throws Exception {
        final Date jrnlDt = DateUtils.asDate(jrnlDay.getJrnlDt());
        final JrnlDayEntity existingEntity = repository.findByJrnlDt(jrnlDt);

        return existingEntity.getPostNo();
    }

    /**
     * 특정 태그의 관련 일자 목록 조회
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 검색 결과 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Cacheable(value="jrnlDayTagDtl", key="#searchParam.hashCode()")
    public List<JrnlDayDto> jrnlDayTagDtl(final BaseSearchParam searchParam) throws Exception {
        final Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);

        return this.getListDto(searchParamMap);
    }

    /**
     * 등록 전처리. (override)
     *
     * @param registDto 등록할 객체
     */
    @Override
    public void preRegist(final JrnlDayDto registDto) throws Exception {
        // 년도/월 세팅:: 메소드 분리
        this.setYyMnth(registDto);
    }

    /**
     * 등록 후처리. (override)
     *
     * @param updatedEntity - 등록된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postRegist(final JrnlDayEntity updatedEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishEvent(new EhCacheEvictEvent(this, updatedEntity.getPostNo(), JRNL_DAY));
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     *
     * @param key 식별자
     * @return {@link JrnlDiaryDto} -- 조회된 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Cacheable(value="jrnlDayDtlDto", key="#key")
    public JrnlDayDto getDtlDtoWithCache(final Integer key) throws Exception {
        return this.getDtlDto(key);
    }

    /**
     * 수정 전처리. (override)
     *
     * @param modifyDto 수정할 객체
     */
    @Override
    public void preModify(final JrnlDayDto modifyDto) throws Exception {
        // 년도/월 세팅:: 메소드 분리
        this.setYyMnth(modifyDto);
        // 수정 전 정보 캐시 삭제
        EhCacheUtils.evictCache("jrnlDayList", modifyDto.getYy() + "_" + modifyDto.getMnth());
        EhCacheUtils.evictCache("jrnlDayList", modifyDto.getYy() + "_99");
    }

    /**
     * 수정 후처리. (override)
     *
     * @param updatedEntity - 수정된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postModify(final JrnlDayEntity updatedEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishEvent(new EhCacheEvictEvent(this, updatedEntity.getPostNo(), JRNL_DAY));
    }

    /**
     * 날짜 기반으로 년도/월 항목 세팅 :: 메소드 분리
     *
     * @param jrnlDay 날짜 기반으로 년도와 월을 설정할 {@link JrnlDayDto} 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
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
     * 삭제 후처리. (override)
     *
     * @param deletedEntity - 삭제된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postDelete(final JrnlDayEntity deletedEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishEvent(new EhCacheEvictEvent(this, deletedEntity.getPostNo(), JRNL_DAY));
        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 삭제 데이터 조회
     *
     * @param key 삭제된 데이터의 키
     * @return {@link JrnlDayDto} -- 삭제된 데이터 DTO
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional(readOnly = true)
    public JrnlDayDto getDeletedDtlDto(final Integer key) throws Exception {
        return jrnlDayMapper.getDeletedByPostNo(key);
    }
}