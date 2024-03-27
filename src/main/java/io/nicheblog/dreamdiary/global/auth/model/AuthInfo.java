package io.nicheblog.dreamdiary.global.auth.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.entity.user.UserAcsIpEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * AuthInfo
 * <pre>
 *  Spring Security:: 사용자 정보 Dto (UserDetails 구현)
 * </pre>
 *
 * @author nichefish
 * @implements  UserDetails
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"userId"}, callSuper = false)
@Log4j2
public class AuthInfo
        implements UserDetails {

    /**
     * 사용자 ID
     */
    private String userId;
    /**
     * 사용자 정보 ID
     */
    private Integer userProflNo;
    /**
     * 사용자 PW
     */
    private String password;
    /**
     * 사용자 이름
     */
    private String nickNm;
    /**
     * 권한코드
     */
    private String authCd;
    /**
     * 권한이름
     */
    private String authNm;
    /**
     * 승인여부
     */
    private String cfYn;
    /**
     * 잠금여부
     */
    private String lockedYn;
    /**
     * 접속 IP 사용 여부
     */
    private String useAcsIpYn;
    /**
     * 접속 IP 목록
     */
    private List<String> acsIpList;
    /**
     * 최종접속일시
     */
    private Date lstLgnDt;
    /**
     * 최종비밀번호변경일시
     */
    private Date pwChgDt;
    /**
     * 패스워드 리셋 필요여부
     */
    private String needsPwReset;
    /**
     * 프로필 이미지 URL
     */
    private String proflImgUrl;

    /**
     * 사용자 정보 통으로 저장 (일단)
     */
    private UserProflDto userProfl;

    /* ----- */

    /**
     * 생성자
     */
    public AuthInfo(final UserEntity userEntity) throws Exception {
        if (userEntity != null) {
            this.nickNm = userEntity.getNickNm();
            this.userId = userEntity.getUserId();
            this.password = userEntity.getPassword();
            // TODO: 권한
            this.proflImgUrl = userEntity.getProflImgUrl();
            // 사용자정보 세팅
            // UserProflEntity userProflEntity = userEntity.getUserProfl();
            // if (userProflEntity != null && userProflEntity.getUserProflNo() != null) {
            //     this.userProfl = UserInfoMapstruct.INSTANCE.toDto(userProflEntity);
            //     this.userProflNo = userProfl.getUserProflNo();
            // }

            // 계정 상태 세팅
            this.cfYn = userEntity.acntStus.getCfYn();
            this.lockedYn = userEntity.acntStus.getLockedYn();
            this.useAcsIpYn = userEntity.getUseAcsIpYn();
            if ("Y".equals(this.useAcsIpYn)) {
                List<UserAcsIpEntity> acsIpInfoList = userEntity.getAcsIpInfoList();
                if (CollectionUtils.isNotEmpty(acsIpInfoList)) {
                    acsIpList = new ArrayList<>();
                    for (UserAcsIpEntity acsIp : acsIpInfoList) {
                        acsIpList.add(acsIp.getAcsIp());
                    }
                    this.setAcsIpList(acsIpList);
                }
            }
            // 최종접속일 또는 등록일
            this.lstLgnDt = Optional.ofNullable(userEntity.acntStus.getLstLgnDt())
                                    .orElse(userEntity.getRegDt());
            // 최종비밀번호변경일 또는 등록일
            this.pwChgDt = Optional.ofNullable(userEntity.acntStus.getPwChgDt())
                                   .orElse(userEntity.getRegDt());
            // 패스워드 리셋 필요여부
            this.needsPwReset = userEntity.acntStus.getNeedsPwReset();
        }
    }

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
     * 사용자정보 존재여부 (내부사용자)
     */
    public Boolean getHasuserProfl() {
        return this.userProfl != null && this.userProflNo != null;
    }

    /**
     * 사용자정보 존재여부 (내부사용자)
     */
    public Boolean getHasEcnyDt() {
        return this.userProfl != null && this.userProfl.getEcnyDt() != null;
    }

    /**
     * 관리자 여부
     */
    public Boolean getIsMngr() {
        return this.hasAuthority(Constant.ROLE_MNGR);
    }

    /**
     * 개발자 여부
     */
    public Boolean getIsDev() {
        return this.hasAuthority(Constant.ROLE_DEV);
    }

    /**
     * 계정 권한 처리
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auth = new ArrayList<>();

        String authCd = this.getAuthCd();
        if (Constant.AUTH_MNGR.equals(authCd)) {
            auth.add(new SimpleGrantedAuthority(Constant.ROLE_MNGR));
            auth.add(new SimpleGrantedAuthority(Constant.ROLE_USER));
        } else if (Constant.AUTH_USER.equals(authCd)) {
            auth.add(new SimpleGrantedAuthority(Constant.ROLE_USER));
        }

        // 개발계정 권한 부여
        if (Constant.DEV_ACNT.equals(this.userId)) {
            auth.add(new SimpleGrantedAuthority(Constant.ROLE_DEV));
            auth.add(new SimpleGrantedAuthority(Constant.ROLE_MNGR));
            auth.add(new SimpleGrantedAuthority(Constant.ROLE_USER));
        }

        return auth;
    }

    /**
     * 특정 권한 보유 여부
     */
    public boolean hasAuthority(final String roleStr) {
        for (GrantedAuthority grantedAuthority : this.getAuthorities()) {
            if (roleStr.equals(grantedAuthority.getAuthority())) return true;
        }
        return false;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    /**
     * 계정 잠금여부 체크
     */
    @Override
    public boolean isAccountNonLocked() {
        return !"Y".equals(this.getLockedYn());
    }

    /**
     * 계정 정지여부 체크
     */
    @Override
    public boolean isEnabled() {
        return !"Y".equals(this.getCfYn());
    }

    /**
     * 계정 만료여부 체크 (*not used yet*)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 비밀번호 만료여부 체크 (*not used yet*)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
