package io.nicheblog.dreamdiary.domain.jrnl.dream.service.impl;

import io.nicheblog.dreamdiary.auth.exception.NotAuthorizedException;
import io.nicheblog.dreamdiary.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.mapstruct.JrnlDreamMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamDto;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamSearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.dream.repository.jpa.JrnlDreamRepository;
import io.nicheblog.dreamdiary.domain.jrnl.dream.repository.mybatis.JrnlDreamMapper;
import io.nicheblog.dreamdiary.domain.jrnl.dream.service.JrnlDreamService;
import io.nicheblog.dreamdiary.domain.jrnl.dream.spec.JrnlDreamSpec;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common.cache.event.EhCacheEvictEvent;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * JrnlDreamService
 * <pre>
 *  저널 꿈 관리 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("jrnlDreamService")
@RequiredArgsConstructor
@Log4j2
public class JrnlDreamServiceImpl
        implements JrnlDreamService {

    @Getter
    private final JrnlDreamRepository repository;
    @Getter
    private final JrnlDreamSpec spec;
    @Getter
    private final JrnlDreamMapstruct mapstruct = JrnlDreamMapstruct.INSTANCE;

    private final JrnlDreamMapper jrnlDreamMapper;
    private final ApplicationEventPublisher publisher;

    private final ApplicationContext context;
    private JrnlDreamServiceImpl getSelf() {
        return context.getBean(this.getClass());
    }

    private final String JRNL_DREAM = ContentType.JRNL_DREAM.key;

    /**
     * 목록 조회 (dto level) :: 캐시 처리
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 조회된 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="jrnlDreamList", key="#searchParam.hashCode()")
    public List<JrnlDreamDto> getListDtoWithCache(final BaseSearchParam searchParam) throws Exception {
        searchParam.setRegstrId(AuthUtils.getLgnUserId());
        return this.getSelf().getListDto(searchParam);
    }

    /**
     * 특정 년도의 중요 꿈 목록 조회 :: 캐시 처리
     *
     * @param yy 조회할 년도
     * @return {@link List} -- 해당 년도의 중요 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="imprtcDreamList", key="#yy")
    public List<JrnlDreamDto> getImprtcDreamList(final Integer yy) throws Exception {
        final JrnlDreamSearchParam searchParam = JrnlDreamSearchParam.builder().yy(yy).imprtcYn("Y").build();
        final List<JrnlDreamDto> imprtcDreamList = this.getSelf().getListDto(searchParam);
        Collections.sort(imprtcDreamList);

        return imprtcDreamList;
    }

    /**
     * 특정 태그의 관련 꿈 목록 조회 :: 캐시 처리
     *
     * @param searchParam 검색 조건이 담긴 파라미터 객체
     * @return {@link List} -- 검색 결과 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="jrnlDreamTagDtl", key="#searchParam.hashCode()")
    public List<JrnlDreamDto> jrnlDreamTagDtl(final BaseSearchParam searchParam) throws Exception {
        final Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);

        return this.getSelf().getListDto(searchParamMap);
    }

    /**
     * 등록 전처리. (override)
     *
     * @param registDto 등록할 객체
     */
    @Override
    public void preRegist(final JrnlDreamDto registDto) {
        if (!"Y".equals(registDto.getElseDreamYn())) {
            // 인덱스(정렬순서) 처리
            final Integer lastIndex = repository.findLastIndexByJrnlDay(registDto.getJrnlDayNo()).orElse(0);
            registDto.setIdx(lastIndex + 1);
        }
    }

    /**
     * 등록 후처리. (override)
     *
     * @param updatedEntity - 등록된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postRegist(final JrnlDreamEntity updatedEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishEvent(new EhCacheEvictEvent(this, updatedEntity.getPostNo(), JRNL_DREAM));
    }

    /**
     * 상세 조회 (dto level) :: 캐시 처리
     *
     * @param key 식별자
     * @return {@link JrnlDreamDto} -- 조회된 객체
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="jrnlDreamDtlDto", key="#key")
    public JrnlDreamDto getDtlDtoWithCache(final Integer key) throws Exception {
        JrnlDreamDto retrieved = this.getSelf().getDtlDto(key);
        // 권한 체크
        if (!retrieved.getIsRegstr()) throw new NotAuthorizedException("조회 권한이 없습니다.");
        return retrieved;
    }

    /**
     * 수정 후처리. (override)
     *
     * @param updatedEntity - 수정된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postModify(final JrnlDreamEntity updatedEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishEvent(new EhCacheEvictEvent(this, updatedEntity.getPostNo(), JRNL_DREAM));
    }

    /**
     * 삭제 전처리. (override)
     * 등록자가 아니면 삭제 불가 처리.
     *
     * @param deleteEntity - 삭제 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void preDelete(JrnlDreamEntity deleteEntity) throws Exception {
        if (!deleteEntity.isRegstr()) throw new NotAuthorizedException("삭제 권한이 없습니다.");
    };

    /**
     * 삭제 후처리. (override)
     *
     * @param deletedEntity - 삭제된 엔티티
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postDelete(final JrnlDreamEntity deletedEntity) throws Exception {
        // 관련 캐시 삭제
        publisher.publishEvent(new EhCacheEvictEvent(this, deletedEntity.getPostNo(), JRNL_DREAM));
        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 삭제 데이터 조회
     *
     * @param key 삭제된 데이터의 키
     * @return {@link JrnlDreamDto} -- 삭제된 데이터 DTO
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Override
    @Transactional(readOnly = true)
    public JrnlDreamDto getDeletedDtlDto(final Integer key) throws Exception {
        return jrnlDreamMapper.getDeletedByPostNo(key);
    }
}