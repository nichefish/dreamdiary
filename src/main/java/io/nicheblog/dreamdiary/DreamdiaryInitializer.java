package io.nicheblog.dreamdiary;

import io.nicheblog.dreamdiary.global.ActiveProfile;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.entity.AuthRoleEntity;
import io.nicheblog.dreamdiary.global.auth.service.AuthService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import io.nicheblog.dreamdiary.web.model.user.UserAuthRoleDto;
import io.nicheblog.dreamdiary.web.model.user.UserDto;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * DreamdiaryInitializer
 * <pre>
 *  어플리케이션 초기화 로직 분리 클래스
 * </pre>
 *
 * @author nichefish
 */
@Component
@Log4j2
public class DreamdiaryInitializer
        implements CommandLineRunner {

    @Resource(name = "activeProfile")
    private ActiveProfile activeProfile;

    @Resource(name = "authService")
    private AuthService authService;
    @Resource(name = "userService")
    private UserService userService;
    @Resource(name = "lgnPolicyService")
    private LgnPolicyService lgnPolicyService;

    @Resource
    private ApplicationEventPublisher publisher;

    public final String INIT_TEMP_PW = "123qwe!QA";

    /**
     * 프로그램 최초 구동시 수행할 로직
     */
    @Override
    public void run(final String... args) throws Exception {

        log.info("DreamdiaryApplication init... activeProfile: {}", activeProfile.getActive());

        // 시스템 계정 부재시 등록
        this.chkSystemAcnt();
        // 로그인 정책 부재시 등록
        this.chkLgnPolicy();

        // 시스템 재기동 로그 적재
        if (activeProfile.isProd()) {
            LogSysParam logParam = new LogSysParam(true, "시스템이 정상적으로 재기동되었습니다.", ActvtyCtgr.SYSTEM);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
    }

    /**
     * 최초 실행시 사용자가 공백이므로 관리자 계정 자동 등록 (PW 암호화)
     */
    public void chkSystemAcnt() {

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
                // 시스템계정 부재시 등록:: 메소드 분리
                isSuccess = this.regSystemAcnt();
                rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
            }
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            if (!systemAcntExists) {
                logParam.setResult(isSuccess, rsltMsg);
                publisher.publishEvent(new LogSysEvent(this, logParam));
            }
        }
    }

    /**
     * 시스템 계정 등록:: 메소드 분리
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
                .password(INIT_TEMP_PW)
                .authList(List.of(userAuthRole))
                .regstrId(Constant.SYSTEM_ACNT)
                .build();

        UserDto rslt = userService.regist(systemAcnt);
        return (rslt.getUserNo() != null);
    }

    /**
     * 최초 실행시 로그인 정책이 공백이므로 기본값 자동 등록 (PW 암호화)
     */
    public void chkLgnPolicy() {

        LogSysParam logParam = new LogSysParam();

        boolean isSuccess = false;
        boolean systemAcntExists = false;
        String rsltMsg = "";
        try {
            // 로그인 정책 존재여부 체크
            LgnPolicyDto rsLgnPolicy = lgnPolicyService.getDtlDto();
            if (rsLgnPolicy != null) {
                systemAcntExists = true;
                return;
            }
            // 로그인 정책 등록:: 메소드 분리
            isSuccess = this.regLgnPolicy();
            rsltMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            rsltMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(e);
        } finally {
            if (!systemAcntExists) {
                logParam.setResult(isSuccess, rsltMsg);
                publisher.publishEvent(new LogSysEvent(this, logParam));
            }
        }
    }

    /**
     * 로그인 정책 등록:: 메소드 분리
     * 임의의 고정 패스워드로 생성되었으므로 최초설치 후 직접 비밀번호를 변경해 주어야 한다.
     */
    public boolean regLgnPolicy() throws Exception {

        final LgnPolicyDto lgnPolicy = LgnPolicyDto.builder()
                .lgnTryLmt(5)
                .pwChgDy(90)
                .lgnLockDy(90)
                .pwForReset(INIT_TEMP_PW)
                .build();

        return lgnPolicyService.regist(lgnPolicy);
    }
}
