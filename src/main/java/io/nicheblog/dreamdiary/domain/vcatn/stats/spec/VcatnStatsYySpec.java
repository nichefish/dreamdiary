package io.nicheblog.dreamdiary.domain.vcatn.stats.spec;

import io.nicheblog.dreamdiary.domain.vcatn.stats.entity.VcatnStatsYyEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
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
 *  휴가기준일자 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class VcatnStatsYySpec
        implements BaseSpec<VcatnStatsYyEntity> {

    /**
     * 인자별로 구체적인 검색 조건을 세팅한다. (override)
     *
     * @param searchParamMap 검색 파라미터 맵
     * @param root 검색할 엔티티의 Root 객체
     * @param builder 검색 조건을 생성하는 CriteriaBuilder 객체
     * @return {@link List} -- 설정된 검색 조건(Predicate) 리스트
     * @throws Exception 검색 조건 생성 중 발생할 수 있는 예외
     */
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<VcatnStatsYyEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        final List<Predicate> predicate = new ArrayList<>();
        // expressions
        final Expression<Date> endDtExp = root.get("endDt");
        final Expression<Date> bgnDtExp = root.get("bgnDt");

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
