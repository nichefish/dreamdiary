package io.nicheblog.dreamdiary.global.intrfc.spec;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseCrudSpec
 * <pre>
 *  (공통/상속) AUDIT 요소에 대한 검색인자 세팅 Specification 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseCrudSpec<Entity extends BaseCrudEntity>
        extends BaseSpec<Entity> {

    /**
     * default: 검색 조건 목록 반환
     */
    @Override
    default Specification<Entity> searchWith(final Map<String, Object> searchParamMap) {
        // filter
        searchParamMap.remove("backToList");
        searchParamMap.remove("actvtyCtgr");

        return (root, query, builder) -> {
            List<Predicate> basePredicate = new ArrayList<>();
            List<Predicate> predicate = new ArrayList<>();
            try {
                // basePredicte 먼저 처리 후 나머지에 대해 처리
                basePredicate = getBasePredicate(searchParamMap, root, builder);
                predicate = getPredicateWithParams(searchParamMap, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            predicate.addAll(basePredicate);
            this.postQuery(root, query, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * default: 인자별로 구체적인 검색 조건 세팅
     */
    default List<Predicate> getBasePredicate(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        List<String> keysToRemove = new ArrayList<>();

        for (String key : searchParamMap.keySet()) {
            switch(key) {
                case "regstrId":
                    predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
                    keysToRemove.add(key);      // 처리된 키 저장
                    continue;
                case "nickNm":
                    // 작성자 이름 = 조인 후 LIKE 검색
                    Join<ExptrPrsnlPaprEntity, AuditorInfo> regstr = root.join("regstrInfo", JoinType.LEFT);
                    Expression<String> nickNmExp = regstr.get("nickNm");
                    predicate.add(builder.like(nickNmExp, "%" + searchParamMap.get(key) + "%"));
                    keysToRemove.add(key);      // 처리된 키 저장
                    continue;
            }
        }
        keysToRemove.forEach(searchParamMap::remove);       // 처리된 키를 searchParamMap에서 제거

        return predicate;
    }
}
