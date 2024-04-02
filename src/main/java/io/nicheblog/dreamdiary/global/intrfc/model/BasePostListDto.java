package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.Constant;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;

/**
 * BasePostListDto
 * <pre>
 *  (공통/상속) 일반게시판 목록 조회 Dto
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
public class BasePostListDto
        extends BaseClsfListDto {

    /** 제목 */
    protected String title;
    /** 내용 */
    protected String cn;

    /** 글분류 코드 */
    protected String ctgrClCd;
    /** 글분류 코드 */
    protected String ctgrCd;
    /** 글분류 코드 이름 */
    protected String ctgrNm;
    /** 글분류 존재 여부 */
    @Builder.Default
    protected Boolean hasCtgrNm = false;

    /** 중요 여부 (Y/N) */
    @Builder.Default
    protected String imprtcYn = "N";
    /** 상단고정 여부 (Y/N) */
    @Builder.Default
    protected String fxdYn = "N";
    /** 조회수 */
    @Builder.Default
    protected Integer hitCnt = 0;

    /** 수정권한 */
    @Builder.Default
    private String mdfable = Constant.MDFABLE_REGSTR;
    /** 수정 가능 여부 */
    @Builder.Default
    private Boolean isMdfable = false;
}
