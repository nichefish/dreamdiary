package io.nicheblog.dreamdiary.global.intrfc.spec;

import io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.entity.ExptrPrsnlPaprEntity;
import io.nicheblog.dreamdiary.domain._core.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseCrudSpec
 * <pre>
 *  (공통/상속) AUDIT 요소에 대한 검색인자 세팅 Specification 인터페이스.
 * </pre>
 *
 * @author nichefish
 * @extends BaseSpec - 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
public interface BaseCrudSpec<Entity extends BaseCrudEntity>
        extends BaseSpec<Entity> {

    /**
     * default: 검색 조건 목록 반환
     * @param searchParamMap - 검색 파라미터 맵
     * @return Specification<Entity> - 검색 조건에 맞는 Specification 객체
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
            this.postQuery(root, query, builder, searchParamMap);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * default: 인자별로 구체적인 검색 조건 세팅
     * @param searchParamMap - 검색 파라미터 맵
     * @param root - 검색할 엔티티의 Root 객체
     * @param builder - 검색 조건을 생성하는 CriteriaBuilder 객체
     * @return List<Predicate> - 설정된 검색 조건(Predicate) 리스트
     */
    default List<Predicate> getBasePredicate(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) {

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
                    Join<ExptrPrsnlPaprEntity, AuditorInfo> regstrJoin = root.join("regstrInfo", JoinType.LEFT);
                    Expression<String> nickNmExp = regstrJoin.get("nickNm");
                    predicate.add(builder.like(nickNmExp, "%" + searchParamMap.get(key) + "%"));
                    keysToRemove.add(key);      // 처리된 키 저장
            }
        }
        keysToRemove.forEach(searchParamMap::remove);       // 처리된 키를 searchParamMap에서 제거

        return predicate;
    }
}
