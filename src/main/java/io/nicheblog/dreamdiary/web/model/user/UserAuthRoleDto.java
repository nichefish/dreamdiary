package io.nicheblog.dreamdiary.web.model.user;

import io.nicheblog.dreamdiary.global.auth.entity.AuthRole;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.intrfc.model.BaseAtchDto;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import javax.persistence.*;
import java.util.List;

/**
 * UserDto
 * <pre>
 *  사용자(계정) 정보 Dto
 * </pre>
 *
 * @author nichefish
 * @extends BaseAtchDto
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserAuthRoleDto
        extends BaseAtchDto {

    /**
     * 사용자 권한 번호
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_auth_role_no")
    @Comment("사용자 권한 번호")
    private Integer userAuthRoleNo;

    /**
     * 권한 코드
     */
    private String authCd;

    /**
     * 권한 정보 매핑
     */
    private AuthRole role;
}
