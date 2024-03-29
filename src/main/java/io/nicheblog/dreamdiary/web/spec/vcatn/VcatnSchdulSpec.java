package io.nicheblog.dreamdiary.web.spec.vcatn;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnPaprEntity;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * VcatnSchdulSpec
 * <pre>
 *  휴가 일정 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("vcatnSchdulSpec")
@Log4j2
public class VcatnSchdulSpec
        implements BaseSpec<VcatnSchdulEntity> {

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<VcatnSchdulEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {
        List<Predicate> predicate = new ArrayList<>();
        Join<VcatnSchdulEntity, VcatnPaprEntity> vcatnPapr = root.join("vctnPaprInfo", JoinType.LEFT);

        // expressions
        Expression<Date> endDtExp = root.get("endDt");
        Expression<Date> bgnDtExp = root.get("bgnDt");

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                // 휴가계획서 등록자 혹은 수동등록대상자
                case "userId":
                    Predicate papr = builder.equal(vcatnPapr.get("regstrId"), searchParamMap.get(key));
                    Predicate manual = builder.equal(root.get("userId"), searchParamMap.get(key));
                    predicate.add(builder.or(papr, manual));
                    continue;
                    // 휴가시작일이 검색종료일보다 이전이(거나 같)고. 휴가종료일이 검색시작일보다 이후(거나 같)인 것
                case "searchStartDt":
                    predicate.add(builder.greaterThanOrEqualTo(endDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "searchEndDt":
                    predicate.add(builder.lessThanOrEqualTo(bgnDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                    // 기타 조건 처리
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
