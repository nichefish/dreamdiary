package io.nicheblog.dreamdiary.web.spec.jrnl.dream;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.dream.JrnlDreamTagEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
public class JrnlDreamTagSpec
        implements BaseSpec<JrnlDreamTagEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            Root<JrnlDreamTagEntity> root,
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
            final Root<JrnlDreamTagEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();

        // 태그 조인
        Join<TagEntity, JrnlDreamContentTagEntity> jrnlDreamTagJoin = root.join("jrnlDreamTagList", JoinType.INNER);
        Join<JrnlDreamContentTagEntity, JrnlDreamEntity> jrnlDreamJoin = jrnlDreamTagJoin.join("jrnlDream", JoinType.INNER);
        Join<JrnlDreamContentTagEntity, JrnlDayEntity> jrnlDayJoin = jrnlDreamJoin.join("jrnlDay", JoinType.INNER);
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
                    predicate.add(builder.equal(jrnlDayJoin.get(key), searchParamMap.get(key)));
                    continue;
                case "mnth":
                    // 99 = 모든 월
                    Integer mnth = (Integer) searchParamMap.get(key);
                    if (mnth != 99) {
                        predicate.add(builder.equal(jrnlDayJoin.get(key), mnth));
                    }
            }
        }
        return predicate;
    }
}
