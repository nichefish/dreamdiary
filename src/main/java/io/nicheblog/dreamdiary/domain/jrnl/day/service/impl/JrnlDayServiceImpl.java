package io.nicheblog.dreamdiary.domain.jrnl.day.service.impl;

import io.nicheblog.dreamdiary.auth.security.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDayEntity;
import io.nicheblog.dreamdiary.domain.jrnl.day.mapstruct.JrnlDayMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDayDto;
import io.nicheblog.dreamdiary.domain.jrnl.day.model.JrnlDaySearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.day.repository.jpa.JrnlDayRepository;
import io.nicheblog.dreamdiary.domain.jrnl.day.repository.mybatis.JrnlDayMapper;
import io.nicheblog.dreamdiary.domain.jrnl.day.service.JrnlDayService;
import io.nicheblog.dreamdiary.domain.jrnl.day.service.strategy.JrnlDayCacheEvictor;
import io.nicheblog.dreamdiary.domain.jrnl.day.spec.JrnlDaySpec;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.model.JrnlDiaryDto;
import io.nicheblog.dreamdiary.domain.jrnl.diary.service.JrnlDiaryService;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.service.JrnlDreamService;
import io.nicheblog.dreamdiary.extension.cache.event.EhCacheEvictEvent;
import io.nicheblog.dreamdiary.extension.cache.handler.EhCacheEvictEventListner;
import io.nicheblog.dreamdiary.extension.clsf.ContentType;
import io.nicheblog.dreamdiary.global.handler.ApplicationEventPublisherWrapper;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class JrnlDayServiceImpl
        implements JrnlDayService {

    @Getter
    private final JrnlDayRepository repository;
    @Getter
    private final JrnlDaySpec spec;
    @Getter
    private final JrnlDayMapstruct mapstruct = JrnlDayMapstruct.INSTANCE;

    private final JrnlDayMapper jrnlDayMapper;
    private final JrnlDiaryService jrnlDiaryService;
    private final JrnlDreamService jrnlDreamService;
    private final ApplicationEventPublisherWrapper publisher;

    private final String JRNL_DAY = ContentType.JRNL_DAY.key;

    private final ApplicationContext context;
    private JrnlDayServiceImpl getSelf() {
        return context.getBean(this.getClass());
    }

    /**
     * 내 목록 조회 (dto level) :: 캐시 처리
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 조회된 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="myJrnlDayList", key="T(io.nicheblog.dreamdiary.auth.security.util.AuthUtils).getLgnUserId() + \"_\" + #searchParam.getYy() + \"_\" + #searchParam.getMnth()")
    public List<JrnlDayDto> getMyListDto(final JrnlDaySearchParam searchParam) throws Exception {
        searchParam.setRegstrId(AuthUtils.getLgnUserId());

        final List<JrnlDayEntity> myJrnlDayListEntity = this.getSelf().getListEntityWithTag(searchParam);
        return myJrnlDayListEntity.stream()
                .map(entity -> {
                    final Integer jrnlDayNo = entity.getPostNo();
                    try {
                        // 저널 일기
                        final List<JrnlDiaryEntity> diaryList = jrnlDiaryService.getMyListEntityByJrnlDay(jrnlDayNo);
                        entity.setJrnlDiaryList(diaryList);
                        // 저널 꿈
                        final List<JrnlDreamEntity> dreamList = jrnlDreamService.getMyListEntityByJrnlDay(jrnlDayNo);
                        // 내 꿈
                        final List<JrnlDreamEntity> myDreamList = dreamList.stream()
                                .filter(dream -> "N".equals(dream.getElseDreamYn()))
                                .toList();
                        entity.setJrnlDreamList(myDreamList);
                        // 타인 꿈
                        final List<JrnlDreamEntity> elseDreamList = dreamList.stream()
                                .filter(dream -> "Y".equals(dream.getElseDreamYn()))
                                .toList();
                        entity.setJrnlElseDreamList(elseDreamList);
                        return mapstruct.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * default: 항목 목록 조회 (entity level)
     *
     * @param searchParam 검색 조건 파라미터
     * @return {@link List} -- 목록 (entity level)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public List<JrnlDayEntity> getListEntityWithTag(final BaseSearchParam searchParam) throws Exception {
        final Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        final Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);

        return this.getListEntityWithTag(filteredSearchKey);
    }

    /**
     * default: 항목 목록 조회 (entity level)
     *
     * @param searchParamMap 검색 조건 파라미터 맵
     * @return {@link List} -- 목록 (entity level)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public List<JrnlDayEntity> getListEntityWithTag(final Map<String, Object> searchParamMap) throws Exception {
        return repository.findAll(spec.searchWith(searchParamMap));
    }

    /**
     * 중복 체크 (정상시 true / 중복시 false)
     *
     * @param jrnlDay {@link JrnlDayDto} -- 중복 여부를 확인할 {@link JrnlDayDto} 객체
     * @return {@link boolean} -- 정상 시 true, 중복 시 false 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional(readOnly = true)
    public boolean dupChck(final JrnlDayDto jrnlDay) throws Exception {
        final boolean isDtUnknown = "Y".equals(jrnlDay.getDtUnknownYn());
        if (isDtUnknown) return false;

        final Date jrnlDt = DateUtils.asDate(jrnlDay.getJrnlDt());
        final String regstrId = AuthUtils.getLgnUserId();
        final Integer isDup = repository.countByJrnlDt(jrnlDt, regstrId);

        return isDup > 0;
    }

    /**
     * 날짜 기준으로 중복(해당 데이터 존재)시 해당 키값 반환
     *
     * @param jrnlDay {@link JrnlDayDto} -- 중복 여부를 확인할 {@link JrnlDayDto} 객체
     * @return {@link Integer} -- 중복되는 경우 해당하는 키값 (게시글 번호)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getDupKey(final JrnlDayDto jrnlDay) throws Exception {
        final Date jrnlDt = DateUtils.asDate(jrnlDay.getJrnlDt());
        final String regstrId = AuthUtils.getLgnUserId();
        final JrnlDayEntity existingEntity = repository.findByJrnlDt(jrnlDt, regstrId);

        return existingEntity.getPostNo();
    }

    /**
     * 특정 태그의 관련 일자 목록 조회
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 검색 결과 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="myJrnlDayTagDtl", key="T(io.nicheblog.dreamdiary.auth.security.util.AuthUtils).getLgnUserId() + \"_\" + #searchParam.getTagNo()")
    public List<JrnlDayDto> jrnlDayTagDtl(final JrnlDaySearchParam searchParam) throws Exception {
        searchParam.setSort("DESC");

        return this.getSelf().getListDto(searchParam);
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
     * 상세 조회 (dto level) :: 캐시 처리
     *
     * @param key 식별자
     * @return {@link JrnlDiaryDto} -- 조회된 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="myJrnlDayDtlDto", key="T(io.nicheblog.dreamdiary.auth.security.util.AuthUtils).getLgnUserId() + \"_\" + #key")
    public JrnlDayDto getDtlDtoWithCache(final Integer key) throws Exception {
        final JrnlDayDto retrieved = this.getSelf().getDtlDto(key);
        // 권한 체크
        if (!retrieved.getIsRegstr()) throw new NotAuthorizedException(MessageUtils.getMessage("common.rslt.access-not-authorized"));
        return retrieved;
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
    }

    /**
     * 날짜 기반으로 년도/월 항목 세팅 :: 메소드 분리
     *
     * @param jrnlDay 날짜 기반으로 년도와 월을 설정할 {@link JrnlDayDto} 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
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
     * 삭제 전처리. (override)
     * 등록자가 아니면 삭제 불가 처리.
     *
     * @param deleteEntity - 삭제 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void preDelete(final JrnlDayEntity deleteEntity) throws Exception {
        log.info("regstrId: {}", deleteEntity.getRegstrId());
        if (!deleteEntity.isRegstr()) throw new NotAuthorizedException(MessageUtils.getMessage("delete-not-authorized"));
    }

    /**
     * 삭제 데이터 조회
     *
     * @param key 삭제된 데이터의 키
     * @return {@link JrnlDayDto} -- 삭제된 데이터 DTO
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional(readOnly = true)
    public JrnlDayDto getDeletedDtlDto(final Integer key) throws Exception {
        return jrnlDayMapper.getDeletedByPostNo(key);
    }

    /**
     * 주요 처리 후 캐시 삭제
     *
     * @param jrnlDayEntity 캐시 삭제 판단에 필요한 객체
     * @throws Exception 처리 중 발생 가능한 예외
     * @see EhCacheEvictEventListner
     * @see JrnlDayCacheEvictor
     */
    @Override
    public void evictCache(final JrnlDayEntity jrnlDayEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishAsyncEvent(new EhCacheEvictEvent(this, jrnlDayEntity.getPostNo(), JRNL_DAY));
    }
}