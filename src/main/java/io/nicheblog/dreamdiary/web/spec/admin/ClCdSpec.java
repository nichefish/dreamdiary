package io.nicheblog.dreamdiary.web.spec.admin;

import io.nicheblog.dreamdiary.global.cmm.cd.entity.ClCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ClCdSpec
 * <pre>
 *  분류코드 검색인자 세팅 Specification
 *  ※분류코드(cl_cd) = 상위 분류코드. 상세코드(dtl_cd)를 1:N 묶음으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component("clCdSpec")
@Log4j2
public class ClCdSpec
        implements BaseSpec<ClCdEntity> {

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<ClCdEntity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        List<Predicate> predicate = new ArrayList<>();

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                // LIKE 검색
                case "clCd":
                case "clCdNm":
                case "clCdDc":
                    Expression<String> keyExp = root.get(key);
                    predicate.add(builder.like(keyExp, "%" + searchParamMap.get(key) + "%"));
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
