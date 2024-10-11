package io.nicheblog.dreamdiary;

import io.github.cdimascio.dotenv.Dotenv;
import io.nicheblog.dreamdiary.domain._core.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.domain._core.auth.service.AuthService;
import io.nicheblog.dreamdiary.domain._core.log.actvty.ActvtyCtgr;
import io.nicheblog.dreamdiary.domain._core.log.sys.event.LogSysEvent;
import io.nicheblog.dreamdiary.domain._core.log.sys.model.LogSysParam;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.model.LgnPolicyDto;
import io.nicheblog.dreamdiary.domain.admin.lgnPolicy.service.LgnPolicyService;
import io.nicheblog.dreamdiary.domain.user.info.model.UserAuthRoleDto;
import io.nicheblog.dreamdiary.domain.user.info.model.UserDto;
import io.nicheblog.dreamdiary.domain.user.info.service.UserService;
import io.nicheblog.dreamdiary.global.ActiveProfile;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DreamdiaryInitializer
 * <pre>
 *  어플리케이션 초기화 로직 수행 클래스.
 * </pre>
 *
 * @author nichefish
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class DreamdiaryInitializer
        implements CommandLineRunner {

    private final ActiveProfile activeProfile;
    private final AuthService authService;
    private final UserService userService;
    private final LgnPolicyService lgnPolicyService;
    private final ApplicationEventPublisher publisher;

    @Value("${system.init-temp-pw:}")
    public String SYSTEM_INIT_TEMP_PW;

    /**
     * 프로그램 최초 구동시 수행할 로직.
     *
     * @param args - 명령줄에서 전달된 인수
     */
    @Override
    public void run(final String... args) throws Exception {

        log.info("DreamdiaryApplication init... activeProfile: {}", activeProfile.getActive());

        // 시스템 계정 부재시 등록 :: 메소드 분리
        this.regSystemAcntIfEmpty();
        // 로그인 정책 부재시 등록 :: 메소드 분리
        this.regLgnPolicyIfEmpty();
        // 프로필에 따른 .env 로드 :: 메소드 분리
        this.loadDotEnvProperties();

        // 시스템 재기동 로그 적재:: 운영 환경 이외에는 적재하지 않음
        if (activeProfile.isProd()) {
            LogSysParam logParam = new LogSysParam(true, "시스템이 정상적으로 재기동되었습니다.", ActvtyCtgr.SYSTEM);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }

    /**
     * 최초 실행시 사용자가 공백이므로 관리자 계정 자동 등록. (PW 암호화)
     */
    public void regSystemAcntIfEmpty() {

        LogSysParam logParam = new LogSysParam();

        boolean isSuccess = false;
        boolean systemAcntExists = false;
        String rsltMsg = "";
        try {
            try {
                // 시스템계정 존재여부 체크
                authService.loadUserByUsername(Constant.SYSTEM_ACNT);
                systemAcntExists = true;
            } catch (UsernameNotFoundException e) {
                // 시스템 계정 부재시 등록:: 메소드 분리
                isSuccess = this.regSystemAcnt();
                rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 시스템 계정 등록 처리했을 경우 로그 적재
            if (!systemAcntExists) {
                logParam.setResult(isSuccess, rsltMsg);
                publisher.publishEvent(new LogSysEvent(this, logParam));
            }
        }
    }

    /**
     * 시스템 계정 등록.
     * 임의의 고정 패스워드로 생성되었으므로 최초설치 후 직접 비밀번호를 변경해 주어야 한다.
     */
    public boolean regSystemAcnt() throws Exception {

        final AuthRoleEntity authRoleEntityMngr = authService.getAuthRole(Constant.AUTH_MNGR);

        final UserAuthRoleDto userAuthRole = UserAuthRoleDto.builder()
                .authCd(Constant.AUTH_MNGR)
                .role(authRoleEntityMngr)
                .build();

        final UserDto.DTL systemAcnt = UserDto.DTL.builder()
                .nickNm(Constant.SYSTEM_ACNT_NM)
                .userId(Constant.SYSTEM_ACNT)
                .password(SYSTEM_INIT_TEMP_PW)
                .authList(List.of(userAuthRole))
                .regstrId(Constant.SYSTEM_ACNT)
                .build();

        UserDto rslt = userService.regist(systemAcnt);
        return (rslt.getUserNo() != null);
    }

    /**
     * 최초 실행시 로그인 정책이 공백이므로 기본값 자동 등록. (PW 암호화)
     */
    public void regLgnPolicyIfEmpty() {

        LogSysParam logParam = new LogSysParam();

        boolean isSuccess = false;
        boolean lgnPolicyExists = false;
        String rsltMsg = "";
        try {
            // 로그인 정책 존재여부 체크
            LgnPolicyDto rsLgnPolicy = lgnPolicyService.getDtlDto();
            if (rsLgnPolicy != null) {
                lgnPolicyExists = true;
                return;
            }
            // 로그인 정책 부재시 등록:: 메소드 분리
            isSuccess = this.regLgnPolicy();
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            // 로그인 정책 등록 처리했을 경우 로그 적재
            if (!lgnPolicyExists) {
                logParam.setResult(isSuccess, rsltMsg);
                publisher.publishEvent(new LogSysEvent(this, logParam));
            }
        }
    }

    /**
     * 로그인 정책 등록.
     * 임의의 고정 패스워드로 생성되었으므로 최초설치 후 직접 비밀번호를 변경해 주어야 한다.
     */
    public boolean regLgnPolicy() throws Exception {

        final LgnPolicyDto lgnPolicy = LgnPolicyDto.builder()
                .lgnTryLmt(5)
                .pwChgDy(90)
                .lgnLockDy(90)
                .pwForReset(SYSTEM_INIT_TEMP_PW)
                .build();

        return lgnPolicyService.regist(lgnPolicy);
    }

    /**
     * 프로필에 따라 dotEnv 설정 로드.
     */
    private void loadDotEnvProperties() {
        try {
            // 프로필 기반 .env.${profile} 프로퍼티 로드 (속성 없을시:: 기본값 local)
            String profile = System.getProperty("spring.profiles.active", "local");
            this.setDotEnvPropertiesByFileNm(".env");
            this.setDotEnvPropertiesByFileNm(".env." + profile);
        } catch (Exception e) {
            log.error("Failed to load .env file for profile '{}'", System.getProperty("spring.profiles.active"), e);
        }
    }

    /**
     * 지정된 프로퍼티 파일을 읽어 시스템 환경 변수에 추가합니다.
     * @param fileName - 로드할 .env 파일의 이름
     */
    private void setDotEnvPropertiesByFileNm(final String fileName) {
        Dotenv dotenv = Dotenv.configure().filename(fileName).load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        log.info("Loaded {} file successfully.", fileName);
    }
}
