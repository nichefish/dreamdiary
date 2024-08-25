package io.nicheblog.dreamdiary.web.repository.cmm.tag.jpa;

import io.nicheblog.dreamdiary.global.intrfc.repository.BaseStreamRepository;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TagRepository
 * <pre>
 *  게시판 태그 정보 repository 인터페이스
 *  ※게시판 태그(board_tag) = 게시판별 태그 정보. 일반게시판 게시물 태그(board_post_tag)와 1:N으로 연관된다.
 * </pre>
 *
 * @author nichefish
 */

@Repository("tagRepository")
public interface TagRepository
        extends BaseStreamRepository<TagEntity, Integer> {

    /**
     * 태그명으로 테이블 조회
     */
    Optional<TagEntity> findByTagNm(String tagNo);
    
    /**
     * 태그명 + 카테고리명으로 테이블 조회
     */
    Optional<TagEntity> findByTagNmAndCtgr(String tagNm, String ctgr);
}
