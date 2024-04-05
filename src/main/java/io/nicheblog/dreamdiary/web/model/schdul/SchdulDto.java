package io.nicheblog.dreamdiary.web.model.schdul;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.web.entity.schdul.SchdulPrtcpntEntity;
import io.nicheblog.dreamdiary.web.mapstruct.schdul.SchdulPrtcpntMapstruct;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SchdulDto
 * <pre>
 *  일정 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseCrudDto
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SchdulDto
        extends BasePostDto
        implements CommentCmpstnModule, TagCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.SCHDUL;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 일정 코드 */
    private String schdulCd;
    /** 일정 분류 코드 이름 */
    private String schdulNm;

    /** 시작일 */
    private String bgnDt;
    /** 종료일 */
    private String endDt;

    /** 개인일정 여부 (Y/N) */
    @Builder.Default
    private String prvtYn = "N";

    /** 참석자 리스트  */
    private List<SchdulPrtcpntDto> prtcpntList;
    /** 참석자 목록 문자열 */
    private String prtcpntListStr;

    /* ----- */

    /**
     * 개인일정 여부
     */
    public Boolean getIsPrvt() {
        return "Y".equals(this.prvtYn);
    }

    public List<SchdulPrtcpntEntity> getPrtcpntEntityList() throws Exception {
        if (CollectionUtils.isEmpty(this.prtcpntList)) return new ArrayList<>();
        List<SchdulPrtcpntEntity> entityList = new ArrayList<>();
        for (SchdulPrtcpntDto prtcpnt : this.prtcpntList) {
            if (StringUtils.isEmpty(prtcpnt.getUserId())) continue;
            entityList.add(SchdulPrtcpntMapstruct.INSTANCE.toEntity(prtcpnt));
        }
        return entityList;
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}