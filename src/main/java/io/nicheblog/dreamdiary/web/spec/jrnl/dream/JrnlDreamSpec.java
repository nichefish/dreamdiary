package io.nicheblog.dreamdiary.web.spec.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.spec.BasePostSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JrnlDreamSpec
 * <pre>
 *  저널 꿈 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 * @implements BasePostSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class JrnlDreamSpec
        implements BasePostSpec<JrnlDreamEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            Root<JrnlDreamEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder
    ) {
        query.distinct(true);
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<JrnlDreamEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();

        // expressions
        Expression<Date> regDtExp = root.get("regDt");

        // Use jrnlDt if available, otherwise aprxmtDt
        Join<JrnlDreamEntity, JrnlDayEntity> jrnlDay = root.join("jrnlDay", JoinType.INNER);
        Expression<Date> jrnlDtExp = jrnlDay.get("jrnlDt");
        Expression<Date> aprxmtDtExp = jrnlDay.get("aprxmtDt");
        Expression<Date> effectiveDtExp = builder.coalesce(jrnlDtExp, aprxmtDtExp);

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "searchStartDt":
                    // 기간 검색
                    predicate.add(builder.greaterThanOrEqualTo(regDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "searchEndDt":
                    // 기간 검색
                    predicate.add(builder.lessThanOrEqualTo(regDtExp, DateUtils.asDate(searchParamMap.get(key))));
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
                case "tagNo":
                    // 특정 태그된 꿈만 조회
                    Join<JrnlDreamEntity, TagEmbed> tagJoin = root.join("tag");
                    Join<TagEmbed, ContentTagEntity> contentTagJoin = tagJoin.join("list", JoinType.INNER);
                    Expression<Integer> refTagNoExp = contentTagJoin.get("refTagNo");
                    predicate.add(builder.equal(refTagNoExp, searchParamMap.get(key)));
                    continue;
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
}
