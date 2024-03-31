package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import io.nicheblog.dreamdiary.web.entity.cmm.viewer.ViewerEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.viewer.ViewerMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.viewer.ViewerDto;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewerCmpstn
 * <pre>
 *  컨텐츠 열람자 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewerCmpstn {

    /** 컨텐츠 열람자 목록 */
    private List<ViewerDto> list;

    /* ----- */

    /**
     * 열람자 :: List<Dto> -> List<Entity> 반환
     */
    public List<ViewerEntity> getEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(dto -> {
                    try {
                        return ViewerMapstruct.INSTANCE.toEntity(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}