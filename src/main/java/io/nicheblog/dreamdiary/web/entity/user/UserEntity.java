package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.intrfc.entity.BaseAtchEntity;
import io.nicheblog.dreamdiary.web.entity.user.profl.UserProflEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
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
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@Where(clause = "del_yn='N'")
@SQLDelete(sql = "UPDATE user SET del_yn = 'Y' WHERE user_no = ?")
public class UserEntity
        extends BaseAtchEntity {

    /**
     * 사용자 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    @Comment("사용자 번호")
    private Integer userNo;

    /**
     * 사용자 아이디
     */
    @Column(name = "user_id", length = 20, unique = true)
    @Comment("사용자 아이디")
    private String userId;

    /**
     * 비밀번호 :: 암호화된 비밀번호(64bit)를 저장하기 위해 길이=64이다.
     */
    @Column(name = "password", length = 64)
    @Comment("비밀번호")
    private String password;

    /**
     * 사용자 프로필 정보
     */
    @OneToOne(mappedBy = "user")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 프로필 정보")
    private UserProflEntity userProfl;

    /**
     * 표시이름 : 사용자 프로필 정보가 없을 때 표시되는 이름
     */
    @Column(name = "nick_nm", length = 50)
    @Comment("표시이름")
    private String nickNm;

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "profl_img_url", length = 1000)
    @Comment("프로필 이미지 URL")
    private String proflImgUrl;

    /**
     * 접속 IP 사용 여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "use_acs_ip_yn", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("접속 IP 사용 여부")
    private String useAcsIpYn = "N";

    /**
     * 접속 IP 정보
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "user_no")
    @Fetch(FetchMode.SELECT)
    @OrderBy("acsIp ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("접속 IP 정보")
    private List<UserAcsIpEntity> acsIpInfoList;

    /**
     * 계정 설명 (관리자용)
     */
    @Column(name = "user_dc", length = 1000)
    @Comment("계정 설명")
    private String userDc;

    /**
     * 계정 상태 정보 (위임)
     */
    @Embedded
    public UserStusInfo acntStus;

    /* ----- */

    /**
     * getter override
     */
    public String getProflImgUrl() {
        if (StringUtils.isEmpty(this.proflImgUrl)) {
            return ("/metronic/assets/media/avatar_blank.png");
        }
        return this.proflImgUrl;
    }

    /**
     * 서브엔티티 List 처리를 위한 Setter (override)
     * 한 번 Entity가 생성된 이후부터는 new List를 할당하면 안 되고 계속 JPA 이력이 추적되어야 한다.
     */
    public void setAcsIpInfoList(final List<UserAcsIpEntity> acsIpInfoList) {
        if (CollectionUtils.isEmpty(acsIpInfoList)) return;
        if (this.acsIpInfoList == null) {
            this.acsIpInfoList = acsIpInfoList;
        } else {
            this.acsIpInfoList.clear();
            this.acsIpInfoList.addAll(acsIpInfoList);
        }
    }

    /**
     * 접속가능IP 목록을 문자열로 변환하여 반환
     */
    public String getAcsIpInfoListStr() {
        if (this.acsIpInfoList == null) return null;
        List<String> acsIpInfoList = new ArrayList<>();
        for (UserAcsIpEntity ip : this.getAcsIpInfoList()) {
            acsIpInfoList.add(ip.getAcsIp());
        }
        return StringUtils.join(acsIpInfoList, ", ");
    }
}