package io.nicheblog.dreamdiary.global.intrfc.entity.embed;

import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
public class TagEmbed
        implements Serializable {

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
}