package io.nicheblog.dreamdiary.feature.prospective.project.entity;

import io.nicheblog.dreamdiary.auth.intrfc.entity.BaseAuditEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * ProspectiveProjectEntity
 * <pre>
 *  실행 약속 없이 미래의 가능성을 하나의 지속적인 맥락으로 조직하는 Project.
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "prospective_project")
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE prospective_project SET deleted_at = NOW() WHERE id = ?")
public class ProspectiveProjectEntity extends BaseAuditEntity {

    /** Project ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("Prospective Project ID")
    private Integer id;

    /** 사용자에게 표시할 Project 이름 */
    @Column(name = "title", nullable = false, length = 200)
    @Comment("Project 이름")
    private String title;

    /** Project가 묶는 장기 맥락에 대한 선택적 설명 */
    @Column(name = "description", columnDefinition = "LONGTEXT")
    @Comment("Project 설명")
    private String description;

    /** 사용자 Project 목록의 전역 순번 */
    @Column(name = "sort_order", nullable = false)
    @Comment("사용자별 Project 순번")
    private Integer sortOrder;
}
