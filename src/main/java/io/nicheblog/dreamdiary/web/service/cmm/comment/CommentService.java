package io.nicheblog.dreamdiary.web.service.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import io.nicheblog.dreamdiary.web.repository.cmm.comment.jpa.CommentRepository;
import io.nicheblog.dreamdiary.web.service.cmm.cache.EhCacheEvictService;
import io.nicheblog.dreamdiary.web.spec.cmm.comment.CommentSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * CommentService
 * <pre>
 *  게시판 댓글 서비스 모듈
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 * TODO: 좀 더 섬세하게 캐시를 다루는 방법?
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("commentService")
@RequiredArgsConstructor
@Log4j2
public class CommentService
        implements BaseMultiCrudService<CommentDto, CommentDto, Integer, CommentEntity, CommentRepository, CommentSpec, CommentMapstruct> {

    private final CommentRepository commentRepository;
    private final CommentSpec commentSpec;
    private final CommentMapstruct commentMapstruct = CommentMapstruct.INSTANCE;

    private final EhCacheEvictService ehCacheEvictService;

    @Override
    public CommentRepository getRepository() {
        return this.commentRepository;
    }
    @Override
    public CommentMapstruct getMapstruct() {
        return this.commentMapstruct;
    }
    @Override
    public CommentSpec getSpec() {
        return this.commentSpec;
    }

    /**
     * 등록 후처리 :: override
     */
    @Override
    public void postRegist(final CommentEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);
    }

    /**
     * 수정 후처리 :: override
     */
    @Override
    public void postModify(final CommentEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     */
    @Override
    public void postDelete(final CommentEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 처리
     */
    public void evictClsfCache(final CommentEntity rslt) throws Exception {
        String refContentType = rslt.getRefContentType();
        Integer refPostNo = rslt.getRefPostNo();
        ehCacheEvictService.evictClsfCache(refContentType, refPostNo);
        EhCacheUtils.clearL2Cache(CommentEntity.class);
    }
}