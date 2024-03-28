package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRole;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * UserAuthRoleEntity
 * <pre>
 *  사용자 권한 Entity
 * </pre>
 *
 * @author nichefish
 */
@Entity
@Table(name = "user_auth_role")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@RequiredArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE user_auth_role SET del_yn = 'Y' WHERE user_auth_role_no = ?")
public class UserAuthRoleEntity {

    /**
     * 사용자 권한 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_auth_role_no")
    @Comment("사용자 권한 번호")
    private Integer userAuthRoleNo;

    /**
     * 사용자 정보
     */
    @ManyToOne
    @JoinColumn(name = "user_no", referencedColumnName = "user_no", insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 정보")
    private UserEntity user;

    /**
     * 권한 코드
     */
    @Column(name = "auth_cd")
    @Comment("권한 코드")
    private String authCd;

    /**
     * 권한 정보 매핑
     */
    @ManyToOne
    @JoinColumn(name = "auth_cd", referencedColumnName = "auth_cd", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("작업자 정보")
    private AuthRole roleInfo;

    /**
     * 삭제 여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "del_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제 여부")
    private String delYn = "N";
}
