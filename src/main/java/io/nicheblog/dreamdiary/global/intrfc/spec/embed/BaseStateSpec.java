package io.nicheblog.dreamdiary.global.intrfc.spec.embed;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbedModule;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseStateSpec
 * <pre>
 *  (공통/상속) 검색인자 세팅 Specification 인터페이스
 *  StateEmbed 구현시 처리 로직
 * </pre>
 *
 * @author nichefish
 */
public interface BaseStateSpec<Entity extends BaseAuditEntity & StateEmbedModule>
        extends BaseSpec<Entity> {

    /**
     * default: 인자별로 구체적인 검색 조건 세팅
     */
    @Override
    default List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<Entity> root,
            final CriteriaBuilder builder
    ) throws Exception {

        // 클래스 타입 검사
        // Class<?> entityClass = root.getModel().getJavaType();
        // boolean implementsInterface = StateEmbedModule.class.isAssignableFrom(entityClass);

        List<Predicate> predicate = new ArrayList<>();
        for (String key : searchParamMap.keySet()) {
            switch (key) {
                case "useYn":
                case "sortOrdr":
                    predicate.add(builder.equal(root.get("state").get(key), searchParamMap.get(key)));
                    continue;
                default:
                // default :: 조건 파라미터에 대해 equal 검색
                    try {
                        predicate.add(builder.equal(root.get(key), searchParamMap.get(key)));
                    } catch (Exception e) {
                        // log.info("unable to locate attribute " + key + " while trying root.get(key).");
                    }
            }
        }
        return predicate;
    }
}
