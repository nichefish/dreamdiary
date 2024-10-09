package io.nicheblog.dreamdiary.domain._core.tag.model;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * ContentTagDto
 * <pre>
 *  컨텐츠-태그 Dto.
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContentTagDto
        extends BaseCrudDto
        implements Identifiable<Integer>, Comparable<ContentTagDto> {

    /** 컨텐츠 태그 번호 (PK) */
    @Positive
    private Integer contentTagNo;

    /** 참조 태그 번호 */
    @Positive
    private Integer refTagNo;

    /** 참조 글 번호 */
    @Positive
    private Integer refPostNo;

    /** 참조 컨텐츠 타입 */
    @Size(max = 50)
    private String refContentType;

    /** 태그 카테고리 */
    @Size(max = 50)
    private String ctgr;

    /** 태그 정보 */
    private TagDto tag;

    /** 태그 이름 */
    @Size(max = 50)
    private String tagNm;

    /* ----- */

    /**
     * 태그이름 오름차순 정렬
     * @param other - 비교할 ContentTagDto 객체
     * @return int - 사전 순 비교 결과 (음수, 0, 양수)
     */
    @SneakyThrows
    @Override
    public int compareTo(ContentTagDto other) {
        String thisTagNm = this.getTagNm();
        String otherTagNm = other.getTagNm();
        return thisTagNm.compareTo(otherTagNm);
    }

    /* ----- */

    @Override
    public Integer getKey() {
        return this.contentTagNo;
    }
}
