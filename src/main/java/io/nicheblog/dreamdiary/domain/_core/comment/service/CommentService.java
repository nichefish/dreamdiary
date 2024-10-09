package io.nicheblog.dreamdiary.domain._core.comment.service;

import io.nicheblog.dreamdiary.domain._core.cache.service.EhCacheEvictService;
import io.nicheblog.dreamdiary.domain._core.comment.entity.CommentEntity;
import io.nicheblog.dreamdiary.domain._core.comment.mapstruct.CommentMapstruct;
import io.nicheblog.dreamdiary.domain._core.comment.model.CommentDto;
import io.nicheblog.dreamdiary.domain._core.comment.repository.jpa.CommentRepository;
import io.nicheblog.dreamdiary.domain._core.comment.spec.CommentSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.global.util.EhCacheUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CommentService
 * <pre>
 *  댓글 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("commentService")
@RequiredArgsConstructor
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
     * @param rslt - 등록된 엔티티
     * @throws Exception - 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postRegist(final CommentEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);
    }

    /**
     * 수정 후처리 :: override
     * @param rslt - 수정된 엔티티
     * @throws Exception - 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postModify(final CommentEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);
    }

    /**
     * 삭제 후처리 :: override
     * @param rslt - 삭제된 엔티티
     * @throws Exception - 처리 중 발생할 수 있는 예외
     */
    @Override
    public void postDelete(final CommentEntity rslt) throws Exception {
        // 관련 캐시 처리
        this.evictClsfCache(rslt);

        // TODO: 관련 엔티티 삭제?
    }

    /**
     * 관련 캐시 처리
     * @param rslt 캐시 처리할 엔티티
     * @throws Exception 캐시 처리 중 발생할 수 있는 예외
     */
    public void evictClsfCache(final CommentEntity rslt) throws Exception {
        String refContentType = rslt.getRefContentType();
        Integer refPostNo = rslt.getRefPostNo();
        ehCacheEvictService.evictClsfCache(refContentType, refPostNo);
        EhCacheUtils.clearL2Cache(CommentEntity.class);
    }
}