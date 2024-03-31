package io.nicheblog.dreamdiary.global.intrfc.entity.embed;

import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.viewer.ViewerMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.viewer.ViewerDto;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewerEmbed
 * <pre>
 *  컨텐츠 열람자 관련 정보 위임
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
public class ViewerEmbed
        implements Serializable {

    /** 컨텐츠 열람자 목록 */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no", insertable = false, updatable = false)),
            @JoinColumnOrFormula(column = @JoinColumn(name = "ref_content_type", referencedColumnName = "content_type", insertable = false, updatable = false))
    })
    @Fetch(FetchMode.SELECT)
    @OrderBy("regDt DESC")
    @NotFound(action = NotFoundAction.IGNORE)
    private List<ViewerEntity> list;

    /* ----- */

    /**
     * 열람자 :: List<Entity> -> List<Dto> 반환
     */
    public List<ViewerDto> getDtoList() throws Exception {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(entity -> {
                    try {
                        return ViewerMapstruct.INSTANCE.toDto(entity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}