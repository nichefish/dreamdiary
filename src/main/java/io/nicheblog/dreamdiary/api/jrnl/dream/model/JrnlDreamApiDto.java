package io.nicheblog.dreamdiary.api.jrnl.dream.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BasePostDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * JrnlDreamApiDto
 * <pre>
 *  API:: 저널 꿈 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class JrnlDreamApiDto
        extends BasePostDto {

    /** 저널 꿈 고유 번호 (PK) */
    private Integer postNo;

    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = "JRNL_DREAM";

    /** 저널 일자 번호 */
    private Integer jrnlDayNo;

    /** 순번 */
    private Integer sortOrdr;

    /** 내용 */
    private String cn;

    /** 편집완료 여부 (Y/N) */
    @Builder.Default
    private String editComptYn = "N";

    /** 타인 꿈 여부 (Y/N) */
    @Builder.Default
    private String elseDreamYn = "N";

    /** 꿈꾼이(타인) 이름 */
    private String elseDreamerNm;
}
