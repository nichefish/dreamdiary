package io.nicheblog.dreamdiary.web.spec.exptr.prsnl;

import io.nicheblog.dreamdiary.global.intrfc.spec.BasePostSpec;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlPaprEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ExptrPrsnlPaprSpec
 * <pre>
 *  개인경비지출서 목록 검색인자 세팅 Specification
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class ExptrPrsnlPaprSpec
        implements BasePostSpec<ExptrPrsnlPaprEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            final Root<ExptrPrsnlPaprEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        List<Order> order = new ArrayList<>();
        order.add(builder.desc(root.get("yy")));
        order.add(builder.desc(root.get("mnth")));
        query.orderBy(order);
    }

    /**
     * 기존 등록건수 체크 목록 반환
     */
    public Specification<ExptrPrsnlPaprEntity> searchWith(
            final String userId,
            final Integer[] yyMnth
    ) {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                predicate = getExistingChck(userId, yyMnth, root, builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }

    /**
     * Parameter별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<ExptrPrsnlPaprEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();
        // expressions
        Expression<Date> regDtExp = root.get("regDt");

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
     * 이번달/이전달 체크
     */
    public List<Predicate> getExistingChck(
            final String userId,
            final Integer[] yyMnth,
            final Root<ExptrPrsnlPaprEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {
        List<Predicate> predicate = new ArrayList<>();
        predicate.add(builder.equal(root.get("regstrId"), userId));
        // 월 :: 0~11 대신 1~12이므로 그대로 검색한다.
        predicate.add(builder.and(builder.equal(root.get("yy"), yyMnth[0]), builder.equal(root.get("mnth"), yyMnth[1])));
        return predicate;
    }

}

