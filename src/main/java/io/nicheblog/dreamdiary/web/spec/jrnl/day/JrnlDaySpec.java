package io.nicheblog.dreamdiary.web.spec.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseClsfSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * JrnlDaySpec
 * <pre>
 *  저널 일자 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class JrnlDaySpec
        implements BaseClsfSpec<JrnlDayEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     * 날짜 오름차순 정렬, jrnlDt 부재시 aprxmtDt 사용
     */
    @Override
    public void postQuery(
            Root<JrnlDayEntity> root,
            CriteriaQuery<?> query,
            CriteriaBuilder builder
    ) {
        List<Order> order = new ArrayList<>();
        order.add(builder.asc(builder.coalesce(root.get("jrnlDt"), root.get("aprxmtDt"))));
        query.orderBy(order);
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<JrnlDayEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();

        // expressions
        Expression<Date> regDtExp = root.get("regDt");
        // Use jrnlDt if available, otherwise aprxmtDt
        Expression<Date> jrnlDtExp = root.get("jrnlDt");
        Expression<Date> aprxmtDtExp = root.get("aprxmtDt");
        Expression<Date> effectiveDateExp = builder.coalesce(jrnlDtExp, aprxmtDtExp);

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
                    Expression<Integer> yearExp = builder.function("YEAR", Integer.class, effectiveDateExp);
                    predicate.add(builder.equal(yearExp, searchParamMap.get(key)));
                    continue;
                case "mnth":
                    // Month filter: Extract month from effectiveDateExp and compare with 'mnth'
                    Expression<Integer> monthExp = builder.function("MONTH", Integer.class, effectiveDateExp);
                    predicate.add(builder.equal(monthExp, searchParamMap.get(key)));
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
