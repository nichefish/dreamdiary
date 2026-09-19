package io.nicheblog.dreamdiary.feature.journal.todo.model;

import io.nicheblog.dreamdiary.feature.attachable._shared.model.BaseAttachableDto;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.cmpstn.LifecycleCmpstn;
import io.nicheblog.dreamdiary.feature.attachable.prefix.model.PrefixDto;
import io.nicheblog.dreamdiary.feature.attachable.lifecycle.model.cmpstn.LifecycleCmpstnModule;
import io.nicheblog.dreamdiary.feature.attachable.tag.model.cmpstn.TagCmpstn;
import io.nicheblog.dreamdiary.feature.attachable.tag.model.cmpstn.TagCmpstnModule;
import io.nicheblog.dreamdiary.feature.journal._shared.model.JournalPeriodModule;
import io.nicheblog.dreamdiary.global.intrfc.model.Identifiable;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;

/**
 * JournalTodoDto
 * <pre>
 *  저널 할일 Dto.
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
public class JournalTodoDto
        extends BaseAttachableDto
        implements Identifiable<Integer>, TagCmpstnModule, JournalPeriodModule, LifecycleCmpstnModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.JOURNAL_TODO;
    /** 컨텐츠 타입 */
    @Builder.Default
    private String contentType = CONTENT_TYPE.key;

       /** 제목 */
    private String title;

    /** 내용 */
    private String content;

    /** 마크다운 처리된 내용 */
    private String markdownContent;

    /** 위치 */
    @Size(max = 200)
    private String location;

    /**
     * 일시(자유 텍스트 원문).
     * <pre>
     *  단일일·연속 범위·불연속 다회차·시각을 사용자가 쓴 표현 그대로 한 문자열로 보존한다.
     *  서버는 이 값을 해석하지 않고 원문 그대로 저장·반환한다.
     * </pre>
     */
    @Size(max = 200)
    private String dateText;

    /** 선택된 말머리(표시용). prefix_content 연결에서 조립한다. */
    private PrefixDto prefix;

    /** 선택한 말머리 ID (0..1, null 이면 선택 해제) */
    private Integer prefixId;

    /* ----- */

    /** 년도 */
    private Integer yy;
    /** 월 */
    private Integer mnth;
    /** 순번 */
    private Integer sortOrder;

    /* ----- */

    @Override
    public Integer getKey() {
        return this.id;
    }

    /** 위임 :: 라이프사이클 모듈. 목록·상세 enrich 가 부착 테이블에서 채운다. 없으면 OPEN. */
    public LifecycleCmpstn lifecycle;

    /** 위임 :: 태그 정보 모듈 */
    public TagCmpstn tag;
}
