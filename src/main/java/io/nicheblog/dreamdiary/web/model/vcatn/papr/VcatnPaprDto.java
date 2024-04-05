package io.nicheblog.dreamdiary.web.model.vcatn.papr;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.*;
import io.nicheblog.dreamdiary.web.entity.vcatn.papr.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.papr.VcatnSchdulMapstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * VcatnPaprDto
 * <pre>
 *  휴가계획서 Dto
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
@ToString
public class VcatnPaprDto
        extends BasePostDto
        implements CommentCmpstnModule, TagCmpstnModule, ManagtCmpstnModule, ViewerCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.VCATN_PAPR;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 확인 여부 (Y/N) */
    private String cfYn;

    /** 휴가 일정 리스트 */
    private List<VcatnSchdulDto> schdulList;

    /* ----- */

    /** sublist 변환 */
    public List<VcatnSchdulEntity> getSchdulEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.schdulList)) return null;
        List<VcatnSchdulEntity> vcatnSchdulEntityList = new ArrayList<>();
        for (VcatnSchdulDto vcatnSchdulDto : this.schdulList) {
            String vcatnCd = vcatnSchdulDto.getVcatnCd();
            if (Constant.VCATN_AM_HALF.equals(vcatnCd)) {
                vcatnSchdulDto.setBgnDt(vcatnSchdulDto.getBgnDt() + " 09:00:00");
                vcatnSchdulDto.setEndDt(vcatnSchdulDto.getEndDt() + " 14:00:00");
            } else if (Constant.VCATN_PM_HALF.equals(vcatnCd)) {
                vcatnSchdulDto.setBgnDt(vcatnSchdulDto.getBgnDt() + " 14:00:00");
                vcatnSchdulDto.setEndDt(vcatnSchdulDto.getEndDt() + " 18:00:00");
            } else {
                vcatnSchdulDto.setBgnDt(vcatnSchdulDto.getBgnDt() + " 01:00:00");
                vcatnSchdulDto.setEndDt(vcatnSchdulDto.getEndDt() + " 23:59:59");
            }
            VcatnSchdulEntity entity = VcatnSchdulMapstruct.INSTANCE.toEntity(vcatnSchdulDto);
            vcatnSchdulEntityList.add(entity);
        }
        return vcatnSchdulEntityList;
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString
    public static class DTL extends VcatnPaprDto {
        //
    }

    @Getter
    @Setter
    @SuperBuilder(toBuilder = true)
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ToString
    public static class LIST extends VcatnPaprDto {
        //
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
