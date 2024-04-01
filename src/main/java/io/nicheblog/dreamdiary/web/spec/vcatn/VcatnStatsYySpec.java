package io.nicheblog.dreamdiary.web.spec.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.stats.VcatnStatsYyEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * VcatnStatsYySpec
 * <pre>
 *  휴가기준일자 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("vcatnStatsYySpec")
@Log4j2
public class VcatnStatsYySpec
        implements BaseSpec<VcatnStatsYyEntity> {

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<VcatnStatsYyEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        // expressions
        Expression<Date> endDtExp = root.get("endDt");
        Expression<Date> bgnDtExp = root.get("bgnDt");

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "today":
                    // "today"가 시작일자, 종료일자에 포함되는 조건 검색
                    predicate.add(builder.greaterThanOrEqualTo(endDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    predicate.add(builder.lessThanOrEqualTo(bgnDtExp, DateUtils.asDate(searchParamMap.get(key))));
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
