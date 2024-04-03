package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.entity.admin.MenuEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.MenuMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.MenuDto;
import io.nicheblog.dreamdiary.web.model.admin.MenuListDto;
import io.nicheblog.dreamdiary.web.repository.admin.MenuRepository;
import io.nicheblog.dreamdiary.web.spec.admin.MenuSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * MenuService
 * <pre>
 *  메뉴 관리 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("menuService")
@Log4j2
public class MenuService
        implements BaseCrudService<MenuDto, MenuListDto, Integer, MenuEntity, MenuRepository, MenuSpec, MenuMapstruct>,
                   BaseStateService<MenuDto, MenuListDto, Integer, MenuEntity, MenuRepository, MenuSpec, MenuMapstruct> {

    @Resource(name = "menuRepository")
    private MenuRepository menuRepository;
    @Resource(name = "menuSpec")
    private MenuSpec menuSpec;

    private final MenuMapstruct menuMapstruct = MenuMapstruct.INSTANCE;

    @Override
    public MenuRepository getRepository() {
        return this.menuRepository;
    }

    @Override
    public MenuSpec getSpec() {
        return this.menuSpec;
    }

    @Override
    public MenuMapstruct getMapstruct() {
        return this.menuMapstruct;
    }

    /**
     * 메인메뉴(사용자, 관리자, 공통 포함) 목록 조회
     */
    public Page<MenuListDto> getMainMenuList(
            final BaseSearchParam searchParam,
            final PageRequest pageRequest
    ) throws Exception {

        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        searchParamMap.put("menuTyCd", "main");
        Page<MenuEntity> menuMainEntityPage = this.getListEntity(searchParamMap, pageRequest);
        return this.pageEntityToDto(menuMainEntityPage);
    }

    /**
     * 메인메뉴(사용자) 조회
     */
    public Page<MenuListDto> getUserMenuList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("menuTyCd", "main");
            put("menuNo", "00000000");
        }};
        Sort sort = Sort.by(Sort.Direction.ASC, "state.sortOrdr");
        PageRequest pageRequest = PageRequest.of(0, 99, sort);
        Page<MenuEntity> menuMainEntityPage = this.getListEntity(searchParamMap, pageRequest);
        return this.pageEntityToDto(menuMainEntityPage);
    }

    /**
     * 메인메뉴(관리자) 조회
     */
    public Page<MenuListDto> getMngrMenuList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("menuTyCd", "main");
            put("menuNo", "00000000");
        }};
        Sort sort = Sort.by(Sort.Direction.ASC, "state.sortOrdr");
        PageRequest pageRequest = PageRequest.of(0, 99, sort);
        Page<MenuEntity> menuMainEntityPage = this.getListEntity(searchParamMap, pageRequest);
        return this.pageEntityToDto(menuMainEntityPage);
    }

}
