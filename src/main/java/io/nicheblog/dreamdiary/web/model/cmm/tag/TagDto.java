package io.nicheblog.dreamdiary.web.model.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * TagDto
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TagDto
        extends BaseCrudDto
        implements Identifiable<Integer> {

    /** 태그 번호 (PK) */
    private Integer tagNo;

    /** 태그 */
    private String tagNm;

    /** 게시물 태그 목록 (=게시물 리스트) */
    private List<ContentTagDto> contentTagList;
    /** 게시물 목록 (게시물 태그 목록을 글 목록으로 정제한 버전) */
    private List<?> contentList;
    /** 태그 크기 (=컨텐츠 개수) */
    @Builder.Default
    private Integer size = 0;

    /* ----- */

    /**
     * 생성자
     */
    public TagDto(final String tagNm) {
        this.tagNm = tagNm;
    }

    @Override
    public Integer getKey() {
        return this.tagNo;
    }
}
