package io.nicheblog.dreamdiary.domain.jrnl.dream.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * JrnlDreamContentTagParam
 * <pre>
 *  저널 꿈 컨텐츠 태그 목록 검색 파라미터.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@ToString
public class JrnlDreamContentTagParam {

    /** 참조 글 번호 */
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    private String refContentType;

    /** 태그 번호 */
    private Integer tagNo;

    /** 년도 */
    private Integer yy;

    /** 월 */
    private Integer mnth;

    /** 등록자 ID */
    private String regstrId;
}
