package io.nicheblog.dreamdiary.global.intrfc.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * CustomStreamRepository
 * <pre>
 *  Stream 조회 처리용 Custom Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("customStreamRepository")
public interface BaseStreamRepository<T, ID extends Serializable> {

    /**
     * stream 조회
     */
    Stream<T> streamAllBy(Specification<T> searchWith);

    /** 공격탐지내역 엑셀 다운로드용 Stream 조회 */
    /*@QueryHints(value=@QueryHint(name="org.hibernate.readOnly", value="true"))
    public Stream<Entity> streamAllBy(Specification<Entity> searchWith) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Entity> query = cb.createQuery(Entity.class);
        Root<Entity> root = query.from(Entity.class);
        query.where(searchWith.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getResultStream();
    }*/
}