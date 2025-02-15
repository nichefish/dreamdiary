package io.nicheblog.dreamdiary.domain.jrnl.dream.service;

import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.mapstruct.JrnlDreamMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamDto;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamSearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.dream.repository.jpa.JrnlDreamRepository;
import io.nicheblog.dreamdiary.domain.jrnl.dream.spec.JrnlDreamSpec;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseClsfService;

import java.util.List;
import java.util.Map;

/**
 * JrnlDreamService
 * <pre>
 *  저널 꿈 관리 서비스 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
public interface JrnlDreamService
        extends BaseClsfService<JrnlDreamDto, JrnlDreamDto, Integer, JrnlDreamEntity, JrnlDreamRepository, JrnlDreamSpec, JrnlDreamMapstruct> {

    /**
     * 목록 조회 (dto level) :: 캐시 처리
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 조회된 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    List<JrnlDreamDto> getListDtoWithCache(final BaseSearchParam searchParam) throws Exception;

    /**
     * 특정 저널 일자에 대한 목록 조회 (entity level) :: 캐시 처리
     *
     * @param jrnlDayNo 저널 일자 번호
     * @return {@link List} -- 조회된 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    List<JrnlDreamEntity> getMyListEntityByJrnlDay(final Integer jrnlDayNo) throws Exception;

    /**
     * default: 항목 목록 조회 (entity level)
     *
     * @param searchParam 검색 조건 파라미터
     * @return {@link List} -- 목록 (entity level)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    List<JrnlDreamEntity> getListEntityWithTag(final BaseSearchParam searchParam) throws Exception;

    /**
     * default: 항목 목록 조회 (entity level)
     *
     * @param searchParamMap 검색 조건 파라미터 맵
     * @return {@link List} -- 목록 (entity level)
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    List<JrnlDreamEntity> getListEntityWithTag(final Map<String, Object> searchParamMap) throws Exception;

    /**
     * 특정 년도의 중요 꿈 목록 조회 :: 캐시 처리
     *
     * @param yy 조회할 년도
     * @return {@link List} -- 해당 년도의 중요 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    List<JrnlDreamDto> getImprtcDreamList(final Integer yy) throws Exception;

    /**
     * 특정 태그의 관련 꿈 목록 조회 :: 캐시 처리
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 검색 결과 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    List<JrnlDreamDto> jrnlDreamTagDtl(final JrnlDreamSearchParam searchParam) throws Exception;

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     *
     * @param key 식별자
     * @return {@link JrnlDreamDto} -- 조회된 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    JrnlDreamDto getDtlDtoWithCache(final Integer key) throws Exception;

    /**
     * 삭제 데이터 조회
     *
     * @param key 삭제된 데이터의 키
     * @return {@link JrnlDreamDto} -- 삭제된 데이터 DTO
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    JrnlDreamDto getDeletedDtlDto(final Integer key) throws Exception;
}