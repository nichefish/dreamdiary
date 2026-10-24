package io.nicheblog.dreamdiary.feature.attachable.prefix.entity;

import io.nicheblog.dreamdiary.auth.intrfc.entity.BaseAuditEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import io.nicheblog.dreamdiary.global.type.TextClass;

import javax.persistence.*;

/**
 * 하나의 Prefix Scope에 속하는 평면 말머리 영속 엔티티.
 *
 * @author nichefish
 */
@Entity
@Table(name = "prefix")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
public class PrefixEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /** 말머리 목록의 소유·선택 경계 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scope_id", nullable = false)
    private PrefixScopeEntity scope;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * 표시 색 의미 코드.
     * 태그 프로필({@code tag_profile.text_class})과 같은 {@link TextClass} 어휘를 공유하므로,
     * hex 를 저장하지 않고 의미 코드만 저장한다. 렌더 클래스는 {@link TextClass#toCssClass()} 가 해석한다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "text_class_cd", length = 20)
    private TextClass textClass;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Builder.Default
    @Column(name = "active_yn", nullable = false, length = 1)
    private String activeYn = "Y";
}
