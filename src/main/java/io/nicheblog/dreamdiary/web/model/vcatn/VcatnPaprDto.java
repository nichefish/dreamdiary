package io.nicheblog.dreamdiary.web.model.vcatn;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.web.entity.vcatn.VcatnSchdulEntity;
import io.nicheblog.dreamdiary.web.mapstruct.vcatn.VcatnSchdulMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.comment.CommentDto;
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
        extends BasePostDto {

    /* 확인 여부*/
    private String cfYn;

    /* 휴가 추가 리스트*/
    private List<VcatnSchdulDto> vcatnSchdulList;

    /* ----- */

    /** sublist 변환 */
    public List<VcatnSchdulEntity> getVcatnSchdulEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.vcatnSchdulList)) return null;
        List<VcatnSchdulEntity> vcatnSchdulEntityList = new ArrayList<>();
        for (VcatnSchdulDto vcatnSchdulDto : this.vcatnSchdulList) {
            String vcatnCd = vcatnSchdulDto.getVcatnCd();
            if (Constant.VCATN_AM_HALF.equals(vcatnCd)) {
                vcatnSchdulDto.setBeginDt(vcatnSchdulDto.getBeginDt() + " 09:00:00");
                vcatnSchdulDto.setEndDt(vcatnSchdulDto.getEndDt() + " 14:00:00");
            } else if (Constant.VCATN_PM_HALF.equals(vcatnCd)) {
                vcatnSchdulDto.setBeginDt(vcatnSchdulDto.getBeginDt() + " 14:00:00");
                vcatnSchdulDto.setEndDt(vcatnSchdulDto.getEndDt() + " 18:00:00");
            } else {
                vcatnSchdulDto.setBeginDt(vcatnSchdulDto.getBeginDt() + " 01:00:00");
                vcatnSchdulDto.setEndDt(vcatnSchdulDto.getEndDt() + " 23:59:59");
            }
            VcatnSchdulEntity entity = VcatnSchdulMapstruct.INSTANCE.toEntity(vcatnSchdulDto);
            vcatnSchdulEntityList.add(entity);
        }
        return vcatnSchdulEntityList;
    }

    /* ----- 댓글 모듈 ----- */

    /** 댓글 목록 */
    private List<CommentDto> commentList;
    /** 댓글 갯수 */
    @Builder.Default
    private Integer commentCnt = 0;
    /** 댓글 존재 여부 */
    @Builder.Default
    private Boolean hasComment = false;
}
