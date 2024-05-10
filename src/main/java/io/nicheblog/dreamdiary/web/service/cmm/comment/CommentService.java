package io.nicheblog.dreamdiary.web.service.cmm.comment;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import io.nicheblog.dreamdiary.web.repository.cmm.comment.CommentRepository;
import io.nicheblog.dreamdiary.web.spec.cmm.comment.CommentSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * CommentService
 * <pre>
 *  게시판 댓글 서비스 모듈
 *  ※게시판 댓글(board_comment) = 게시판 게시물(board_post)에 1:N으로 귀속된다.
 * </pre>
 *
 * @author nichefish
 * @implements BaseMultiCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("commentService")
@Log4j2
public class CommentService
        implements BaseMultiCrudService<CommentDto.DTL, CommentDto.LIST, Integer, CommentEntity, CommentRepository, CommentSpec, CommentMapstruct> {

    private final CommentMapstruct commentMapstruct = CommentMapstruct.INSTANCE;

    @Resource(name = "commentRepository")
    private CommentRepository commentRepository;
    @Resource(name = "commentSpec")
    private CommentSpec commentSpec;

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

    @Override
    @CacheEvict(value={"jrnlDayList", "imprtcDreamList"}, allEntries = true)
    public void postRegist(final CommentEntity rslt) throws Exception {
        //
    }

    @Override
    @CacheEvict(value={"jrnlDayList", "imprtcDreamList"}, allEntries = true)
    public void postModify(final CommentEntity rslt) throws Exception {
        //
    }
    @Override
    @CacheEvict(value={"jrnlDayList", "imprtcDreamList"}, allEntries = true)
    public void postDelete(final CommentEntity rslt) throws Exception {
        //
    }
}