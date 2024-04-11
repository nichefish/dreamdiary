package io.nicheblog.dreamdiary.web.spec.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseCrudSpec;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.emplym.UserEmplymEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * UserSpec
 * <pre>
 *  사용자(계정) 정보 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class UserSpec
        implements BaseCrudSpec<UserEntity> {

    /**
     * 검색 조건 목록 반환
     * ("시스템관리자" 목록 화면에서 제외 위해 override)
     */
    @Override
    public Specification<UserEntity> searchWith(final Map<String, Object> searchParamMap) {
        // filter
        searchParamMap.remove("backToList");
        searchParamMap.remove("actvtyCtgr");

        return (root, query, builder) -> {
            List<Predicate> basePredicate = new ArrayList<>();
            List<Predicate> predicate = new ArrayList<>();
            try {
                // basePredicte 먼저 처리 후 나머지에 대해 처리
                basePredicate = getBasePredicate(searchParamMap, root, builder);
                predicate = getPredicateWithParams(searchParamMap, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            predicate.addAll(basePredicate);
            // 시스템 관리자 목록에서 제외
            predicate.add(builder.notEqual(root.get("userId"), Constant.SYSTEM_ACNT));
            this.postQuery(root, query, builder);
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * 검색 조건 목록 반환 :: preset된 특정 모드를 반환
     */
    public Specification<UserEntity> searchCrdtUser(final String startDtStr, final String endDtStr) {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                predicate = getCrdtUser(startDtStr, endDtStr, root, builder);
                List<Order> order = getOrderByTitleAndEcnyDt(root, builder);
                query.orderBy(order);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * 검색 조건 목록 반환 :: preset된 특정 모드를 반환
     */
    public Specification<UserEntity> searchCrdtBrthdyUser() {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                String startDtStr = DateUtils.getCurrDateStr(DatePtn.DATE);
                String endDtStr = DateUtils.getNextDateStr(DatePtn.DATE);
                predicate = getCrdtBrthdyUser(startDtStr, endDtStr, root, builder);
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
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<UserEntity> root,
            final CriteriaBuilder builder
    ) {

        List<Predicate> predicate = new ArrayList<>();
        Join<UserEntity, UserProflEntity> userProfl = root.join("userProfl", JoinType.LEFT);

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                // 사용자 정보 존재 "hasUserProfl" 체크시 조인결과 있는 목록만 반환
                case "hasUserProfl":
                    if ((Boolean) searchParamMap.get(key)) {
                        predicate.add(builder.isNotNull(userProfl.get("userProflNo")));
                    }
                    continue;
                    // 이름 = LIKE 검색
                case "cmpyCd":
                case "rankCd":
                case "teamCd":
                    predicate.add(builder.equal(userProfl.get(key), searchParamMap.get(key)));
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

    /**
     * 내부직원 검색조건 세팅
     */
    public List<Predicate> getCrdtUser(
            final String startDtStr,
            final String endDtStr,
            final Root<UserEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {
        List<Predicate> predicate = new ArrayList<>();
        // JOIN 조건 세팅
        Join<UserEntity, UserEmplymEntity> emplym = root.join("emplym", JoinType.INNER);
        // 2. 기간조건 :: 해당 년도 내에 근무내역이 있음 (입사일 // 퇴사일)
        // 퇴사일 :: 퇴사 안했거나 or 비교일 내에 퇴사했거나
        Date startDay = DateUtils.Parser.bfDateParse(DateUtils.asDate(startDtStr));
        Predicate notRetired = builder.isNull(emplym.get("retireDt"));
        Predicate retiredAfterFirstDay = builder.greaterThanOrEqualTo(emplym.get("retireDt"), startDay);
        predicate.add(builder.or(notRetired, retiredAfterFirstDay));
        // 입사일 :: 비교일보다 전에 입사
        Date endDay = DateUtils.Parser.bfDateParse(DateUtils.asDate(endDtStr));
        Predicate hasEcnyDt = builder.isNotNull(emplym.get("ecnyDt"));
        Predicate enteredBeforeEndDay = builder.lessThanOrEqualTo(emplym.get("ecnyDt"), endDay);
        predicate.add(builder.and(hasEcnyDt, enteredBeforeEndDay));
        return predicate;
    }

    /**
     * 오늘이 생일인 내부직원 검색조건 세팅
     */
    public List<Predicate> getCrdtBrthdyUser(
            final String startDtStr,
            final String endDtStr,
            final Root<UserEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {
        List<Predicate> predicate = getCrdtUser(startDtStr, endDtStr, root, builder);
        // JOIN 조건 세팅
        Join<UserEntity, UserEmplymEntity> emplym = root.join("emplym", JoinType.INNER);
        Join<UserEntity, UserProflEntity> profl = root.join("profl", JoinType.INNER);
        predicate.add(builder.equal(profl.get("brthdy"), DateUtils.asDate(startDtStr)));
        return predicate;
    }

    /**
     * 정렬 조건 세팅 ::
     */
    private static List<Order> getOrderByTitleAndEcnyDt(
            final Root<UserEntity> root,
            final CriteriaBuilder builder
    ) {
        List<Order> order = new ArrayList<>();
        Join<UserEntity, UserEmplymEntity> emplym = root.join("emplym", JoinType.INNER);
        Join<UserEmplymEntity, DtlCdEntity> rankCdInfo = emplym.join("rankCdInfo", JoinType.INNER);
        order.add(builder.desc(rankCdInfo.get("state").get("sortOrdr")));
        order.add(builder.asc(emplym.get("ecnyDt")));
        return order;
    }
}
