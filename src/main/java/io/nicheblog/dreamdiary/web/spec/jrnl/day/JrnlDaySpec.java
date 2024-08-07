package io.nicheblog.dreamdiary.web.spec.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseClsfSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
            final Root<JrnlDayEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder,
            final Map<String, Object> searchParamMap
    ) {
        List<Order> order = new ArrayList<>();
        String sortStr = (String) searchParamMap.get("sort");
        if (StringUtils.isNotEmpty(sortStr) && "DESC".equals(sortStr)) {
            order.add(builder.desc(builder.coalesce(root.get("jrnlDt"), root.get("aprxmtDt"))));
        } else {
            order.add(builder.asc(builder.coalesce(root.get("jrnlDt"), root.get("aprxmtDt"))));
        }
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

        // Use jrnlDt if available, otherwise aprxmtDt
        Expression<Date> jrnlDtExp = root.get("jrnlDt");
        Expression<Date> aprxmtDtExp = root.get("aprxmtDt");
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
                    // 9999 = 모든 년
                    Integer yy = (Integer) searchParamMap.get(key);
                    if (yy != 9999) predicate.add(builder.equal(root.get(key), yy));
                    continue;
                case "mnth":
                    Integer mnth = (Integer) searchParamMap.get(key);
                    if (mnth != 99) predicate.add(builder.equal(root.get(key), mnth));
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
