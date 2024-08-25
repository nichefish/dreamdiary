package io.nicheblog.dreamdiary.web.repository.admin.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.admin.TmplatDefEntity;
import org.springframework.stereotype.Repository;

/**
 * TmplatDefRepository
 * <pre>
 *  입력내용 템플릿 관리 Repository
 * </pre>
 *
 * @author nichefish
 */
@Repository("tmplatDefRepository")
public interface TmplatDefRepository
        extends BaseStreamRepository<TmplatDefEntity, Integer> {

    //
}
