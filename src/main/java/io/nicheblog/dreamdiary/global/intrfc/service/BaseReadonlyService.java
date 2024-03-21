package io.nicheblog.dreamdiary.global.intrfc.service;

import io.nicheblog.dreamdiary.global.intrfc.mapstruct.BaseListMapstruct;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.CmmUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * BaseReadonlyService
 * <pre>
 *  (공통/상속) 조회 전용 서비스 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseReadonlyService<Dto extends BaseCrudDto, ListDto extends BaseCrudDto, Key extends Serializable, Entity, Repository extends BaseRepository<Entity, Key>, Spec extends BaseSpec<Entity>, Mapstruct extends BaseListMapstruct<Dto, ListDto, Entity>> {

    // Resource : repository
    Repository getRepository();

    // Resource : spec
    Spec getSpec();

    // Resource : mapstruct
    Mapstruct getMapstruct();

    /**
     * default: 항목 목록 조회 (entity level)
     */
    default Page<Entity> getListEntity(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        Repository repository = this.getRepository();
        Spec spec = this.getSpec();

        return repository.findAll(spec.searchWith(searchParamMap), pageable);
    }

    /**
     * default: 항목 목록 Page<Entity>->Page<Dto> 변환
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

    /**
     * default: 항목 목록 조회 (dto level)
     */
    default Page<ListDto> getListDto(
            final Object searchParam,
            final Pageable pageable
    ) throws Exception {
        Map<String, Object> searchParamMap = CmmUtils.convertParamToMap(searchParam);
        return this.getListDto(searchParamMap, pageable);
    }

    /**
     * default: 항목 목록 조회 (dto level)
     */
    default Page<ListDto> getListDto(
            final Map<String, Object> searchParamMap,
            final Pageable pageable
    ) throws Exception {
        // searchParamMap에서 빈 값들 및 쓸모없는 값들 정리
        Map<String, Object> filteredSearchKey = CmmUtils.filterParamMap(searchParamMap);

        return this.pageEntityToDto(this.getListEntity(filteredSearchKey, pageable));
    }

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
