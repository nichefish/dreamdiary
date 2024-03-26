package io.nicheblog.dreamdiary.web.entity.user;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.cd.entity.DtlCdEntity;
import io.nicheblog.dreamdiary.global.intrfc.entity.BaseUserEntity;
import io.nicheblog.dreamdiary.global.util.DateUtils;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
@Table(name = "USER")
@DynamicInsert      // null인 값은 (null로 insert하는 대신) insert에서 제외
@Getter
@Setter
@SuperBuilder(toBuilder=true)
@AllArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = {"userInfo"}, callSuper = true)
@Where(clause = "DEL_YN='N'")
@SQLDelete(sql = "UPDATE USER SET DEL_YN = 'Y' WHERE USER_NO = ?")
public class UserEntity
        extends BaseUserEntity {

    /**
     * 사용자 번호 (PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_NO")
    @Comment("사용자 번호")
    private Integer userNo;

    /**
     * 사용자 정보 번호 (위임)
     */
    @Column(name = "USER_INFO_NO")
    @Comment("사용자 정보 번호")
    private Integer userInfoNo;

    /**
     * 사용자 정보
     */
    @OneToOne
    @JoinColumn(name = "USER_INFO_NO", referencedColumnName = "USER_INFO_NO", insertable = false, updatable = false)
    @Fetch(FetchMode.SELECT)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("사용자 정보")
    private UserInfoEntity userInfo;

    /**
     * 사용자 아이디
     */
    @Column(name = "USER_ID", length = 20, unique = true)
    @Comment("사용자 아이디")
    private String userId;

    /**
     * 비밀번호
     */
    // 암호화된 비밀번호(64bit)를 저장하기 위해 길이=64이다.
    @Column(name = "USER_PW", length = 64)
    @Comment("비밀번호")
    private String userPw;

    /**
     * 표시이름 : 사용자 정보가 없을때 표시되는 이름
     */
    @Column(name = "NICK_NM", length = 50)
    @Comment("표시이름")
    private String nickNm;

    /**
     * 권한코드
     */
    @Column(name = "AUTH_CD", length = 20)
    @Comment("권한코드")
    private String authCd;

    /**
     * 권한코드 정보 (복합키 조인)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "\'" + Constant.AUTH_CD + "\'", referencedColumnName = "CL_CD")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "AUTH_CD", referencedColumnName = "DTL_CD", insertable = false, updatable = false))
    })
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("권한코드 정보")
    private DtlCdEntity authCdInfo;

    /**
     * 권한 정보
     */
    @ManyToOne
    @JoinColumn(name = "AUTH_CD", referencedColumnName = "AUTH_CD", insertable = false, updatable = false)
    @Fetch(value = FetchMode.JOIN)
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("권한 정보")
    private UserRoleEntity roleInfo;

    /**
     * 접속 IP 사용여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "ACS_IP_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("접속 IP 사용여부")
    private String acsIpYn = "N";

    /**
     * 접속 IP 정보
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "USER_NO")
    @Fetch(FetchMode.SELECT)
    @OrderBy("acsIp ASC")
    @NotFound(action = NotFoundAction.IGNORE)
    @Comment("접속 IP 정보")
    private List<UserAcsIpEntity> acsIpInfoList;

    /**
     * 마지막 로그인 일시
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "LST_LGN_DT")
    @Comment("마지막 로그인 일시")
    private Date lstLgnDt;

    /**
     * 로그인 실패 횟수
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "LGN_FAIL_CNT", columnDefinition = "INT DEFAULT 0")
    @Comment("로그인 실패 횟수")
    private Integer lgnFailCnt = 0;

    /**
     * 패스워드 변경일시
     */
    @DateTimeFormat(pattern = DateUtils.PTN_DATETIME)
    @Column(name = "PW_CHG_DT")
    @Comment("패스워드 변경일시")
    private Date pwChgDt;

    /**
     * 퇴사여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "RETIRE_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("퇴사여부")
    private String retireYn = "N";

    /**
     * 계정 설명 (관리자용)
     */
    @Column(name = "USER_DC", length = 1000)
    @Comment("계정 설명")
    private String userDc;

    /**
     * 패스워드 리셋 필요여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "NEEDS_PW_RESET", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("패스워드 리셋 필요여부")
    private String needsPwReset = "N";

    /**
     * 장기 미로그인 패스 체크 여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "DORMANT_BYPASS_YN", length = 1, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Comment("장기 미로그인 패스 체크 여부")
    private String dormantBypassYn = "N";

    /**
     * 프로필 이미지 URL
     */
    @Column(name = "PROFL_IMG_URL", length = 1000)
    @Comment("프로필 이미지 URL")
    private String proflImgUrl;

    /**
     * 본인신청여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "REQST_YN", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("본인신청여부")
    private String reqstYn = "Y";

    /**
     * 승인여부
     */
    @Builder.Default        // Builder 사용시 초기값 세팅하도록 설정
    @Column(name = "CF_YN", length = 1, columnDefinition = "CHAR DEFAULT 'Y'")
    @Comment("승인여부")
    private String cfYn = "Y";

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