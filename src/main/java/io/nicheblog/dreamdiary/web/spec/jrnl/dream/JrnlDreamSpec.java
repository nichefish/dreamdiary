package io.nicheblog.dreamdiary.web.spec.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.global.intrfc.spec.BasePostSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDaySmpEntity;
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
            final Root<JrnlDreamEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        List<Order> order = new ArrayList<>();
        Join<JrnlDreamEntity, JrnlDaySmpEntity> jrnlDayJoin = root.join("jrnlDay", JoinType.INNER);
        order.add(builder.desc(builder.coalesce(jrnlDayJoin.get("jrnlDt"), jrnlDayJoin.get("aprxmtDt"))));
        query.orderBy(order);
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
        Join<JrnlDreamEntity, JrnlDaySmpEntity> jrnlDayJoin = root.join("jrnlDay", JoinType.INNER);
        Expression<Date> effectiveDtExp = builder.coalesce(jrnlDayJoin.get("jrnlDt"), jrnlDayJoin.get("aprxmtDt"));

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
                    // 9999 = 모든 년
                    Integer yy = (Integer) searchParamMap.get(key);
                    if (yy != 9999) predicate.add(builder.equal(jrnlDayJoin.get(key), yy));
                    continue;
                case "mnth":
                    // 99 = 모든 월
                    Integer mnth = (Integer) searchParamMap.get(key);
                    if (mnth != 99) predicate.add(builder.equal(jrnlDayJoin.get(key), mnth));
                    continue;
                case "dreamKeyword":
                    // 내용 like 검색
                    predicate.add(builder.like(root.get("cn"), "%" + searchParamMap.get(key) + "%"));
                    continue;
                case "tagNo":
                    // 특정 태그된 꿈만 조회
                    Join<JrnlDreamEntity, TagEmbed> tagJoin = root.join("tag", JoinType.INNER);
                    Join<TagEmbed, ContentTagEntity> contentTagJoin = tagJoin.join("list", JoinType.INNER);
                    predicate.add(builder.equal(contentTagJoin.get("refTagNo"), searchParamMap.get(key)));
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
