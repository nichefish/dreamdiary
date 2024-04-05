package io.nicheblog.dreamdiary.global.intrfc.spec;

import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * BasePostSpec
 * <pre>
 *  (공통/상속) 게시판 검색인자 세팅 Specification 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BasePostSpec<Entity extends BasePostEntity>
        extends BaseClsfSpec<Entity> {

    /**
     * default: 검색 조건 목록 반환
     */
    @Override
    default Specification<Entity> searchWith(final Map<String, Object> searchParamMap) {
        return (root, query, builder) -> {
            List<Predicate> basePredicate = new ArrayList<>();
            List<Predicate> clsfPredicate = new ArrayList<>();
            List<Predicate> postPredicate = new ArrayList<>();
            List<Predicate> predicate = new ArrayList<>();
            try {
                // basePredicte 먼저 처리 후 나머지에 대해 처리
                basePredicate = getBasePredicate(searchParamMap, root, builder);
                clsfPredicate = getClsfPredicate(searchParamMap, root, builder);
                postPredicate = getPostPredicate(searchParamMap, root, builder);
                predicate = getPredicateWithParams(searchParamMap, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            predicate.addAll(basePredicate);
            predicate.addAll(clsfPredicate);
            predicate.addAll(postPredicate);
            this.postQuery(root, query, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * default: CLSF 요소에 대해 인자별로 구체적인 검색 조건 세팅
     */
    default List<Predicate> getPostPredicate(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        List<String> keysToRemove = new ArrayList<>();

        // expressions
        Expression<Date> regDtExp = root.get("regDt");

        for (String key : searchParamMap.keySet()) {
            switch(key) {
                case "title":
                    // 제목 = LIKE 검색
                    Expression<String> keyExp = root.get(key);
                    predicate.add(builder.like(keyExp, "%" + searchParamMap.get(key) + "%"));
                    keysToRemove.add(key);      // 처리된 키 저장
                    continue;
            }
        }
        keysToRemove.forEach(searchParamMap::remove);       // 처리된 키를 searchParamMap에서 제거

        return predicate;
    }
}
