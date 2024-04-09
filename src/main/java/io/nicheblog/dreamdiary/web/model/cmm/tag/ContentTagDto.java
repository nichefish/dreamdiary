package io.nicheblog.dreamdiary.web.model.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * ContentTagDto
 * <pre>
 *  태그 Dto
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
public class ContentTagDto
        extends BaseCrudDto
        implements Identifiable<Integer> {

    /** 컨텐츠 태그 번호 (PK) */
    private Integer contentTagNo;

    /** 참조 태그 번호 */
    private Integer refTagNo;
    /** 참조 글 번호 */
    private Integer refPostNo;
    /** 참조 컨텐츠 타입 */
    private String refContentType;

    /** 태그 정보 */
    private TagDto tag;

    /** 태그 이름 */
    private String tagNm;

    @Override
    public Integer getKey() {
        return this.contentTagNo;
    }
}
