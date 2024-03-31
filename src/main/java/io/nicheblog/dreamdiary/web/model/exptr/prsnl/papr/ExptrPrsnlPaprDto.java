package io.nicheblog.dreamdiary.web.model.exptr.prsnl.papr;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ManagtCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.ViewerCmpstn;
import io.nicheblog.dreamdiary.web.entity.exptr.prsnl.ExptrPrsnlItemEntity;
import io.nicheblog.dreamdiary.web.mapstruct.exptr.prsnl.ExptrPrsnlItemMapstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.Embedded;
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
        extends BasePostDto {

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
    /** 확인 여부 (Y/N) */
    private String cfYn;

    /** 개별 지출내역 목록 */
    List<ExptrPrsnlItemDto> itemList;
    /** 항목 금액 총합 */
    @Builder.Default
    private Integer totAmt = 0;

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

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    @Embedded
    public CommentCmpstn comment;

    /** 조치 정보 모듈 (위임) */
    @Embedded
    public ManagtCmpstn managt;

    /** 열람자 정보 모듈 (위임) */
    @Embedded
    public ViewerCmpstn viewer;
}
