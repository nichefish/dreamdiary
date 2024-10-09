package io.nicheblog.dreamdiary.domain.notice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.nicheblog.dreamdiary.global.ContentType;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BasePostSearchParam;
import lombok.*;

/**
 * NoticeSearchParam
 * <pre>
 *  공지사항 목록 검색 파라미터 Dto.
 * </pre>
 *
 * @author nichefish
 * @extends BasePostSearchParam
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoticeSearchParam
        extends BasePostSearchParam {

    /** 컨텐츠 타입 */
    private String contentType = ContentType.NOTICE.key;
}
