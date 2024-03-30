package io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.web.model.cmm.CmmStus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embedded;
import java.util.List;

/**
 * ExptrPrsnlListDto
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출서 목록 조회 Dto
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostListDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExptrPrsnlPaprListDto
        extends BasePostListDto {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.EXPTR_PRSNL_PAPR;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 년도 */
    private String yy;
    /** 월 */
    private String mnth;
    /** 확인 여부 */
    private String cfYn;

    /** 개별 지출내역 목록 */
    List<ExptrPrsnlItemDto> itemList;
    /** 항목 금액 총합 */
    @Builder.Default
    private Integer totAmt = 0;

    /** 항목 건수 */
    @Builder.Default
    private Integer itemCnt = 0;
    /** 영수증 첨부 건수 */
    @Builder.Default
    private Integer atchRciptCnt = 0;
    /** 영수증 제출 건수 */
    @Builder.Default
    private Integer orgnlRciptCnt = 0;
    /** 영수증 불필요 건수 */
    @Builder.Default
    private Integer orgnlRciptNotNeededCnt = 0;

    /** 영수증 스캔본 첨부 상태 */
    private CmmStus atchRciptStus;
    /** 영수증 원본 제출 상태 */
    private CmmStus orgnlRciptStus;

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;
}
