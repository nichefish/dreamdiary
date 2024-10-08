package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.nicheblog.dreamdiary.global.auth.model.AuditorDto;
import io.nicheblog.dreamdiary.web.entity.cmm.managt.ManagtrEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.managt.ManagtrMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.managtr.ManagtrDto;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ManagtCmpstn
 * <pre>
 *  조치 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagtCmpstn
        implements Serializable {

    /** 조치자(작업자)ID */
    private String managtrId;

    /** 조치자(작업자)이름 */
    private String managtrNm;

    /** 조치자 정보 */
    private AuditorDto managtrInfo;
    /** 처리(조치)자 여부 */
    private Boolean isManagtr;
    /** 조치(작업)일시 */
    private String managtDt;

    /** 게시물 조치자 목록 */
    private List<ManagtrDto> list;

    /* ----- */

    /**
     * 게시물 조치자 목록 추가
     */
    public void addManagtr(final ManagtrDto managtr) {
        if (this.list == null) this.list = new ArrayList<>();
        list.add(managtr);
    }

    /**
     * 댓글 :: List<Dto> -> List<Entity> 반환
     */
    @JsonIgnore
    public List<ManagtrEntity> getEntityList() {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(dto -> {
                    try {
                        return ManagtrMapstruct.INSTANCE.toEntity(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}