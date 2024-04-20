package io.nicheblog.dreamdiary.web.model.jrnl.day;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseClsfDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.web.model.jrnl.diary.JrnlDiaryDto;
import io.nicheblog.dreamdiary.web.model.jrnl.dream.JrnlDreamDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * JrnlDayDto
 * <pre>
 *  저널 일자 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BaseClsfDto
 * @implements CommentCmpstnModule, TagCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JrnlDayDto
        extends BaseClsfDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_DAY;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    protected String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 저널 일자 */
    private String jrnlDt;
    /** 저널 일자 요일 */
    private String jrnlDtWeekDay;
    /** 날짜미상 여부 (Y/N) */
    @Builder.Default
    private String dtUnknownYn = "N";
    /** 대략일자 (날짜미상시 해당일자 이후에 표기) */
    private String aprxmtDt;
    /** 기준일자 (저널일자 또는 대략일자) */
    private String stdrdDt;

    /** 저널 꿈 목록 */
    private List<JrnlDreamDto> jrnlDreamList;
    /** 저널 일기 목록 */
    private List<JrnlDiaryDto> jrnlDiaryList;

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /* ----- */

    /** 기준일자 */
    public String getStdrdDt() {
        if (!StringUtils.isEmpty(this.jrnlDt)) return jrnlDt;
        return aprxmtDt;
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
