package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.List;

/**
 * UserEntity
 * <pre>
 *  계정 정보 Entity :: 사용자 정보(UserInfo)를 위임 필드로 가짐
 * </pre>
 *
 * @author nichefish
 * @extends BaseUserEntity
 */
@Entity
@Table(name = "user")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE user SET del_yn = 'Y' WHERE user_no = ?")
public class UserEntity
        extends BaseAtchEntity {

    /** 사용자 번호 (PK) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    @Comment("사용자 번호 (PK)")
    private Integer userNo;

    /** 사용자 아이디 */
    @Column(name = "user_id", length = 20, unique = true)
    @Comment("사용자 아이디")
    private String userId;

    /** 비밀번호 :: 암호화된 비밀번호(64bit)를 저장하기 위해 길이=64이다. */
    @Column(name = "password", length = 64)
    @Comment("비밀번호")
    private String password;

    /** 사용자 권한 정보 */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "user_no")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 권한 정보")
    private List<UserAuthRoleEntity> authList;

    /** 접속가능 IP 정보 (위임) */
    @Embedded
    public UserAcsIpEmbed acsIpInfo;

    /** 표시이름 : 사용자 프로필 정보가 없을 때 표시되는 이름 */
    @Column(name = "nick_nm", length = 50)
    @Comment("표시이름")
    private String nickNm;

    /** 프로필 이미지 URL */
    @Column(name = "profl_img_url", length = 1000)
    @Comment("프로필 이미지 URL")
    private String proflImgUrl;

    /** 계정 설명 (관리자용) */
    @Column(name = "user_dc", length = 1000)
    @Comment("계정 설명")
    private String userDc;

    /** 사용자 프로필 정보 */
    @OneToOne(mappedBy = "user")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 프로필 정보")
    private UserProflEntity userProfl;

    /** 계정 상태 정보 (위임) */
    @Embedded
    public UserStusEmbed acntStus;

    /* ----- */

    /**
     * getter override
     */
    public String getProflImgUrl() {
        if (StringUtils.isEmpty(this.proflImgUrl)) {
            return (Constant.BLANK_AVATAR_URL);
        }
        return this.proflImgUrl;
    }
}