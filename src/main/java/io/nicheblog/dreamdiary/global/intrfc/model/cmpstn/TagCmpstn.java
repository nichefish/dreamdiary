package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import lombok.*;
import org.springframework.util.CollectionUtils;

import javax.persistence.Transient;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TagCmpstn
 * <pre>
 *  태그 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCmpstn {

    /** 컨텐츠 태그 목록 */
    private List<ContentTagDto> list;

    /** 컨텐츠 태그 문자열 목록 */
    private List<String> tagStrlist;

    /** 컨텐츠 태그 문자열 (','로 구분) */
    private String tagListStr;

    /* ----- */

    /**
     * 태그 :: List<Dto> -> List<Entity> 반환
     */
    public List<ContentTagEntity> getEntityList() {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(dto -> {
                    try {
                        return ContentTagMapstruct.INSTANCE.toEntity(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}