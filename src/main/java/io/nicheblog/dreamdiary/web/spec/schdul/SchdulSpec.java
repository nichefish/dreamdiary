package io.nicheblog.dreamdiary.web.spec.schdul;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.spec.BasePostSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulEntity;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulPrtcpntEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SchdulSpec
 * <pre>
 *  일정 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class SchdulSpec
        implements BasePostSpec<SchdulEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            final Root<SchdulEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        List<Order> order = new ArrayList<>();
        order.add(builder.desc(root.get("bgnDt")));
        query.orderBy(order);
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<SchdulEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        Join<SchdulEntity, SchdulPrtcpntEntity> prtcpntJoin;
        // expressions
        Expression<Date> endDtExp = root.get("endDt");
        Expression<Date> bgnDtExp = root.get("bgnDt");
        Expression<String> prvtYnExp = root.get("prvtYn");
        Expression<String> schdulCdExp = root.get("schdulCd");

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "searchStartDt":
                    // 기간 검색
                    predicate.add(builder.greaterThanOrEqualTo(endDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "searchEndDt":
                    // 기간 검색
                    predicate.add(builder.lessThanOrEqualTo(bgnDtExp, DateUtils.asDate(searchParamMap.get(key))));
                    continue;
                case "getHldyCeremonyOnly":
                    // 휴일/공휴일, 행사 조회
                    predicate.add(builder.equal(prvtYnExp, "N"));
                    Predicate hldy = builder.equal(schdulCdExp, Constant.SCHDUL_HLDY);
                    Predicate ceremony = builder.equal(schdulCdExp, Constant.SCHDUL_CEREMONY);
                    predicate.add(builder.or(hldy, ceremony));
                    continue;
                case "getExceptHldyCeremony":
                    // 휴일/공휴일, 행사 제외하고 조회
                    predicate.add(builder.equal(prvtYnExp, "N"));
                    Predicate notHldy = builder.notEqual(schdulCdExp, Constant.SCHDUL_HLDY);
                    Predicate notCeremony = builder.notEqual(schdulCdExp, Constant.SCHDUL_CEREMONY);
                    predicate.add(builder.and(notHldy, notCeremony));
                    continue;
                case "getPrvtOnly":
                    // 개인 일정 조회
                    predicate.add(builder.equal(prvtYnExp, "Y"));
                    prtcpntJoin = root.join("prtcpntList", JoinType.INNER);
                    predicate.add(builder.equal(prtcpntJoin.get("userId"), AuthUtils.getLgnUserId()));
                    continue;
                case "indtChked":
                    // 내근 조회
                    if ("N".equals(searchParamMap.get(key))) {
                        predicate.add(builder.notEqual(schdulCdExp, Constant.SCHDUL_INDT));
                    }
                    continue;
                case "outdtChked":
                    // 외근 조회
                    if ("N".equals(searchParamMap.get(key))) {
                        predicate.add(builder.notEqual(schdulCdExp, Constant.SCHDUL_OUTDT));
                    }
                    continue;
                case "tlcmmtChked":
                    // 재택근무 조회
                    if ("N".equals(searchParamMap.get(key))) {
                        predicate.add(builder.notEqual(schdulCdExp, Constant.SCHDUL_TLCMMT));
                    }
                    continue;
                // case "myPaprChked":
                //     // 내가 속한 일정 조회
                //     if ("Y".equals(searchParamMap.get(key))) {
                //         prtcpntJoin = root.join("prtcpntList", JoinType.INNER);
                //         predicate.add(builder.equal(prtcpntJoin.get("userId"), AuthUtils.getLgnUserId()));
                //     }
                //     continue;
                case "searchKeyword":
                    // 입력 키워드 검색
                    String keyword = (String) searchParamMap.get(key);
                    Predicate schdulNm = builder.like(root.get("title"), "%" + keyword + "%");
                    Predicate schdulResn = builder.like(root.get("cn"), "%" + keyword + "%");
                    predicate.add(builder.or(schdulNm, schdulResn));
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
