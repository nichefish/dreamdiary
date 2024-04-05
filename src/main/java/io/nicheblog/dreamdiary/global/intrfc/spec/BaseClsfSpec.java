package io.nicheblog.dreamdiary.global.intrfc.spec;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.notice.NoticeEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseClsfSpec
 * <pre>
 *  (공통/상속) 검색인자 세팅 Specification 인터페이스
 * </pre>
 *
 * @author nichefish
 */
public interface BaseClsfSpec<Entity extends BaseClsfEntity>
        extends BaseCrudSpec<Entity> {

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
            List<Predicate> clsfPredicate = new ArrayList<>();
            List<Predicate> predicate = new ArrayList<>();
            try {
                // basePredicte 먼저 처리 후 나머지에 대해 처리
                basePredicate = getBasePredicate(searchParamMap, root, builder);
                clsfPredicate = getClsfPredicate(searchParamMap, root, builder);
                predicate = getPredicateWithParams(searchParamMap, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            predicate.addAll(basePredicate);
            predicate.addAll(clsfPredicate);
            this.postQuery(root, query, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * default: CLSF 요소에 대해 인자별로 구체적인 검색 조건 세팅
     */
    default List<Predicate> getClsfPredicate(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        List<String> keysToRemove = new ArrayList<>();

        for (String key : searchParamMap.keySet()) {
            switch(key) {
                // 태그 모듈
                case "tags":
                    try {
                        List<Integer> refTagNoList = (List<Integer>) searchParamMap.get(key);
                        if (CollectionUtils.isEmpty(refTagNoList)) continue;
                        // 태그 검색
                        Join<NoticeEntity, ContentTagEntity> contentTag = root.join("tag").join("list", JoinType.INNER);
                        Expression<String> contentTagExp = contentTag.get("refTagNo");
                        predicate.add(contentTagExp.in(refTagNoList)); // IN 절 사용
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    keysToRemove.add(key);      // 처리된 키 저장
                    continue;
                // TODO:
            }
        }
        keysToRemove.forEach(searchParamMap::remove);       // 처리된 키를 searchParamMap에서 제거

        return predicate;
    }
}
