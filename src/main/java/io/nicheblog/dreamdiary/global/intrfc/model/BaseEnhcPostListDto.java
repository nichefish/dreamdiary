package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuditorDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.MappedSuperclass;

/**
 * BasePostListDto
 * <pre>
 *  (공통/상속) 게시판 목록 조회 Dto
 *  "All classes in the hierarchy must be annotated with @SuperBuilder."
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfListDto
 */
@MappedSuperclass
@Getter(AccessLevel.PUBLIC)
@Setter
@SuperBuilder(toBuilder=true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BaseEnhcPostListDto
        extends BasePostListDto {

    /** 수정권한 */
    @Builder.Default
    protected String mdfable = Constant.MDFABLE_REGSTR;

    /** 조치자(작업자)ID */
    private String managtrId;

    /** 조치자(작업자)이름 */
    private String managtrNm;

    /** 조치일시 */
    private String managtDt;

    /** 조치자 정보 */
    private AuditorDto managtrInfo;

    /** 게시물 조치자 목록 */
    // private List<BoardPostManagtrDto> managtrList;

    /** 열람자 목록 */
    // private List<BoardPostViewerDto> viewerList;

    /** (수정시) 조치일자 변경하지 않음 변수 */
    private String managtDtUpdtYn;
}
