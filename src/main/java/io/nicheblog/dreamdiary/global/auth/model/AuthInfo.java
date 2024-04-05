package io.nicheblog.dreamdiary.global.auth.model;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.web.model.user.profl.UserProflDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    /** 사용자 ID */
    private String userId;
    /** 사용자 PW */
    private String password;
    /** 권한 목록 */
    private List<AuthRoleDto> authList;

    /** * 사용자 이름 */
    private String nickNm;
    /** 사용자 정보 ID */
    private Integer userProflNo;
    /** 프로필 이미지 URL */
    private String proflImgUrl;

    /** 승인 여부 (Y/N) */
    private String cfYn;
    /** 잠금 여부 (Y/N) */
    private String lockedYn;
    /** 접속 IP 사용 여부 (Y/N) */
    private String useAcsIpYn;
    /** 접속 IP 목록 */
    private List<String> acsIpList;
    /** 최종접속일시 */
    private Date lstLgnDt;
    /** 최종비밀번호변경일시 */
    private Date pwChgDt;
    /** 패스워드 리셋 필요 여부 (Y/N) */
    private String needsPwReset;

    /** 사용자 정보 통으로 저장 (일단) */
    private UserProflDto userProfl;

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

    /**
     * 사용자 정보 존재여부 (내부사용자)
     */
    public Boolean getHasuserProfl() {
        return this.userProfl != null && this.userProflNo != null;
    }

    /**
     * 사용자 정보 존재여부 (내부사용자)
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
        return this.authList.stream()
                .map(entity -> {
                    try {
                        return new SimpleGrantedAuthority("ROLE_" + entity.getAuthCd());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
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
