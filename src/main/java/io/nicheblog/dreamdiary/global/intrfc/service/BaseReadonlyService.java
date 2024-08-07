package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseCrudMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * BaseReadonlyService
 * <pre>
 *  (공통/상속) 조회 전용 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseReadonlyService<Dto extends BaseCrudDto & Identifiable<Key>, ListDto extends BaseCrudDto, Key extends Serializable, Entity extends BaseCrudEntity, Repository extends BaseStreamRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseCrudMapstruct<Dto, ListDto, Entity>> {

    // Resource : repository
    Repository getRepository();

    // Resource : spec
    Spec getSpec();

    // Resource : mapstruct
    Mapstruct getMapstruct();

    /**
     * default: 항목 페이징 목록 조회 (dto level)
     */
    default Page<ListDto> getPageDto(final BaseSearchParam searchParam, final Pageable pageable) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getPageDto(searchParamMap, pageable);
    }

    /**
     * default: 항목 페이징 목록 조회 (dto level)
     */
    default Page<ListDto> getPageDto(final Map<String, Object> searchParamMap, final Pageable pageable) throws Exception {
        // searchParamMap에서 빈 값들 및 쓸모없는 값들 정리
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);

        return this.pageEntityToDto(this.getPageEntity(filteredSearchKey, pageable));
    }

    /**
     * default: 항목 페이징 목록 조회 (entity level)
     */
    default Page<Entity> getPageEntity(final Map<String, Object> searchParamMap, final Pageable pageable) throws Exception {
        Repository repository = this.getRepository();
        Spec spec = this.getSpec();

        return repository.findAll(spec.searchWith(searchParamMap), pageable);
    }

    /**
     * default: 항목 페이징 목록 Page<Entity>->Page<Dto> 변환
     */
    default Page<ListDto> pageEntityToDto(final Page<Entity> entityPage) throws Exception {
        Mapstruct mapstruct = this.getMapstruct();
        AtomicInteger counter = new AtomicInteger(0);
        List<ListDto> dtoList = entityPage.stream()
                .map(entity -> {
                    try {
                        ListDto listDto = mapstruct.toListDto(entity);
                        listDto.setRnum(CmmUtils.getPageRnum(entityPage, counter.getAndIncrement()));
                        return listDto;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
    }

    /* ----- */

    /**
     * default: 항목 목록 조회 (dto level)
     */
    default List<ListDto> getListDto(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListDto(searchParamMap);
    }

    /**
     * default: 항목 목록 조회 (dto level)
     */
    default List<ListDto> getListDto(final Map<String, Object> searchParamMap) throws Exception {
        // searchParamMap에서 빈 값들 및 쓸모없는 값들 정리
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);

        return this.listEntityToDto(this.getListEntity(filteredSearchKey));
    }

    /**
     * default: 항목 목록 조회 (+정렬) (dto level)
     */
    default List<ListDto> getListDto(final BaseSearchParam searchParam, Sort sort) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListDto(searchParamMap, sort);
    }

    /**
     * default: 항목 목록 조회 (+정렬) (dto level)
     */
    default List<ListDto> getListDto(final Map<String, Object> searchParamMap, Sort sort) throws Exception {
        // searchParamMap에서 빈 값들 및 쓸모없는 값들 정리
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);

        return this.listEntityToDto(this.getListEntity(filteredSearchKey, sort));
    }

    /**
     * default: 항목 목록 조회 (entity level)
     */
    default List<Entity> getListEntity(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListEntity(searchParamMap);
    }

    /**
     * default: 항목 목록 조회 (entity level)
     */
    default List<Entity> getListEntity(final Map<String, Object> searchParamMap) throws Exception {
        Repository repository = this.getRepository();
        Spec spec = this.getSpec();

        return repository.findAll(spec.searchWith(searchParamMap));
    }

    /**
     * default: 항목 목록 조회 (+정렬) (entity level)
     */
    default List<Entity> getListEntity(final BaseSearchParam searchParam, Sort sort) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getListEntity(searchParamMap, sort);
    }

    /**
     * default: 항목 목록 조회 (+정렬) (entity level)
     */
    default List<Entity> getListEntity(final Map<String, Object> searchParamMap, Sort sort) throws Exception {
        Repository repository = this.getRepository();
        Spec spec = this.getSpec();

        return repository.findAll(spec.searchWith(searchParamMap), sort);
    }

    /**
     * default: 항목 목록 Page<Entity>->Page<Dto> 변환
     */
    default List<ListDto> listEntityToDto(final List<Entity> entityList) throws Exception {
        Mapstruct mapstruct = this.getMapstruct();
        return entityList.stream()
                .map(entity -> {
                    try {
                        return mapstruct.toListDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /* ----- */

    /**
     * default: Stream<Entity> 변환
     */
    default Stream<Entity> getStreamEntity(final BaseSearchParam searchParam) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getStreamEntity(searchParamMap);
    }

    /**
     * default: Stream<Entity> 변환
     */
    default Stream<Entity> getStreamEntity(final Map<String, Object> searchParamMap) throws Exception {
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);
        Repository repository = this.getRepository();
        Spec spec = this.getSpec();
        return repository.streamAllBy(spec.searchWith(filteredSearchKey));
    }

    /**
     * default: Stream<Entity> (+정렬) 변환
     */
    default Stream<Entity> getStreamEntity(final BaseSearchParam searchParam, Sort sort) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        return this.getStreamEntity(searchParamMap, sort);
    }

    /**
     * default: Stream<Entity> 변환
     */
    default Stream<Entity> getStreamEntity(final Map<String, Object> searchParamMap, Sort sort) throws Exception {
        Map<String, Object> filteredSearchKey = CmmUtils.Param.filterParamMap(searchParamMap);
        Repository repository = this.getRepository();
        Spec spec = this.getSpec();
        return repository.streamAllBy(spec.searchWith(filteredSearchKey), sort);
    }

    /* ----- */

    /**
     * default: 단일 항목 조회 (entity level)
     */
    default Entity getDtlEntity(final Key key) throws Exception {
        // 의존성 주입
        Repository repository = this.getRepository();
        Optional<Entity> entityWrapper = repository.findById(key);
        return Objects.requireNonNull(entityWrapper.orElseThrow(() -> new NullPointerException("해당 정보가 존재하지 않습니다.")));
    }
    /**
     * default: 단일 항목 조회 (entity level)
     */
    default Entity getDtlEntity(final Dto dto) throws Exception {
        return this.getDtlEntity(dto.getKey());
    }

    /**
     * default: 단일 항목 조회 (dto level)
     *
     * @param key
     * @return Dto
     */
    default Dto getDtlDto(final Key key) throws Exception {
        Entity entity = this.getDtlEntity(key);
        Mapstruct mapstruct = this.getMapstruct();
        return mapstruct.toDto(entity);
    }
}
