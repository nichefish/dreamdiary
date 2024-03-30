package io.nicheblog.dreamdiary.web.repository.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import org.springframework.stereotype.Repository;

/**
 * TagRepository
 * <pre>
 *  게시판 태그 정보 repository 인터페이스
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
 * </pre>
 *
 * @author nichefish
 */

@Repository("tagRepository")
public interface TagRepository
        extends BaseRepository<TagEntity, Integer> {
    //
}
