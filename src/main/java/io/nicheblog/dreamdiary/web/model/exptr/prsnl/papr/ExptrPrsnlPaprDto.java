package io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.web.mapstruct.exptr.prsnl.ExptrPrsnlItemMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.CmmStus;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ExptrPrsnlDto
 * <pre>
 *  경비 관리 > 경비지출서 > 경비지출서 Dto
 *  ※ 경비지출서(exptr_prsnl_papr) = 경비지출서. 경비지출항목(exptr_prsnl_item)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
    List<ExptrPrsnlItemDto> itemList;
    /** 항목 금액 총합 */
    @Builder.Default
    protected Integer totAmt = 0;

    /* ----- */

    /**
     * 생성자
     */
    public ExptrPrsnlPaprDto(final String cfYn) {
        this.cfYn = cfYn;
    }

    /**
     * 개별내역 목록을 엔티티로 변환해서 반환
     */
    public List<ExptrPrsnlItemEntity> getItemEntityList() {
        if (CollectionUtils.isEmpty(this.itemList)) return null;
        return this.itemList.stream()
                            .map(item -> {
                                try {
                                    return ExptrPrsnlItemMapstruct.INSTANCE.toEntity(item);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());
    }

    /**
     * 빈 지출항목 걸러내기
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

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class DTL extends ExptrPrsnlPaprDto {
        //
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class LIST extends ExptrPrsnlPaprDto {
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

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
    /** 조치 정보 모듈 (위임) */
    public ManagtCmpstn managt;
    /** 열람자 정보 모듈 (위임) */
    public ViewerCmpstn viewer;
}
