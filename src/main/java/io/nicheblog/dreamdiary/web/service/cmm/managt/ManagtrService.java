/*
package io.nicheblog.dreamdiary.web.service.board;

import lombok.extern.log4j.Log4j2;
import dreamdiary.nicheblog.io.cmm.util.BaseAuthUtils;
import dreamdiary.nicheblog.io.web.entity.BasePostKey;
import dreamdiary.nicheblog.io.web.entity.board.BoardPostManagtrEntity;
import dreamdiary.nicheblog.io.web.mapstruct.board.BoardPostManagtrMapstruct;
import dreamdiary.nicheblog.io.web.model.board.BoardPostManagtrDto;
import dreamdiary.nicheblog.io.web.repository.board.BoardPostManagtrRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

*/
/**
 * BoardPostManagtrService
 * <pre>
 *  게시판 게시물 작업자 서비스 모듈
 *  게시물 게시물 작업자(board_post_managtr) = 게시판 수정이력자. 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 *//*

@Service("boardPostManagtrService")
@Log4j2
public class BoardPostManagtrService {

    private final BoardPostManagtrMapstruct postManagtrMapstruct = BoardPostManagtrMapstruct.INSTANCE;

    @Resource(name = "boardPostManagtrRepository")
    private BoardPostManagtrRepository boardPostManagtrRepository;

    */
/**
     * 게시물 열람자 존재여부 체크
     *//*

    public Boolean hasAlreadyManagt(final List<BoardPostManagtrDto> managtrList) {
        if (CollectionUtils.isEmpty(managtrList)) return false;
        String lgnUserId = BaseAuthUtils.getLgnUserId();
        for (BoardPostManagtrDto managtr : managtrList) {
            if (lgnUserId.equals(managtr.getRegstrId())) return true;
        }
        return false;
    }

    */
/**
     * 게시물 열람자 등록
     *//*

    public BoardPostManagtrDto regPostManagtr(final BasePostKey key) throws Exception {
        // Dto -> Entity
        BoardPostManagtrEntity rsltEntity = boardPostManagtrRepository.save(new BoardPostManagtrEntity(key));
        boardPostManagtrRepository.refresh(rsltEntity);
        return postManagtrMapstruct.toDto(rsltEntity);
    }
}*/
