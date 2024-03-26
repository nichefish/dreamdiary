package io.nicheblog.dreamdiary.web.model.cmm;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BaseSearchParam
 * <pre>
 *  (공통/상속) 검색 파라미터 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseParam
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BaseSearchParam
        extends BaseParam {

    /**
     * 페이지 크기
     */
    private Integer pageSize;
    /**
     * 페이지 번호
     */
    private Integer pageNo;

    /**
     * 검색 유형 (검색 키워드와 한 세트)
     */
    private String searchType;
    /**
     * 검색 키워드 (검색 유형과 한 세트)
     */
    private String searchKeyword;

    /**
     * 등록자 ID
     */
    private String regstrId;
    /**
     * 조치자 ID
     */
    private String managtrId;
}
