package io.nicheblog.dreamdiary.web.model.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
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
        implements Identifiable<Integer>, Comparable<ContentTagDto> {

    /** 컨텐츠 태그 번호 (PK) */
    private Integer contentTagNo;

    /** 참조 태그 번호 */
    private Integer refTagNo;
    /** 참조 글 번호 */
    private Integer refPostNo;
    /** 참조 컨텐츠 타입 */
    private String refContentType;

    /** 태그 카테고리 */
    private String tagCtgr;
    /** 태그 정보 */
    private TagDto tag;

    /** 태그 이름 */
    private String tagNm;

    /* ----- */

    @Override
    public Integer getKey() {
        return this.contentTagNo;
    }

    /**
     * 태그이름 오름차순 정렬
     */
    @SneakyThrows
    @Override
    public int compareTo(ContentTagDto other) {
        String thisTagNm = this.getTagNm();
        String otherTagNm = other.getTagNm();
        return thisTagNm.compareTo(otherTagNm);
    }
}
