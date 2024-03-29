package io.nicheblog.dreamdiary.global.cmm.log.service;

import io.nicheblog.dreamdiary.global.ActiveProfile;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.service.AuthService;
import io.nicheblog.dreamdiary.global.cmm.log.entity.LogActvtyEntity;
import io.nicheblog.dreamdiary.global.cmm.log.entity.LogSysEntity;
import io.nicheblog.dreamdiary.global.cmm.log.mapstruct.LogMapstruct;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogActvtyParam;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.cmm.log.repository.LogActvtyRepository;
import io.nicheblog.dreamdiary.global.cmm.log.repository.LogSysRepository;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * LogService
 * <pre>
 *  로그 적재 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("logService")
@Log4j2
public class LogService {

    private final LogMapstruct logMapstruct = LogMapstruct.INSTANCE;

    @Resource(name = "logActvtyRepository")
    private LogActvtyRepository logActvtyRepository;

    @Resource(name = "logSysRepository")
    private LogSysRepository logSysRepository;

    @Resource(name = "authService")
    private AuthService authService;

    @Resource
    private HttpServletRequest request;

    @Resource(name = "activeProfile")
    ActiveProfile activeProfile;

    /**
     * 활동 로그 등록 (로그인 상태)
     */
    @SneakyThrows
    public void regLogActvty(final LogActvtyParam logParam) {
        String logCn = logParam.getCn();
        this.regLogActvty(logParam, logCn);
    }
    @SneakyThrows
    public void regLogActvty(
            final LogActvtyParam logParam,
            final String logCn
    ) {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (!activeProfile.isProd()) return;

        LogActvtyEntity logActvty = logMapstruct.toEntity(logParam);
        logActvty.setUrl(request.getServletPath());         // 작업 url
        logActvty.setParam(request.getQueryString());       // 작업 파라미터
        logActvty.setReferer(request.getHeader(Constant.REFERER));      // 리퍼러
        logActvty.setIpAddr(authService.getUserIpAddr());       // 작업 IP
        logActvty.setCn(logCn);     // 작업 내용

        log.info("isSuccess: {}, resultMsg: {}", logParam.getIsSuccess(), logParam.getResultMsg());
        logActvtyRepository.save(logActvty);
    }

    /**
     * 활동 로그 등록 (비로그인 상태)
     */
    @SneakyThrows
    public void regLogAnonActvty(
            final LogActvtyParam logParam
    ) {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (!activeProfile.isProd()) return;

        // TODO: 막상 분리해놓고 보니 차이가 없네? 일단 분리 상태 유지하기
        this.regLogActvty(logParam);
    }

    /**
     * 활동 로그 등록 (비로그인 상태)
     */
    @SneakyThrows
    public void regLogAnonActvty(
            final String userId,
            final String resultMsg,
            boolean isSuccess
    ) {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (!activeProfile.isProd()) return;

        LogActvtyEntity logActvty = new LogActvtyEntity();
        logActvty.setLogUserId(userId);
        logActvty.setUrl(request.getServletPath());     // 작업 url
        logActvty.setParam(request.getQueryString());       // 작업 파라미터
        logActvty.setReferer(request.getHeader(Constant.REFERER));  // 리퍼러
        logActvty.setIpAddr(authService.getUserIpAddr());       // 작업 IP
        logActvty.setRslt(isSuccess);               // 작업 결과
        logActvty.setRsltMsg(resultMsg);            // 작업 결과 메세지
        logActvtyRepository.save(logActvty);

        log.info("isSuccess: {}, resultMsg: {}", isSuccess, resultMsg);
    }

    @SneakyThrows
    public void regSysActvty(
            final LogSysParam logParam
    ) {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (!activeProfile.isProd()) return;

        LogSysEntity logActvty = logMapstruct.toEntity(logParam);

        log.info("isSuccess: {}, resultMsg: {}", logParam.getIsSuccess(), logParam.getResultMsg());
        logSysRepository.save(logActvty);
    }
}
