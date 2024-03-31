package io.nicheblog.dreamdiary.global.intrfc.model.cmpstn;

import io.nicheblog.dreamdiary.web.entity.cmm.tag.ContentTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.cmm.tag.ContentTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.ContentTagDto;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * TagCmpstn
 * <pre>
 *  태그 관련 정보 위임
 * </pre>
 *
 * @author nichefish
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagCmpstn
        implements Serializable {

    /** Tagify (ex.) = [{"value":"123.123.123.123"},{"value":"234.234.234.234"}] 문자열 형식으로 넘어온댜. */
    public List<String> getParsedTagList() {
        if (StringUtils.isEmpty(this.tagListStr)) return new ArrayList<>();
        JSONArray jArray = new JSONArray(tagListStr);
        return IntStream.range(0, jArray.length())
                .mapToObj(jArray::getJSONObject)
                .map(json -> json.getString("value"))
                .collect(Collectors.toList());
    }

    /** 컨텐츠 타입 :: 상위에서 주입받음. */
    private String contentType;

    /** 컨텐츠 태그 목록 */
    private List<ContentTagDto> list;

    /** 컨텐츠 태그 문자열 목록 */
    private List<String> tagStrList;
    /** 컨텐츠 태그 문자열 (','로 구분) */
    private String tagListStr;

    /* ----- */

    /**
     * 태그 :: List<Dto> -> List<Entity> 반환
     */
    public List<ContentTagEntity> getEntityList() {
        if (CollectionUtils.isEmpty(this.list)) return null;
        return this.list.stream()
                .map(dto -> {
                    try {
                        return ContentTagMapstruct.INSTANCE.toEntity(dto);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
}