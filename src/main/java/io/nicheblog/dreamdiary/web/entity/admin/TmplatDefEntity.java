package io.nicheblog.dreamdiary.web.entity.admin;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAuditEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbed;
import io.nicheblog.dreamdiary.global.intrfc.entity.embed.StateEmbedModule;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * TmplatDefEntity
 * <pre>
 *  템플릿(사전입력폼) 정의 정보 Entity
 * </pre>
 *
 * @author nichefish
 * @extends BaseAuditEntity
 * @implements StateEmbedModule
 */
@Entity
@Table(name = "tmplat_def")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE tmplat_def SET del_yn = 'Y' WHERE tmplat_def_no = ?")
public class TmplatDefEntity
        extends BaseAuditEntity
        implements StateEmbedModule {

    /** 템플릿 정의 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tmplat_def_no")
    @Comment("템플릿 정의 번호 (PK)")
    private Integer tmplatDefNo;

    /** 템플릿 정의 코드 */
    @Column(name = "tmplat_def_cd", length = 1000)
    @Comment("템플릿 정의 코드")
    private String tmplatDefCd;

    /** 제목 */
    @Column(name = "title", length = 1000)
    @Comment("제목")
    private String title;

    /* ----- */

    /** 상태 관리 모듈 (위임) */
    @Embedded
    public StateEmbed state;
}
