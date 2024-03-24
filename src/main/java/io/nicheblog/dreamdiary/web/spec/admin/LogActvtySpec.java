package io.nicheblog.dreamdiary.web.spec.admin;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * LogActvtySpec
 * <pre>
 *  활동 로그 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("logActvtySpec")
@Log4j2
public class LogActvtySpec
        implements BaseSpec<LogActvtyEntity> {

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<LogActvtyEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        // expressions
        Expression<Date> logDtExp = root.get("logDt");

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "searchStartDt":
                    // 기간 검색
                    predicate.add(builder.greaterThanOrEqualTo(logDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "searchEndDt":
                    // 기간 검색
                    predicate.add(builder.lessThanOrEqualTo(logDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "nickNm":
                    // 작업자 이름 = 조인 후 LIKE 검색
                    Join<LogActvtyEntity, AuditorInfo> regstr = root.join("logUserInfo", JoinType.LEFT);      //  JOIN 타입 명시하기
                    Expression<String> nickNmExp = regstr.get(key);
                    predicate.add(builder.like(nickNmExp, "%" + searchParamMap.get(key) + "%"));
                    continue;
                case "rslt":
                    // 작업결과 :: true / false 검색
                    Expression<Boolean> keyBoolExp = root.get(key);
                    predicate.add("true".equals(searchParamMap.get(key)) ? builder.isTrue(keyBoolExp) : builder.isFalse(keyBoolExp));
                    continue;
                default:
                    // default :: 조건 파라미터에 대해 equal 검색
                    try {
                        predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
                    } catch (Exception e) {
                        log.info("unable to locate attribute " + key + " while trying root.get(key).");
                        // e.printStackTrace();
                    }
            }
        }
        return predicate;
    }
}
