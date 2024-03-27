package io.nicheblog.dreamdiary.web.spec.user;

import io.nicheblog.dreamdiary.global.auth.entity.AuditorInfo;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.global.util.DateParser;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * UserProflSpec
 * <pre>
 *  사용자 프로필 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("userProflSpec")
@Log4j2
public class UserProflSpec
        implements BaseSpec<UserProflEntity> {

    /**
     * 검색 조건 목록 반환 :: preset된 특정 모드를 반환
     */
    public Specification<UserProflEntity> searchWith(
            final String searchMode,
            final String yyStr
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                predicate = getCrdtUser(searchMode, yyStr, root, builder);
                List<Order> order = getOrderByTitleAndEcnyDt(root, builder);
                query.orderBy(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<UserProflEntity> root,
            final CriteriaBuilder builder
    ) {

        List<Predicate> predicate = new ArrayList<>();

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                // 작성자 이름 = 조인 후 LIKE 검색
                case "nickNm":
                    Join<UserProflEntity, AuditorInfo> regstr = root.join("regstrInfo", JoinType.LEFT);      //  JOIN 타입 명시하기\
                    Expression<String> nickNmExp = regstr.get(key);
                    predicate.add(builder.like(nickNmExp, "%" + searchParamMap.get(key) + "%"));
                    continue;
                    // 기타 조건 처리
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

    /**
     * 내부직원 검색조건 세팅
     */
    public List<Predicate> getCrdtUser(
            final String searchMode,
            final String yyStr,
            final Root<UserProflEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {
        List<Predicate> predicate = new ArrayList<>();
        if (StringUtils.isEmpty(searchMode)) return predicate;
        if ("crdtUser".equals(searchMode)) {
            Expression<String> cmpyCdExp = root.get("cmpyCd");
            Expression<Date> retireDtExp = root.get("retireDt");
            // 2. 기간조건 :: 해당 년도 내에 근무내역이 있음 (입사일은 당연히 있다고 간주, 퇴사일만 체크)
            Date firstDay = DateParser.bfDateParse(DateUtils.asDate(yyStr + "0101"));
            Predicate notRetired = builder.isNull(retireDtExp);
            Predicate retiredAfterFirstDay = builder.greaterThanOrEqualTo(retireDtExp, firstDay);
            predicate.add(builder.or(notRetired, retiredAfterFirstDay));
            return predicate;
        }
        return predicate;
    }

    /**
     * 정렬 조건 세팅 ::
     */
    private static List<Order> getOrderByTitleAndEcnyDt(
            final Root<UserProflEntity> root,
            final CriteriaBuilder builder
    ) {
        List<Order> order = new ArrayList<>();
        Join<UserProflEntity, DtlCdEntity> jobTitleCd = root.join("jobTitleCdInfo", JoinType.LEFT);      //  JOIN 타입 명시하기
        order.add(builder.desc(jobTitleCd.get("sortOrdr")));
        order.add(builder.asc(root.get("ecnyDt")));
        return order;
    }
}
