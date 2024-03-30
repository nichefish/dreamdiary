package io.nicheblog.dreamdiary.global.intrfc.entity.embed;

import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.entity.cmm.tag.TagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TagEmbed
 * <pre>
 *  태그 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEmbed {

    @PostLoad
    private void onLoad() {
        this.tagListStr = this.generateTagListStr();
        this.tagStrList = this.generateTagStrList();
    }
    private String generateTagListStr() {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(contentTag -> contentTag.getTag().getTagNm())
                .collect(Collectors.joining(","));
    }
    private List<String> generateTagStrList() {
         if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(ContentTagEntity::getTag)
                .filter(tag -> tag != null && StringUtils.isNotEmpty(tag.getTagNm()))
                .map(TagEntity::getTagNm)
                .collect(Collectors.toList());
    }

    /** 컨텐츠 태그 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_content_type", referencedColumnName = "content_type", insertable = false, updatable = false)),
    })
    @Fetch(FetchMode.SELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("컨텐츠 태그 목록")
    private List<ContentTagEntity> list;

    /** 컨텐츠 태그 문자열 목록 */
    @Transient
    private List<String> tagStrList;

    /** 컨텐츠 태그 문자열 (','로 구분) */
    @Transient
    private String tagListStr;

    /* ----- */

    /**
     * 태그 :: List<Entity> -> List<Dto> 반환
     */
    public List<ContentTagDto> getDtoList() {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
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