package io.nicheblog.dreamdiary.web.entity.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * ContentTagEntity
 * <pre>
 *  컨텐츠 태그 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "content_tag")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE content_tag SET del_yn = 'Y' WHERE content_tag_no = ?")
public class ContentTagEntity
        extends BaseCrudEntity {

    /** 컨텐츠 태그 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_tag_no")
    @Comment("컨텐츠 태그 번호 (PK)")
    private Integer contentTagNo;

    /** 참조 태그 번호 */
    @Column(name = "ref_tag_no")
    @Comment("참조 태그 번호")
    private Integer refTagNo;

    /** 참조 글 번호 */
    @Column(name = "ref_post_no")
    @Comment("참조 글 번호")
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    @Column(name = "ref_content_type")
    @Comment("참조 컨텐츠 타입")
    private String refContentType;

    /** 태그 정보 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_tag_no", referencedColumnName = "tag_no", updatable = false, insertable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private TagSmpEntity tag;

    /* ----- */

    /**
     * 생성자
     */
    public ContentTagEntity(Integer refTagNo, BaseClsfKey clsfKey) {
        this.refTagNo = refTagNo;
        this.refPostNo = clsfKey.getPostNo();
        this.refContentType = clsfKey.getContentType();
    }

    /**
     * entityList -> dtoList
     */
    // public List<BoardPostTagDto> getPostTagDtoList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.postTagList)) return null;
    //     List<BoardPostTagDto> dtoList = new ArrayList<>();
    //     for (BoardPostTagEntity e : this.postTagList) {
    //         BoardPostTagDto d = BoardPostTagMapstruct.INSTANCE.toDto(e);
    //         dtoList.add(d);
    //     }
    //     return dtoList;
    // }

    /**
     * entityList -> postList
     */
    // public List<BoardPostDto> getPostDtoList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.postTagList)) return null;
    //     List<BoardPostDto> postList = new ArrayList<>();
    //     for (BoardPostTagEntity e : this.postTagList) {
    //         BoardPostTagDto d = BoardPostTagMapstruct.INSTANCE.toDto(e);
    //         if (d.getPost() != null) postList.add(d.getPost());
    //     }
    //     return postList;
    // }

    /**
     * entityList -> postList
     */
    // public List<NoticeDto> getNoticeDtoList() throws Exception {
    //     if (CollectionUtils.isEmpty(this.postTagList)) return null;
    //     List<NoticeDto> noticeList = new ArrayList<>();
    //     for (BoardPostTagEntity e : this.postTagList) {
    //         BoardPostTagDto d = BoardPostTagMapstruct.INSTANCE.toDto(e);
    //         if (d.getNotice() != null) noticeList.add(d.getNotice());
    //     }
    //     return noticeList;
    // }

}
