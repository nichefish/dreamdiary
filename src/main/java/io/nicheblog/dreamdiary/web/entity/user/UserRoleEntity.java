package io.nicheblog.dreamdiary.web.entity.user;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * UserRoleEntity
 * <pre>
 *  계정 권한 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "USER_ROLE")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(callSuper = true)
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE USER_ROLE SET DEL_YN = 'Y' WHERE AUTH_CD = ?")
public class UserRoleEntity {

    /**
     * 권한코드 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTH_CD")
    @Comment("권한코드 (key)")
    private String authCd;

    /**
     * 권한코드이름
     */
    @Column(name = "AUTH_NM")
    @Comment("권한코드이름")
    private String authNm;

    /**
     * 권한레벨
     */
    @Column(name = "AUTH_LEVEL")
    @Comment("권한레벨")
    private Integer authLevel;

    /**
     * 상위권한코드
     */
    @Column(name = "TOP_AUTH_CD")
    @Comment("상위권한 코드")
    private String topAuthCd;

    /**
     * 상위권한정보
     */
    @ManyToOne
    @JoinColumn(name = "TOP_AUTH_CD", referencedColumnName = "AUTH_CD", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @ToString.Exclude
    @Comment("상위권한 정보")
    private UserRoleEntity topAuthInfo;

    /**
     * 정렬 순서
     */
    @Column(name = "SORT_ORDR")
    @Comment("정렬 순서")
    private Integer sortOrdr;

    /**
     * 사용여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "USE_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("사용여부")
    private String useYn = "N";

    /**
     * 삭제여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "DEL_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제여부")
    private String delYn = "N";
}
