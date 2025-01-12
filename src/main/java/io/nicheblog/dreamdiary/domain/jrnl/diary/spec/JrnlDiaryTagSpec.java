package io.nicheblog.dreamdiary.domain.jrnl.diary.spec;

import io.nicheblog.dreamdiary.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.jrnl.day.entity.JrnlDaySmpEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryContentTagEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiarySmpEntity;
import io.nicheblog.dreamdiary.domain.jrnl.diary.entity.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common._clsf.tag.entity.TagEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JrnlDiaryTagSpec
 * <pre>
 *  저널 일기 태그 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class JrnlDiaryTagSpec
        implements BaseSpec<JrnlDiaryTagEntity> {

    /**
     * 검색 조건 세팅 후 쿼리 후처리. (override)
     * 
     * @param root 조회할 엔티티의 Root 객체
     * @param query - CriteriaQuery 객체
     * @param builder CriteriaBuilder 객체
     */
    @Override
    public void postQuery(
            final Root<JrnlDiaryTagEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        // distinct
        query.distinct(true);
    }

    /**
     * 인자별로 구체적인 검색 조건을 세팅한다. (override)
     *
     * @param searchParamMap 검색 파라미터 맵
     * @param root 검색할 엔티티의 Root 객체
     * @param builder 검색 조건을 생성하는 CriteriaBuilder 객체
     * @return {@link List} -- 설정된 검색 조건(Predicate) 리스트
     * @throws Exception 검색 조건 생성 중 발생할 수 있는 예외
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<JrnlDiaryTagEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        final List<Predicate> predicate = new ArrayList<>();

        // 태그 조인
        final Join<TagEntity, JrnlDiaryContentTagEntity> JrnlDiaryTagJoin = root.join("jrnlDiaryTagList", JoinType.INNER);
        final Join<JrnlDiaryContentTagEntity, JrnlDiarySmpEntity> JrnlDiaryJoin = JrnlDiaryTagJoin.join("jrnlDiary", JoinType.INNER);
        final Join<JrnlDiarySmpEntity, JrnlDaySmpEntity> jrnlDayJoin = JrnlDiaryJoin.join("jrnlDay", JoinType.INNER);
        final Expression<Date> effectiveDtExp = builder.coalesce(jrnlDayJoin.get("jrnlDt"), jrnlDayJoin.get("aprxmtDt"));

        predicate.add(builder.equal(JrnlDiaryTagJoin.get("regstrId"), AuthUtils.getLgnUserIdOrDefault                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         ()));     // 등록자 ID 기준으로 조회
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
                    final Integer yy = (Integer) searchParamMap.get(key);
                    if (yy != 9999) predicate.add(builder.equal(jrnlDayJoin.get(key), yy));
                    continue;
                case "mnth":
                    // 99 = 모든 월
                    final Integer mnth = (Integer) searchParamMap.get(key);
                    if (mnth != 99) predicate.add(builder.equal(jrnlDayJoin.get(key), mnth));
            }
        }

        return predicate;
    }
}
