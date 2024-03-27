package io.nicheblog.dreamdiary;

import io.nicheblog.NicheblogBasePackage;
import io.nicheblog.dreamdiary.global.ActiveProfile;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.web.entity.admin.LgnPolicyEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserEntity;
import io.nicheblog.dreamdiary.web.entity.user.UserStusInfo;
import io.nicheblog.dreamdiary.web.model.admin.LgnPolicyDto;
import io.nicheblog.dreamdiary.web.repository.admin.LgnPolicyRepository;
import io.nicheblog.dreamdiary.web.repository.user.UserRepository;
import io.nicheblog.dreamdiary.web.service.admin.LgnPolicyService;
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

    @Resource(name = "dreamdiaryInitializer")
    private DreamdiaryInitializer initializer;

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

        // 시스템 계정 부재시 등록
        initializer.chkSystemAcnt();
        // 로그인 정책 부재시 등록
        initializer.chkLgnPolicy();

        // 시스템 재기동 로그 적재
        if (!activeProfile.isProd()) return;
        LogSysParam logParam = new LogSysParam(true, "시스템이 정상적으로 재기동되었습니다.");
        publisher.publishEvent(new LogSysEvent(this, logParam));
    }
}
