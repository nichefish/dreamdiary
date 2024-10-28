package io.nicheblog.dreamdiary.domain.admin.tmplat.spec;

import io.nicheblog.dreamdiary.domain.admin.tmplat.entity.TmplatDefEntity;
import io.nicheblog.dreamdiary.global.intrfc.spec.BaseCrudSpec;
import io.nicheblog.dreamdiary.global.intrfc.spec.embed.BaseStateSpec;
import org.springframework.stereotype.Component;

/**
 * TmplatDefSpec
 * <pre>
 *  템플릿(텍스트에디터) 정의 정보 목록 검색인자 세팅 Specification.
 * </pre>
 *
 * @author nichefish
 */
@Component
public class TmplatDefSpec
        implements BaseCrudSpec<TmplatDefEntity>,
                   BaseStateSpec<TmplatDefEntity> {
    //
}
