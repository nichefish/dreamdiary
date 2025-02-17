package io.nicheblog.dreamdiary.extension.clsf.tag.spec;

import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.extension.clsf.tag.entity.ContentTagEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ContentTagSpec
 * <pre>
 *  컨텐츠-태그 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class ContentTagSpec
        implements BaseSpec<ContentTagEntity> {

    /**
     * 검색 조건 세팅 후 쿼리 후처리. (override)
     * 
     * @param root 조회할 엔티티의 Root 객체
     * @param query - CriteriaQuery 객체
     * @param builder CriteriaBuilder 객체
     */
    @Override
    public void postQuery(
            final Root<ContentTagEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        // distinct
        query.distinct(true);
    }

    /**
     * 인자별로 구체적인 검색 조건을 세팅한다. (override)
     *
     * @param searchParamMap 검색 파라미터 맵
     * @param root 검색할 엔티티의 Root 객체
     * @param builder 검색 조건을 생성하는 CriteriaBuilder 객체
     * @return {@link List} -- 설정된 검색 조건(Predicate) 리스트
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<ContentTagEntity> root,
            final CriteriaBuilder builder
    ) {

        final List<Predicate> predicate = new ArrayList<>();
        predicate.add(builder.equal(root.get("regstrId"), AuthUtils.getLgnUserIdOrDefault()));      // 등록자 ID 기준으로 조회

        // 파라미터 비교
        for (final String key : searchParamMap.keySet()) {
            final Object value = searchParamMap.get(key);
            switch (key) {
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
