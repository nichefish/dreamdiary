package io.nicheblog.dreamdiary.web.repository.cmm.viewer;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import org.springframework.stereotype.Repository;

/**
 * BoardPostViewerRepository
 * <pre>
 *  게시판 게시물 열람자 Repository 인터페이스
 *  ※게시판 게시물 열람자(board_post_viewer) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("boardPostViewerRepository")
public interface ViewerRepository
        extends BaseRepository<ViewerEntity, Integer> {

}
