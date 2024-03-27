package io.nicheblog.dreamdiary.web.entity.user;

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
@AllArgsConstructor
@RequiredArgsConstructor
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
     * 사용자 번호
     */
    @Column(name = "user_no")
    @Comment("사용자 번호")
    private Integer userNo;

    /**
     * 권한 코드
     */
    @Column(name = "auth_cd")
    @Comment("권한 코드")
    private String authCd;

    /**
     * 삭제 여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "del_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("삭제 여부")
    private String delYn = "N";
}
