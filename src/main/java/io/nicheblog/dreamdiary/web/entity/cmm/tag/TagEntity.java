package io.nicheblog.dreamdiary.web.entity.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseCrudEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Where;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TagEntity
 * <pre>
 *  태그 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudEntity
 */
@Entity
@Table(name = "tag")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
public class TagEntity
        extends BaseCrudEntity {

    @PostLoad
    private void onLoad() {
        this.size = CollectionUtils.isEmpty(this.contentTagList) ? 0 : this.contentTagList.size();
    }

    /** 태그 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_no")
    @Comment("태그 번호 (PK)")
    private Integer tagNo;

    /** 태그 */
    @Column(name = "tag_nm")
    @Comment("태그")
    private String tagNm;

    /** 컨텐츠 태그 */
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
    private List<ContentTagEntity> contentTagList;

    /** 태그 크기 (=컨텐츠 개수) */
    @Transient
    private Integer size;

    /* ----- */

    /**
     * 생성자
     */
    public TagEntity(final String tagNm) {
        this.tagNm = tagNm;
    }

    /**
     * entityList -> dtoList
     */
    public List<ContentTagDto> getContentTagDtoList() throws Exception {
        if (CollectionUtils.isEmpty(this.contentTagList)) return null;
        return this.contentTagList.stream()
                .map(entity -> {
                    try {
                        return ContentTagMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}
