package io.nicheblog.dreamdiary.global.intrfc.entity;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.web.entity.cmm.comment.CommentEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.comment.CommentMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BaseClsfEntity
 * <pre>
 *  (공통/상속) 태그/댓글 속성 Entity
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @implements BaseAtchEntity
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
public class BaseClsfEntity
        extends BaseAtchEntity {

    /** 필수: 게시물 코드 */
    protected static final String CONTENT_TYPE = ContentType.DEFAULT.name();

    /**
     * 글 번호 (POST_NO, PK)
     * !상속받은 클래스에서 실제 매핑 구성 (auto_increment 또는 테이블 생성 전략(for 복합키))
     */
    @Transient
    protected Integer postNo;

    /**
     * 게시판 분류 코드
     * !상속받은 클래스에서 실제 매핑 구성 (@Column(name="content_type")
     */
    @Transient
    protected String contentType;

    /**
     * 댓글 목록
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_content_type", referencedColumnName = "content_type", insertable = false, updatable = false)),
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("댓글 목록")
    protected List<CommentEntity> commentList;

    /* ----- */

    /**
     * 복합키 객체 반환
     */
    protected BaseClsfKey getPostKey() {
        return new BaseClsfKey(this.postNo, this.contentType);
    }

    /**
     * 댓글 수
     */
    @Transient
    private Integer commentCnt;
    @PostLoad
    private void onLoad() {
        this.commentCnt = (CollectionUtils.isEmpty(this.commentList)) ? 0 : this.commentList.size();
    }

    /**
     * 댓글 :: List<Entity> -> List<Dto> 반환
     */
    public List<CommentDto> getCommentDtoList() {
        if (CollectionUtils.isEmpty(this.commentList)) return null;
        return this.commentList.stream()
                .map(entity -> {
                    try {
                        return CommentMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
