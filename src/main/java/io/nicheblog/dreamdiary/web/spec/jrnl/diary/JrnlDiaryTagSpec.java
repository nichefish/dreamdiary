package io.nicheblog.dreamdiary.web.spec.jrnl.diary;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDaySmpEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiarySmpEntity;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
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
public class JrnlDiaryTagSpec
        implements BaseSpec<JrnlDiaryTagEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            final Root<JrnlDiaryTagEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        query.distinct(true);
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<JrnlDiaryTagEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();

        // 태그 조인
        Join<TagEntity, JrnlDiaryContentTagEntity> JrnlDiaryTagJoin = root.join("jrnlDiaryTagList", JoinType.INNER);
        Join<JrnlDiaryContentTagEntity, JrnlDiarySmpEntity> JrnlDiaryJoin = JrnlDiaryTagJoin.join("jrnlDiary", JoinType.INNER);
        Join<JrnlDiarySmpEntity, JrnlDaySmpEntity> jrnlDayJoin = JrnlDiaryJoin.join("jrnlDay", JoinType.INNER);
        Expression<Date> effectiveDtExp = builder.coalesce(jrnlDayJoin.get("jrnlDt"), jrnlDayJoin.get("aprxmtDt"));

        predicate.add(builder.equal(JrnlDiaryTagJoin.get("refContentType"), ContentType.JRNL_DIARY.key));
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
            }
        }
        return predicate;
    }
}
