package io.nicheblog.dreamdiary.global._common._clsf.comment.service;

import io.nicheblog.dreamdiary.global._common._clsf.comment.entity.CommentEntity;
import io.nicheblog.dreamdiary.global._common._clsf.comment.mapstruct.CommentMapstruct;
import io.nicheblog.dreamdiary.global._common._clsf.comment.model.CommentDto;
import io.nicheblog.dreamdiary.global._common._clsf.comment.repository.jpa.CommentRepository;
import io.nicheblog.dreamdiary.global._common._clsf.comment.spec.CommentSpec;
import io.nicheblog.dreamdiary.global.intrfc.service.BaseMultiCrudService;

/**
 * CommentService
 * <pre>
 *  댓글 서비스 인터페이스.
 * </pre>
 *
 * @author nichefish
 */
public interface CommentService
        extends BaseMultiCrudService<CommentDto, CommentDto, Integer, CommentEntity, CommentRepository, CommentSpec, CommentMapstruct> {

    //
}