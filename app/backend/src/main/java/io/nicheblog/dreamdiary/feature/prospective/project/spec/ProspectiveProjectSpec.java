package io.nicheblog.dreamdiary.feature.prospective.project.spec;

import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseSpec;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Prospective Project 목록 검색 Specification. */
@Component
public class ProspectiveProjectSpec extends BaseSpec<ProspectiveProjectEntity> {

    @Override
    public void postQuery(final Root<ProspectiveProjectEntity> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        query.orderBy(builder.asc(root.get("sortOrder")), builder.asc(root.get("id")));
    }

    @Override
    public List<Predicate> getPredicateWithParams(
            final Map<String, Object> searchParamMap,
            final Root<ProspectiveProjectEntity> root,
            final CriteriaQuery<?> query,
            final CriteriaBuilder builder
    ) {
        final List<Predicate> predicateList = new ArrayList<>();
        searchParamMap.forEach((key, value) -> predicateList.add(builder.equal(root.get(key), value)));
        return predicateList;
    }
}
