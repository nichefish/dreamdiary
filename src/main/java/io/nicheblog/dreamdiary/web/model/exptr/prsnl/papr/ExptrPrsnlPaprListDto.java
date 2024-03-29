package io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.ManagtEmbed;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostListDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.web.model.cmm.CmmStus;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import javax.persistence.Embedded;
import java.util.List;
import java.util.Objects;

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

    /**
     * 외부에서 itemList 주입받아서 숫자들 init
     */
    public void initStus(final List<ExptrPrsnlItemEntity> itemList) {
        if (CollectionUtils.isEmpty(itemList)) return;
        int totAmt = 0;
        int itemCnt = 0;
        int atchRciptCnt = 0;
        int orgnlRciptCnt = 0;
        int orgnlRciptNotNeededCnt = 0;
        for (ExptrPrsnlItemEntity item : itemList) {
            if ("Y".equals(item.getRjectYn())) continue;
            itemCnt++;
            totAmt += item.getExptrAmt();
            if (item.getAtchFileDtlInfo() != null) atchRciptCnt++;
            if ("Y".equals(item.getOrgnlRciptYn())) orgnlRciptCnt++;
            if ("X".equals(item.getOrgnlRciptYn())) orgnlRciptNotNeededCnt++;
        }
        this.itemCnt = itemCnt;
        this.totAmt = totAmt;
        this.atchRciptCnt = atchRciptCnt;
        this.orgnlRciptCnt = orgnlRciptCnt;
        this.orgnlRciptNotNeededCnt = orgnlRciptNotNeededCnt;

        // 영수증 첨부상태 세팅
        if (this.atchRciptCnt == 0) {
            this.setAtchRciptStus(new CmmStus("미제출", Constant.BS_DANGER, "bi bi-x"));
        } else if (this.atchRciptCnt < this.itemCnt) {
            this.setAtchRciptStus(new CmmStus("일부제출", Constant.BS_WARNING, "bi bi-caret-up"));
        } else if (this.atchRciptCnt.equals(this.itemCnt)) {
            this.setAtchRciptStus(new CmmStus("제출완료", Constant.BS_PRIMARY, "bi bi-circle fs-8"));
        }

        // 원본영수증 제출상태 세팅
        boolean hasExptr = this.itemCnt > 0;
        boolean allOrgnlRciptNotNeeded = hasExptr && Objects.equals(this.orgnlRciptNotNeededCnt, this.itemCnt);
        if (hasExptr && this.orgnlRciptCnt == 0 && !allOrgnlRciptNotNeeded) {
            this.setOrgnlRciptStus(new CmmStus("미제출", Constant.BS_DANGER, "bi bi-x"));
        } else if (this.orgnlRciptCnt + this.orgnlRciptNotNeededCnt < this.itemCnt) {
            this.setOrgnlRciptStus(new CmmStus("일부제출", Constant.BS_WARNING, "bi bi-caret-up"));
        } else if (this.orgnlRciptCnt + this.orgnlRciptNotNeededCnt == this.itemCnt) {
            this.setOrgnlRciptStus(new CmmStus("제출완료", Constant.BS_PRIMARY, "bi bi-circle fs-8"));
        }
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtEmbed managt;
}
