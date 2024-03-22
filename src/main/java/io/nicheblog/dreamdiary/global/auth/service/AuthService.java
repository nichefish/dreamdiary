package io.nicheblog.dreamdiary.global.auth.service;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.model.AuthInfo;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserInfoEntity;
import io.nicheblog.dreamdiary.web.repository.user.UserInfoRepository;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

/**
 * AuthService
 * <pre>
 *  Spring Security:: 인증 및 권한 처리 관련 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("authService")
@Log4j2
public class AuthService
        implements UserDetailsService {

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource(name = "userInfoRepository")
    private UserInfoRepository userInfoRepository;

    @Resource
    private HttpServletRequest request;

    /**
     * userId로 계정 + 사용자정보 조회
     * 로그인 등 인증시 Spring Security에서 사용. Spring Security용 사용자 인터페이스(UserDetails) 반환
     */
    @SneakyThrows
    @Override
    public AuthInfo loadUserByUsername(final String userId) throws UsernameNotFoundException {
        Optional<UserEntity> rsUserEntityWrapper = userRepository.findByUserId(userId);
        if (rsUserEntityWrapper.isEmpty()) throw new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.");
        UserEntity rsUserEntity = rsUserEntityWrapper.get();
        // 사용자정보 존재여부 체크
        Integer userInfoNo = rsUserEntity.getUserInfoNo();
        if (userInfoNo != null) {
            UserInfoEntity rsUserInfo = userInfoRepository.findById(userInfoNo).orElse(null);
            rsUserEntity.setUserInfo(rsUserInfo);
        }
        return new AuthInfo(rsUserEntity);
    }

    /**
     * 현재 로그인 중인 사용자 정보 세션에서 조회해서 반환
     */
    public static AuthInfo getAuthenticatedUser() {
        if (RequestContextHolder.getRequestAttributes() == null) return null;
        return (AuthInfo) RequestContextHolder.getRequestAttributes().getAttribute("authInfo", RequestAttributes.SCOPE_SESSION);
    }

    /**
     * 현재 로그인 중인 사용자 정보 Id 세션에서 조회해서 반환
     */
    public static Integer getAuthenticatedUserInfoNo() {
        if (RequestContextHolder.getRequestAttributes() == null) return null;
        AuthInfo authInfo = getAuthenticatedUser();
        assert authInfo != null;
        return (authInfo.getUserInfo() == null) ? null : authInfo.getUserInfo().getUserInfoNo();
    }

    /**
     * 로그인 실패시 실패 카운트 증가
     */
    public Integer applyLgnFailCnt(final String userId) {
        // ID로 사용자 정보 조회
        Optional<UserEntity> userEntityWrapper = userRepository.findByUserId(userId);
        if (userEntityWrapper.isEmpty()) return 0;
        UserEntity userEntity = userEntityWrapper.get();
        // 로그인 실패횟수 조회해서 세팅
        Integer currLgnFailCnt = userEntity.getLgnFailCnt();
        Integer newLgnFailCnt = (currLgnFailCnt == null) ? 1 : currLgnFailCnt + 1;
        userEntity.setLgnFailCnt(newLgnFailCnt);
        // 저장 후 반환된 값 반환
        UserEntity rsltEntity = userRepository.save(userEntity);
        return rsltEntity.getLgnFailCnt();
    }

    /**
     * 계정 잠금 처리
     */
    public void lockAccount(final String userId) {
        // ID로 사용자 정보 조회
        Optional<UserEntity> userEntityWrapper = userRepository.findByUserId(userId);
        UserEntity userEntity = userEntityWrapper.orElseThrow(NullPointerException::new);
        // 계정 잠금 처리
        userEntity.setLockYn("Y");
        userRepository.save(userEntity);
    }

    /**
     * 로그인 성공시 최종 로그인일자 세팅 및 실패 카운트 초기화
     */
    public void setLstLgnDt(final String userId) {
        // ID로 사용자 정보 조회
        Optional<UserEntity> userEntityWrapper = userRepository.findByUserId(userId);
        UserEntity userEntity = userEntityWrapper.orElseThrow(NullPointerException::new);
        // 최종 로그인 날짜 세팅 및 실패 카운터 0으로 세팅
        userEntity.setLstLgnDt(new Date());
        userEntity.setLgnFailCnt(0);
        userRepository.save(userEntity);
    }

    /**
     * 사용자 IP주소 조회 (헤더 조회)
     */
    public String getUserIpAddr() {
        String ipType = "";
        String ipAddr = "";
        for (String s : Constant.IP_HEADERS) {
            ipType = s;
            ipAddr = request.getHeader(ipType);
            if (ipAddr != null) break;
        }
        if (ipAddr == null) {
            ipType = Constant.REMOTE_ADDR;
            ipAddr = request.getRemoteAddr();
        }
        log.info("ipAddr > {}: {}", ipType, ipAddr);
        return ipAddr;
    }

}