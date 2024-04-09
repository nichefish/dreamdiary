package io.nicheblog.dreamdiary.web.service.admin;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.global.util.cmm.CmmUtils;
import io.nicheblog.dreamdiary.web.entity.admin.MenuEntity;
import io.nicheblog.dreamdiary.web.mapstruct.admin.MenuMapstruct;
import io.nicheblog.dreamdiary.web.model.admin.MenuDto;
import io.nicheblog.dreamdiary.web.repository.admin.MenuRepository;
import io.nicheblog.dreamdiary.web.spec.admin.MenuSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
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
        implements BaseCrudService<MenuDto, MenuDto, Integer, MenuEntity, MenuRepository, MenuSpec, MenuMapstruct>,
                   BaseStateService<MenuDto, MenuDto, Integer, MenuEntity, MenuRepository, MenuSpec, MenuMapstruct> {

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
    public Page<MenuDto> getMainMenuList(
            final BaseSearchParam searchParam,
            final PageRequest pageRequest
    ) throws Exception {

        Map<String, Object> searchParamMap = CmmUtils.convertToMap(searchParam);
        searchParamMap.put("menuTyCd", Constant.MENU_TY_MAIN);
        return this.getPageDto(searchParamMap, pageRequest);
    }

    /**
     * 메인메뉴(사용자) 조회
     */
    public Page<MenuDto> getUserMenuList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("menuTyCd", "main");
            put("menuNo", "00000000");
        }};
        Sort sort = Sort.by(Sort.Direction.ASC, "state.sortOrdr");
        PageRequest pageRequest = PageRequest.of(0, 99, sort);
        Page<MenuEntity> menuMainEntityPage = this.getPageEntity(searchParamMap, pageRequest);
        return this.pageEntityToDto(menuMainEntityPage);
    }

    /**
     * 메인메뉴(관리자) 조회
     */
    public Page<MenuDto> getMngrMenuList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("menuTyCd", "main");
            put("menuNo", "00000000");
        }};
        Sort sort = Sort.by(Sort.Direction.ASC, "state.sortOrdr");
        PageRequest pageRequest = PageRequest.of(0, 99, sort);
        Page<MenuEntity> menuMainEntityPage = this.getPageEntity(searchParamMap, pageRequest);
        return this.pageEntityToDto(menuMainEntityPage);
    }

    /**
     * 정렬 순서 업데이트
     */
    @Transactional
    public boolean sortOrdr(List<MenuDto> sortOrdr) throws Exception {
        if (CollectionUtils.isEmpty(sortOrdr)) return true;
        sortOrdr.forEach(dto -> {
            try {
                MenuEntity e = this.getDtlEntity(dto.getMenuNo());
                e.getState().setSortOrdr(dto.getState().getSortOrdr());
                this.updt(e);
            } catch (Exception ex) {
                ex.getStackTrace();
                // 로그 기록, 예외 처리 등
                throw new RuntimeException(ex);
            }
        });
        return true;
    }
}
