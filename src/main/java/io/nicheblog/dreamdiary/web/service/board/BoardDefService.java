package io.nicheblog.dreamdiary.web.service.board;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseManageService;
import io.nicheblog.dreamdiary.web.entity.board.BoardDefEntity;
import io.nicheblog.dreamdiary.web.mapstruct.board.BoardDefMapstruct;
import io.nicheblog.dreamdiary.web.model.board.BoardDefDto;
import io.nicheblog.dreamdiary.web.repository.board.BoardDefRepository;
import io.nicheblog.dreamdiary.web.spec.board.BoardDefSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * BoardDefService
 * <pre>
 *  게시판 정의 서비스 모듈
 *  ※게시판 정의(board_def) = 게시판 분류. 게시판 게시물(board_post)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseManageService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("boardDefService")
@Log4j2
public class BoardDefService
        implements BaseManageService<BoardDefDto, BoardDefDto, String, BoardDefEntity, BoardDefRepository, BoardDefSpec, BoardDefMapstruct> {

    private final BoardDefMapstruct boardDefMapstruct = BoardDefMapstruct.INSTANCE;

    @Resource(name = "boardDefRepository")
    private BoardDefRepository boardDefRepository;
    @Resource(name = "boardDefSpec")
    private BoardDefSpec boardDefSpec;

    @Override
    public BoardDefRepository getRepository() {
        return this.boardDefRepository;
    }

    @Override
    public BoardDefSpec getSpec() {
        return this.boardDefSpec;
    }

    @Override
    public BoardDefMapstruct getMapstruct() {
        return this.boardDefMapstruct;
    }

}