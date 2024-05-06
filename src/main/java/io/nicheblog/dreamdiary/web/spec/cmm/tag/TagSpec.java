package io.nicheblog.dreamdiary.web.spec.cmm.tag;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * TagSpec
 * <pre>
 *  태그 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class TagSpec
        implements BaseSpec<TagEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            Root<TagEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder
    ) {
        query.distinct(true);
    }

    /**
     * default: 검색 조건 목록 반환
     */
    @Override
    public Specification<TagEntity> searchWith(final Map<String, Object> searchParamMap) {
        // filter
        searchParamMap.remove("backToList");
        searchParamMap.remove("actvtyCtgr");

        String contentType = (String) searchParamMap.get("contentType");

        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();

            try {
                // basePredicte 먼저 처리 후 나머지에 대해 처리
                predicate = getPredicateWithParams(searchParamMap, root, builder);
                // 저널 꿈 관련 처리
                if (ContentType.JRNL_DREAM.key.equals(contentType)) {
                    List<Predicate> jrnlDreamPredicate = getJrnlDreamPredicate(searchParamMap, root, builder);
                    predicate.addAll(jrnlDreamPredicate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.postQuery(root, query, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<TagEntity> root,
            final CriteriaBuilder builder
    ) {

        List<Predicate> predicate = new ArrayList<>();

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "contentType":
                    Join<TagEntity, ContentTagEntity> contentTagList = root.join("contentTagList", JoinType.INNER);
                    predicate.add(builder.equal(contentTagList.get("refContentType"), searchParamMap.get(key)));
                default:
                    // default :: 조건 파라미터에 대해 equal 검색
                    try {
                        predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
                    } catch (Exception e) {
                        log.info("unable to locate attribute " + key + " while trying root.get(key).");
                    }
            }
        }
        return predicate;
    }

    public Specification<TagEntity> getNoRefTags() {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                Join<TagEntity, ContentTagEntity> contentTagList = root.join("contentTagList", JoinType.INNER);
                predicate.add(builder.isNull(contentTagList));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }
    
    /** 
     * 컨텐츠 타입에 맞는 태그 목록 조회 
     */
    public Specification<TagEntity> getContentSpecificTag(String contentType) {
        Map<String, Object> searchParamMap = new HashMap<>(){{
            put("contentType", contentType);
        }};
        return this.searchWith(searchParamMap);
    }

    /**
     * 컨텐츠 타입에 맞는 꿈 태그 목록 조회
     */
    public List<Predicate> getJrnlDreamPredicate(
            final Map<String, Object> searchParamMap,
            final Root<TagEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        
        // 태그 조인
        Join<TagEntity, JrnlDreamTagEntity> jrnlDreamTagList = root.join("jrnlDreamTagList", JoinType.INNER);
        Join<JrnlDreamTagEntity, JrnlDreamEntity> jrnlDream = jrnlDreamTagList.join("jrnlDream", JoinType.INNER);
        Join<JrnlDreamTagEntity, JrnlDayEntity> jrnlDay = jrnlDream.join("jrnlDay", JoinType.INNER);
        Expression<Date> jrnlDtExp = jrnlDay.get("jrnlDt");
        Expression<Date> aprxmtDtExp = jrnlDay.get("aprxmtDt");
        Expression<Date> effectiveDtExp = builder.coalesce(jrnlDtExp, aprxmtDtExp);

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "searchStartDt":
                    // 기간 검색
                    predicate.add(builder.greaterThanOrEqualTo(effectiveDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "searchEndDt":
                    // 기간 검색
                    predicate.add(builder.lessThanOrEqualTo(effectiveDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "yy":
                    Expression<Integer> yearExp = builder.function("YEAR", Integer.class, effectiveDtExp);
                    predicate.add(builder.equal(yearExp, searchParamMap.get(key)));
                    continue;
                case "mnth":
                    // Month filter: Extract month from effectiveDtExp and compare with 'mnth'
                    Expression<Integer> monthExp = builder.function("MONTH", Integer.class, effectiveDtExp);
                    predicate.add(builder.equal(monthExp, searchParamMap.get(key)));
                    continue;
            }
        }
        return predicate;
    }
}
