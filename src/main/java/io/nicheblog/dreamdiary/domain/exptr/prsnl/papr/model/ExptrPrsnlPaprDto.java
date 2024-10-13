package io.nicheblog.dreamdiary.domain.exptr.prsnl.papr.model;

import io.nicheblog.dreamdiary.global._common._clsf.ContentType;
import io.nicheblog.dreamdiary.global._common._clsf.comment.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.comment.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global._common._clsf.managt.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.managt.model.cmpstn.ManagtCmpstnModule;
import io.nicheblog.dreamdiary.global._common._clsf.tag.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.tag.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.model.cmpstn.ViewerCmpstn;
import io.nicheblog.dreamdiary.global._common._clsf.viewer.model.cmpstn.ViewerCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.model.CmmStus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ExptrPrsnlDto
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출서 Dto.
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode
public class ExptrPrsnlPaprDto
        extends BasePostDto
        implements CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule, Identifiable<Integer> {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.EXPTR_PRSNL_PAPR;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 경비지출 항목 영수증 첨부파일 번호 */
    protected Integer rciptFileNo;

    /** 년도 */
    protected String yy;

    /** 월 */
    protected String mnth;

    /** 확인 여부 (Y/N) */
    protected String cfYn;

    /** 개별 지출내역 목록 */
    protected List<ExptrPrsnlItemDto> itemList;

    /** 항목 금액 총합 */
    @Builder.Default
    protected Integer totAmt = 0;

    /* ----- */

    /**
     * 생성자.
     *
     * @param cfYn 승인 여부 (String)
     */
    public ExptrPrsnlPaprDto(final String cfYn) {
        this.cfYn = cfYn;
    }

    /**
     * 빈 지출항목 걸러내기.
     */
    public void filterEmptyItems() {
        List<ExptrPrsnlItemDto> itemList = this.getItemList();
        if (CollectionUtils.isEmpty(itemList)) return;

        List<ExptrPrsnlItemDto> sortedItemList = new ArrayList<>();
        for (ExptrPrsnlItemDto item : itemList) {
            if (item.isEmpty()) continue;
            sortedItemList.add(item);
        }
        this.setItemList(sortedItemList);
    }

    /* ----- */

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 상세 (DTL) Dto
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class DTL extends ExptrPrsnlPaprDto {
        //
    }

    /**
     * 경비 관리 > 경비지출서 > 경비지출서 목록 조회 (LIST) Dto
     */
    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class LIST
            extends ExptrPrsnlPaprDto {
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
    }

    /* ----- */

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /** 위임 :: 댓글 정보 모듈 */
    public CommentCmpstn comment;
    /** 위임 :: 태그 정보 모듈 */
    public TagCmpstn tag;
    /** 위임 :: 조치 정보 모듈 */
    public ManagtCmpstn managt;
    /** 위임 :: 열람 정보 모듈 */
    public ViewerCmpstn viewer;
}
