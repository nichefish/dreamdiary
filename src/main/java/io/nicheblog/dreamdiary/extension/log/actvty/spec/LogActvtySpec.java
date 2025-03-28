package io.nicheblog.dreamdiary.extension.log.actvty.spec;

import io.nicheblog.dreamdiary.extension.log.actvty.entity.LogActvtyEntity;
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
 * LogActvtySpec
 * <pre>
 *  활동 로그 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class LogActvtySpec
        implements BaseSpec<LogActvtyEntity> {

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
            final Root<LogActvtyEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        final List<Predicate> predicate = new ArrayList<>();
        // expressions
        final Expression<Date> logDtExp = root.get("logDt");

        // 파라미터 비교
        for (final String key : searchParamMap.keySet()) {
            final Object value = searchParamMap.get(key);
            switch (key) {
                case "searchStartDt":
                    // 기간 검색
                    predicate.add(builder.greaterThanOrEqualTo(logDtExp, DateUtils.asDate(value)));
                    continue;
                case "searchEndDt":
                    // 기간 검색
                    predicate.add(builder.lessThanOrEqualTo(logDtExp, DateUtils.asDate(value)));
                    continue;
                case "rslt":
                    // 작업결과 :: true / false 검색
                    Expression<Boolean> keyBoolExp = root.get(key);
                    predicate.add("true".equals(value) ? builder.isTrue(keyBoolExp) : builder.isFalse(keyBoolExp));
                    continue;
                default:
                    // default :: 조건 파라미터에 대해 equal 검색
                    try {
                        predicate.add(builder.equal(root.get(key), value));
                    } catch (final Exception e) {
                        log.info("unable to locate attribute '{}' while trying root.get(key).", key);
                    }
            }
        }
        return predicate;
    }
}
