package io.nicheblog.dreamdiary.web.model.jrnl.dream;

import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.CommentCmpstnModule;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.global.intrfc.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import java.util.Date;

/**
 * JrnlDreamDto
 * <pre>
 *  저널 꿈 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostDto
 * @implements CommentCmpstnModule, TagCmpstnModule
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class JrnlDreamDto
        extends BasePostDto
        implements Identifiable<Integer>, CommentCmpstnModule, TagCmpstnModule, Comparable<JrnlDreamDto> {

    /** 필수: 컨텐츠 타입 */
    private static final ContentType CONTENT_TYPE = ContentType.JRNL_DREAM;
    /** 필수(Override): 글분류 코드 */
    private static final String CTGR_CL_CD = CONTENT_TYPE.name() + "_CTGR_CD";

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = CONTENT_TYPE.key;

    /* ----- */

    /** 저널 일자 번호 */
    private Integer jrnlDayNo;
    /** 저널 기준일자 */
    private String stdrdDt;
    /** 저널 기준일자 */
    private Integer yy;
    /** 저널 기준일자 */
    private Integer mnth;
    /** 순번 */
    private Integer idx;

    /** 악몽 여부 (Y/N) */
    @Builder.Default
    private String nhtmrYn = "N";

    /** 입면환각 여부 (Y/N) */
    @Builder.Default
    private String hallucYn = "N";

    /** 타인 꿈 여부 (Y/N) */
    @Builder.Default
    private String elseDreamYn = "N";
    /** 꿈꾼이(타인) 이름 */
    @Column(name = "ELSE_DREAMER_NM", length = 64)
    private String elseDreamerNm;

    /* ----- */

    @Override
    public Integer getKey() {
        return this.postNo;
    }

    /**
     * 날짜 오름차순 정렬
     */
    @SneakyThrows
    @Override
    public int compareTo(JrnlDreamDto other) {
        Date thisDate = DateUtils.asDate(this.stdrdDt);
        Date otherDate = DateUtils.asDate(other.stdrdDt);
        return thisDate.compareTo(otherDate);
    }

    /* ----- */

    /** 댓글 정보 모듈 (위임) */
    public CommentCmpstn comment;
    /** 태그 정보 모듈 (위임) */
    public TagCmpstn tag;
}
