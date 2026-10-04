package io.nicheblog.dreamdiary.feature.prospective.keep.entity;

import io.nicheblog.dreamdiary.feature.attachable._shared.entity.BaseAttachableEntity;
import io.nicheblog.dreamdiary.feature.attachable._shared.type.ContentType;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.embed.PrefixEmbed;
import io.nicheblog.dreamdiary.feature.attachable.prefix.entity.embed.PrefixEmbedModule;
import io.nicheblog.dreamdiary.feature.attachable.state.entity.embed.StateEmbed;
import io.nicheblog.dreamdiary.feature.attachable.state.entity.embed.StateEmbedModule;
import io.nicheblog.dreamdiary.feature.attachable.tag.entity.embed.TagEmbed;
import io.nicheblog.dreamdiary.feature.attachable.tag.entity.embed.TagEmbedModule;
import io.nicheblog.dreamdiary.feature.prospective.project.entity.ProspectiveProjectEntity;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * ProspectiveKeepEntity
 * <pre>
 *  Prospective Keep Entity.
 *  Entity that contains each distinct keep_item.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "prospective_keep")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE prospective_keep SET deleted_at = NOW() WHERE id = ?")
public class ProspectiveKeepEntity
        extends BaseAttachableEntity
        implements TagEmbedModule, PrefixEmbedModule, StateEmbedModule {

    /** 필수: 컨텐츠 타입 */
    @Builder.Default
    private static final ContentType CONTENT_TYPE = ContentType.PROSPECTIVE_KEEP;

    /** Prospective Keep 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("Prospective Keep 고유 번호")
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

    /**
     * 유효 기한.
     * <pre>
     *  「이 날이 지나면 더는 할 수 없다」는 단일 날짜. 시각은 갖지 않는다.
     *  {@code dateText} 가 사람이 읽을 일시 원문(다회차·범위)을 보존하는 표시용인 데 반해,
     *  이 컬럼은 정렬·임박 판정에 쓰는 기계용 축이다. 다회차 항목은 마지막 회차 하루만 담는다.
     *  기한이 지나도 라이프사이클은 자동 전이되지 않는다 -- 놓친 것이 FAILED 인지 EXPIRED 인지는
     *  시스템이 가릴 수 없으므로 전이는 사용자가 결정한다.
     * </pre>
     */
    @Column(name = "expire_dt")
    @DateTimeFormat(pattern = DateUtils.PTN_DATE)
    @Comment("유효 기한")
    private LocalDate expireDt;

    /* ----- */

    /** 순번 */
    @Column(name = "sort_order", columnDefinition = "INT DEFAULT 1")
    private Integer sortOrder;

    /** Keep을 선택적으로 조직하는 사용자 소유 Project. 미지정 상태는 null이다. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private ProspectiveProjectEntity project;

    /* ----- */

    /**
     * Keep이 선택한 개인 말머리(0..1).
     * 연결은 prefix_content 에 저장하며 Scope content_type 은 {@code PROSPECTIVE_KEEP} 를 쓴다.
     */
    @Embedded
    public PrefixEmbed prefix;

    /**
     * 위임 :: 상태 모듈.
     * Keep 항목은 요구(DEMANDED)·중요(IMPRTC) 여부를 다중 속성으로 보유한다. 진행 단계(lifecycle)와 별개 축이다.
     */
    @Embedded
    public StateEmbed state;

    /** 위임 :: 태그 정보 모듈 */
    @Embedded
    public TagEmbed tag;
}
