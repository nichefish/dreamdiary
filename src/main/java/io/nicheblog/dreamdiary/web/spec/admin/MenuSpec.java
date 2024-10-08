package io.nicheblog.dreamdiary.web.spec.admin;

import io.nicheblog.dreamdiary.global.intrfc.spec.BaseCrudSpec;
import io.nicheblog.dreamdiary.global.intrfc.spec.embed.BaseStateSpec;
import io.nicheblog.dreamdiary.web.entity.admin.MenuEntity;
import org.springframework.stereotype.Component;

/**
 * MenuSpec
 * <pre>
 *  메뉴 검색인자 세팅 Specification
 * </pre>
 *
 * @author nichefish
 * @implements BaseSpec:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Component
public class MenuSpec
        implements BaseCrudSpec<MenuEntity>,
                   BaseStateSpec<MenuEntity> {

    //
}
