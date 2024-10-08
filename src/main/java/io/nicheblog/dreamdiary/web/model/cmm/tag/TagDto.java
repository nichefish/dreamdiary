package io.nicheblog.dreamdiary.web.model.cmm.tag;

import io.nicheblog.dreamdiary.global.intrfc.model.BaseCrudDto;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
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
@EqualsAndHashCode(of = {"ctgr", "tagNm"}, callSuper = false)

public class TagDto
        extends BaseCrudDto
        implements Identifiable<Integer>, Comparable<TagDto> {

    /** 태그 번호 (PK) */
    @Positive
    private Integer tagNo;

    /** 태그 카테고리 */
    @Builder.Default
    @Size(max = 50)
    private String ctgr = "";

    /** 태그 */
    @Size(max = 50)
    private String tagNm;

    /** 게시물 태그 목록 (=게시물 리스트) */
    private List<ContentTagDto> contentTagList;

    /** 게시물 목록 (게시물 태그 목록을 글 목록으로 정제한 버전) */
    private List<?> contentList;

    /** 태그 크기 (=컨텐츠 개수) */
    @Builder.Default
    private Integer size = 0;

    /** 태그 크기 (=컨텐츠 개수) */
    @Builder.Default
    private Integer contentSize = 0;

    /** 글자 크기 클래스 */
    private String tagClass;


    /* ----- */

    /**
     * 생성자
     */
    public TagDto(final String tagNm) {
        this.tagNm = tagNm;
        this.ctgr = "";
    }
    public TagDto(final String tagNm, final String ctgr) {
        this.tagNm = tagNm;
        this.ctgr =  StringUtils.isEmpty(ctgr) ? "" : ctgr;
    }

    @Override
    public Integer getKey() {
        return this.tagNo;
    }

    /**
     * 태그이름 오름차순 정렬
     */
    @SneakyThrows
    @Override
    public int compareTo(TagDto other) {
        String thisTagNm = this.getTagNm();
        String otherTagNm = other.getTagNm();
        return thisTagNm.compareTo(otherTagNm);
    }
}
