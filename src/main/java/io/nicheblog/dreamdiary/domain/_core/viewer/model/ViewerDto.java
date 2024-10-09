package io.nicheblog.dreamdiary.domain._core.viewer.model;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseClsfKey;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAuditRegDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * ViewerDto
 * <pre>
 *  컨텐츠 열람자 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode
public class ViewerDto
        extends BaseAuditRegDto {

    /** 조치자 번호 (PK) */
    private Integer managtrNo;

    /** 참조 글 번호 */
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 (게시판 코드) */
    private String refContentType;

    /* ----- */

    /**
     * 생성자.
     * @param key 게시물의 고유 키를 포함하는 BaseClsfKey 객체
     */
    public ViewerDto(final BaseClsfKey key) {
        this.refPostNo = key.getPostNo();
        this.refContentType = key.getContentType();
    }
}
