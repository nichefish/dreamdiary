package io.nicheblog.dreamdiary.feature.journal.todo.entity;

import io.nicheblog.dreamdiary.feature.attachable._shared.entity.BaseAttachableEntity;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.embed.PrefixEmbed;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.embed.PrefixEmbedModule;
import io.nicheblog.dreamdiary.feature.attachable.tag.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.feature.attachable.tag.entity.embed.TagEmbedModule;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * JournalTodoEntity
 * <pre>
 *  저널 할일 Entity.
 *  Entity that contains each distinct todo_item.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "journal_todo")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE journal_todo SET deleted_at = NOW() WHERE id = ?")
public class JournalTodoEntity
        extends BaseAttachableEntity
        implements TagEmbedModule, PrefixEmbedModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.JOURNAL_TODO;

    /** 저널 꿈 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("저널 일기 고유 번호")
    private Integer id;

    /** 컨텐츠 타입 */
    @Builder.Default
    @Column(name = "content_type", columnDefinition = "VARCHAR(50) DEFAULT 'JOURNAL_DIARY'")
    @Comment("컨텐츠 타입")
    private String contentType = CONTENT_TYPE.key;

    /** 제목 */
    @Column(name = "title")
    private String title;

    /** 내용 */
    @Column(name = "content")
    private String content;

    /** 위치 */
    @Column(name = "location", length = 200)
    @Comment("위치")
    private String location;

    /**
     * 일시(자유 텍스트 원문).
     * <pre>
     *  단일일·연속 범위·불연속 다회차·시각을 사용자가 쓴 표현 그대로 한 문자열로 보존한다.
     *  (예: "10/17,18 · 10/24,25 19:30") 구조화 날짜 컬럼으로 분해하지 않으므로
     *  불연속 회차가 연속 구간으로 왜곡되지 않는다. 정렬·필터 축으로는 사용하지 않는다.
     * </pre>
     */
    @Column(name = "date_text", length = 200)
    @Comment("일시(자유 텍스트 원문)")
    private String dateText;

    /* ----- */

    /** 년도 */
    @Column(name = "yy")
    @Comment("년도")
    private Integer yy;

    /** 월 */
    @Column(name = "mnth")
    @Comment("월")
    private Integer mnth;

    /** 순번 */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 1")
    private Integer sortOrder;

    /* ----- */

    /**
     * 할일이 선택한 개인 말머리(0..1).
     * 연결은 prefix_content 에 저장하며 Scope content_type 은 {@code JOURNAL_TODO} 를 쓴다.
     */
    @Embedded
    public PrefixEmbed prefix;

    /** 위임 :: 태그 정보 모듈 */
    @Embedded
    public TagEmbed tag;
}

