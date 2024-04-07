package io.nicheblog.dreamdiary.web.repository.admin;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.admin.MenuEntity;
import org.springframework.stereotype.Repository;

/**
 * MenuRepository
 * <pre>
 *  메뉴 Repository 인터페이스
 * </pre>
 *
 * @author nichefish
 */
@Repository("menuRepository")
public interface MenuRepository
        extends BaseStreamRepository<MenuEntity, Integer> {
    //
}