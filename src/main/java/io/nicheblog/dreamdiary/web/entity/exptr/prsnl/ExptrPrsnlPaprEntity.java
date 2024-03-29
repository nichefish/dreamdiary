package io.nicheblog.dreamdiary.web.entity.exptr.prsnl;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.entity.BasePostEntity;
import io.nicheblog.dreamdiary.web.mapstruct.exptr.prsnl.ExptrPrsnlItemMapstruct;
import io.nicheblog.dreamdiary.web.model.exptr.prsnl.ExptrPrsnlItemDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ExptrPrsnlPaprEntity
 * <pre>
 *  경비관리 > 경비지출서 Entity
 *  ※ 경비지출서(ExptrPrsnlPapr) = 경비지출서. 경비지출항목(ExptrPrsnlItem)을 1:N으로 관리한다.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "exptr_prsnl_papr")
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE exptr_prsnl SET del_yn = 'Y' WHERE post_no = ?")
public class ExptrPrsnlPaprEntity
        extends BasePostEntity {

    /** 필수: 컨텐츠 타입 */
    private static final String CONTENT_TYPE = ContentType.EXPTR_PRSNL_PAPR.key;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE + "_CTGR_CD";

    /** 글 번호 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_no")
    @Comment("글 번호")
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE;

    /** 년도 */
    @Column(name = "yy")
    @Comment("년도")
    private String yy;

    /** 월 */
    @Column(name = "mnth")
    @Comment("월")
    private Integer mnth;

    /** 완료 여부 */
    @Builder.Default
    @Column(name = "compt_yn")
    @Comment("완료 여부")
    private String comptYn = "N";

    /** 확인 여부 */
    @Builder.Default
    @Column(name = "cf_yn")
    @Comment("확인 여부")
    private String cfYn = "N";

    /** 경비지출 항목 목록 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "ref_post_no", referencedColumnName = "post_no")
    @Fetch(FetchMode.SELECT)
    @OrderBy("exptrDt ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("경비지출 항목 목록")
    private List<ExptrPrsnlItemEntity> itemList;

    /* ----- */

    /**
     * 개별내역 목록을 Dto로 변환해서 반환
     */
    public List<ExptrPrsnlItemDto> getItemDtoList() throws Exception {
        if (CollectionUtils.isEmpty(this.itemList)) return null;
        List<ExptrPrsnlItemDto> entityList = new ArrayList<>();
        for (ExptrPrsnlItemEntity item : itemList) {
            entityList.add(ExptrPrsnlItemMapstruct.INSTANCE.toDto(item));
        }
        return entityList;
    }

    /**
     * 서브엔티티 List 처리를 위한 Setter (override)
     * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setItemList(final List<ExptrPrsnlItemEntity> itemList) {
        if (CollectionUtils.isEmpty(itemList)) return;
        if (this.itemList == null) {
            this.itemList = itemList;
        } else {
            this.itemList.clear();
            this.itemList.addAll(itemList);
        }
    }
}
