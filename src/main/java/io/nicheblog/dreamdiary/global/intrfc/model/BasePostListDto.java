package io.nicheblog.dreamdiary.global.intrfc.model;

import io.nicheblog.dreamdiary.global.Constant;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.MappedSuperclass;

/**
 * BasePostListDto
 * <pre>
 *  (공통/상속) 게시판 목록 조회 Dto
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

    /** 글분류 분류코드 */
    protected String ctgrClCd;
    /** 글분류 코드 */
    protected String ctgrCd;
    /** 글분류 코드 이름 */
    protected String ctgrNm;

    /** 상단고정여부 */
    @Builder.Default
    protected String fxdYn = "N";
    /** 조회수 */
    @Builder.Default
    protected Integer hitCnt = 0;
    /** 중요 여부 */
    @Builder.Default
    protected String imprtcYn = "N";
    /** 수정권한 */
    @Builder.Default
    protected String mdfable = Constant.MDFABLE_REGSTR;

    /* ----- */

    /**
     * hasCtgrNm
     */
    public Boolean getHasCtgrNm() {
        return StringUtils.isNotEmpty(this.ctgrNm);
    }

}
