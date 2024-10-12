package io.nicheblog.dreamdiary.domain.admin.tmplat.repository.jpa;

import io.nicheblog.dreamdiary.domain.admin.tmplat.entity.TmplatDefEntity;
import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import org.springframework.stereotype.Repository;

/**
 * TmplatDefRepository
 * <pre>
 *  입력내용 템플릿 관리 (JPA) Repository 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
@Repository("tmplatDefRepository")
public interface TmplatDefRepository
        extends BaseStreamRepository<TmplatDefEntity, Integer> {
    //
}
