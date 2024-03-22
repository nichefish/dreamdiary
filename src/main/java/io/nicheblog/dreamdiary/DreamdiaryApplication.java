package io.nicheblog.dreamdiary;

import io.nicheblog.NicheblogBasePackage;
import io.nicheblog.dreamdiary.global.ActiveProfile;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * DreamdiaryApplication
 * <pre>
 *  Spring Boot Application Runner
 * </pre>
 *
 * @author nichefish
 */
@SpringBootApplication(scanBasePackageClasses = { NicheblogBasePackage.class })
@EnableCaching
@EnableScheduling
@Log4j2
public class DreamdiaryApplication
        implements CommandLineRunner {

    @Resource(name = "activeProfile")
    private ActiveProfile activeProfile;

    @Resource(name = "passwordEncoder")
    private PasswordEncoder passwordEncoder;

    @Resource(name = "userRepository")
    private UserRepository userRepository;

    @Resource
    protected ApplicationEventPublisher publisher;

    /**
     * main
     */
    public static void main(final String[] args) {
        SpringApplication.run(DreamdiaryApplication.class, args);
    }

    /**
     * 프로그램 최초 구동시 수행할 로직
     */
    @Override
    public void run(final String... args) throws Exception {

        log.info("activeProfile: {}", activeProfile.getActive());

        // 시스템계정 등록
        this.chckSystemAcnt();

        if (!activeProfile.isProd()) return;

        LogSysParam logParam = new LogSysParam(true, "시스템이 정상적으로 재기동되었습니다.");
        publisher.publishEvent(new LogSysEvent(this, logParam));
    }

    /**
     * 사용자가 공백이므로 최초 실행시 관리자 자동 등록 (PW 암호화)
     */
    public void chckSystemAcnt() {

        LogSysParam logParam = new LogSysParam();

        boolean isSuccess = false;
        boolean systemAcntExists = false;
        String resultMsg = "";
        try {
            // 시스템계정 존재여부 체크
            Optional<UserEntity> rsSystemAccountInfo = userRepository.findByUserId(Constant.SYSTEM_ACNT);
            if (rsSystemAccountInfo.isPresent()) {
                systemAcntExists = true;
                return;
            }
            // 시스템계정 등록:: 메소드 분리
            isSuccess = this.regSystemAcnt();
            resultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_SUCCESS : MessageUtils.RSLT_FAILURE);
        } catch (Exception e) {
            isSuccess = false;
            resultMsg = MessageUtils.getExceptionMsg(e);
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
        } finally {
            if (!systemAcntExists) {
                logParam.setResult(isSuccess, resultMsg);
                publisher.publishEvent(new LogSysEvent(this, logParam));
            }
        }

    }

    /**
     * 시스템 계정 등록:: 메소드 분리
     * 임의의 고정 패스워드로 생성되었으므로 최초설치 후 직접 비밀번호를 변경해 주어야 한다.
     */
    public boolean regSystemAcnt() {
        final String initTempPw = "123qwe!QA";

        final UserEntity system = UserEntity.builder()
                .userNo(1)
                .authCd(Constant.AUTH_MNGR)
                .nickNm(Constant.SYSTEM_ACNT_NM)
                .userId(Constant.SYSTEM_ACNT)
                .userPw(passwordEncoder.encode(initTempPw))
                .regstrId(Constant.SYSTEM_ACNT)
                .build();
        UserEntity rsltEntity = userRepository.saveAndFlush(system);
        return (rsltEntity.getUserNo() != null);
    }
}
