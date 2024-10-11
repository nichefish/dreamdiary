package io.nicheblog.dreamdiary.domain.board.def.service;

import io.nicheblog.dreamdiary.domain.board.def.entity.BoardDefEntity;
import io.nicheblog.dreamdiary.domain.board.def.mapstruct.BoardDefMapstruct;
import io.nicheblog.dreamdiary.domain.board.def.model.BoardDefDto;
import io.nicheblog.dreamdiary.domain.board.def.repository.jpa.BoardDefRepository;
import io.nicheblog.dreamdiary.domain.board.def.spec.BoardDefSpec;
import io.nicheblog.dreamdiary.global.SiteMenu;
import io.nicheblog.dreamdiary.domain._clsf.state.model.cmpstn.StateCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseCrudService;
import io.nicheblog.dreamdiary.global.intrfc.service.embed.BaseStateService;
import io.nicheblog.dreamdiary.global.model.SiteAcsInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * BoardDefService
 * <pre>
 *  게시판 정의 서비스 모듈.
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Service("boardDefService")
@RequiredArgsConstructor
@Log4j2
public class BoardDefService
        implements BaseCrudService<BoardDefDto, BoardDefDto, String, BoardDefEntity, BoardDefRepository, BoardDefSpec, BoardDefMapstruct>,
                   BaseStateService<BoardDefDto, BoardDefDto, String, BoardDefEntity, BoardDefRepository, BoardDefSpec, BoardDefMapstruct> {

    @Getter
    private final BoardDefRepository repository;
    @Getter
    private final BoardDefSpec spec;
    @Getter
    private final BoardDefMapstruct mapstruct = BoardDefMapstruct.INSTANCE;

    /**
     * boardDef 목록 메뉴 조회 (SiteAcsInfo 목록 반환)
     *
     * @return {@link List} -- 게시판 정의 목록을 메뉴 정보로 변환한 리스트
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public List<SiteAcsInfo> boardDefMenuList() throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("useYn", "Y");
        }};
        List<BoardDefEntity> boardDefPage = this.getListEntity(searchParamMap);
        return boardDefPage.stream()
                .map(entity -> {
                    try {
                        return mapstruct.toMenu(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * boardCd로 단일 메뉴 조회 (SiteAcsInfo 반환)
     *
     * @return {@link SiteAcsInfo} -- 게시판 정의를 메뉴 정보로 변환하여 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    public SiteAcsInfo getBoardMenu(final String boardCd) throws Exception {
        BoardDefEntity boardDef = this.getDtlEntity(boardCd);
        return mapstruct.toMenu(boardDef);
    }

    /**
     * 등록 전처리. (override)
     *
     * @param dto 등록할 객체
     */
    @Override
    public void preRegist(final BoardDefDto dto) {
        if (dto.getState() == null) dto.setState(new StateCmpstn());
    }

    /**
     * 상태 변경 후처리. (override)
     */
    @Override
    public void postSetState() throws Exception {
        SiteMenu.BOARD.setSubMenuList(this.boardDefMenuList());
    }

    /**
     * 정렬 순서 업데이트.
     *
     * @param sortOrdr 키 + 정렬 순서로 이루어진 목록
     * @return {@link Boolean} -- 성공시 true 반환
     * @throws Exception 처리 중 발생할 수 있는 예외
     */
    @Transactional
    public boolean sortOrdr(final List<BoardDefDto> sortOrdr) throws Exception {
        if (CollectionUtils.isEmpty(sortOrdr)) return true;
        sortOrdr.forEach(dto -> {
            try {
                BoardDefEntity e = this.getDtlEntity(dto.getBoardCd());
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