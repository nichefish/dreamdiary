package io.nicheblog.dreamdiary.web.spec.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TagSpec
 * <pre>
 *  태그 목록 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
@Log4j2
public class TagSpec
        implements BaseSpec<TagEntity> {

    /**
     * 조회 후처리:: 정렬 순서 변경
     */
    @Override
    public void postQuery(
            final Root<TagEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        query.distinct(true);
    }

    /**
     * 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<TagEntity> root,
            final CriteriaBuilder builder
    ) {

        List<Predicate> predicate = new ArrayList<>();

        // 파라미터 비교
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "contentType":
                    Join<TagEntity, ContentTagEntity> contentTagJoin = root.join("contentTagList", JoinType.INNER);
                    predicate.add(builder.equal(contentTagJoin.get("refContentType"), searchParamMap.get(key)));
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
     * 연관관계 없는 태그 조회
     */
    public Specification<TagEntity> getNoRefTags() {
        return (root, query, builder) -> {
            List<Predicate> predicate = new ArrayList<>();
            try {
                Join<TagEntity, ContentTagEntity> contentTagJoin = root.join("contentTagList", JoinType.INNER);
                predicate.add(builder.isNull(contentTagJoin));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return builder.and(predicate.toArray(new Predicate[0]));
        };
    }
    
    /** 
     * 컨텐츠 타입에 맞는 태그 목록 조회 
     */
    public Specification<TagEntity> getContentSpecificTag(final String contentType) {
        Map<String, Object> searchParamMap = new HashMap<>(){{
            put("contentType", contentType);
        }};
        return this.searchWith(searchParamMap);
    }
}
