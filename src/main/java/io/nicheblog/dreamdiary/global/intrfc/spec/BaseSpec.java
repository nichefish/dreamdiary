package io.nicheblog.dreamdiary.global.intrfc.spec;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseSpec
 * <pre>
 *  (공통/상속) 검색인자 세팅 Specification 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseSpec<Entity> {

    /**
     * default: 검색 조건 목록 반환
     */
    default Specification<Entity> searchWith(final Map<String, Object> searchParamMap) {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                predicate = getPredicateWithParams(searchParamMap, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.postQuery(root, query, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * default: 인자별로 구체적인 검색 조건 세팅
     */
    default List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        for (String key : searchParamMap.keySet()) {
            // default :: 조건 파라미터에 대해 equal 검색
            try {
                predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return predicate;
    }

    /**
     * default: 조회 후처리 (정렬 순서 변경, distinct 등)
     */
    default void postQuery(
            Root<Entity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder
    ) {
        // 정렬 처리:: 기본 공백, 필요시 각 함수에서 Override
    }
}
