package io.nicheblog.dreamdiary.web.repository.cmm.managtr;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import org.springframework.stereotype.Repository;

/**
 * ManagtrRepository
 * <pre>
 *  일반게시판 게시물 작업자 Repository 인터페이스
 *  ※게시물 게시물 작업자(board_post_managtr) = 게시판 수정이력자. 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 */
@Repository("managtrRepository")
public interface ManagtrRepository
        extends BaseRepository<ManagtrEntity, Integer> {

}