package io.nicheblog.dreamdiary.global.cmm.log.service;

import io.nicheblog.dreamdiary.global.ActiveProfile;
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

/**
 * LogService
 * <pre>
 *  로그 적재 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseReadonlyService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service
@Log4j2
public class LogService {

    private final LogMapstruct logMapstruct = LogMapstruct.INSTANCE;

    @Resource(name = "logActvtyRepository")
    private LogActvtyRepository logActvtyRepository;
    @Resource(name = "logSysRepository")
    private LogSysRepository logSysRepository;
    @Resource(name = "activeProfile")
    ActiveProfile activeProfile;

    private final Boolean LOG_DISABLED = !activeProfile.isProd();

    /**
     * 활동 로그 등록 (로그인 상태)
     */
    @SneakyThrows
    public Boolean regLogActvty(final LogActvtyParam logParam) throws Exception {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (LOG_DISABLED) return true;

        LogActvtyEntity logActvty = logMapstruct.toEntity(logParam);

        log.info("isSuccess: {}, rsltMsg: {}", logParam.getRslt(), logParam.getRsltMsg());
        LogActvtyEntity rslt = logActvtyRepository.save(logActvty);

        return rslt.getLogActvtyNo() != null;
    }

    /**
     * 활동 로그 등록 (비로그인 상태)
     */
    // @SneakyThrows
    public Boolean regLogAnonActvty(final LogActvtyParam logParam) throws Exception {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (LOG_DISABLED) return true;

        // TODO: 막상 분리해놓고 보니 차이가 없네? 일단 분리 상태 유지하기
        LogActvtyEntity logActvty = logMapstruct.toEntity(logParam);
        logActvty.setUserId(logParam.getUserId());
        logActvty.setRslt(logParam.getRslt());               // 작업 결과
        logActvty.setRsltMsg(logParam.getRsltMsg());            // 작업 결과 메세지
        LogActvtyEntity rslt = logActvtyRepository.save(logActvty);

        log.info("isSuccess: {}, rsltMsg: {}", logParam.getRslt(), logParam.getRsltMsg());
        return rslt.getLogActvtyNo() != null;
    }

    /**
     * 활동 로그 등록 (비로그인 상태)
     */
    // @SneakyThrows
    public Boolean regLogAnonActvty(final String userId, final String rsltMsg, boolean isSuccess) {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (LOG_DISABLED) return true;

        LogActvtyEntity logActvty = new LogActvtyEntity();
        logActvty.setUserId(userId);
        logActvty.setRslt(isSuccess);               // 작업 결과
        logActvty.setRsltMsg(rsltMsg);            // 작업 결과 메세지
        LogActvtyEntity rslt = logActvtyRepository.save(logActvty);

        log.info("isSuccess: {}, rsltMsg: {}", isSuccess, rsltMsg);
        return rslt.getLogActvtyNo() != null;
    }

    // @SneakyThrows
    public Boolean regSysActvty(final LogSysParam logParam) throws Exception {

        // 로컬 또는 개발 접속은 로그 남기지 않음
        if (LOG_DISABLED) return true;

        LogSysEntity logActvty = logMapstruct.toEntity(logParam);

        log.info("isSuccess: {}, rsltMsg: {}", logParam.getRslt(), logParam.getRsltMsg());
        LogSysEntity rslt = logSysRepository.save(logActvty);

        return rslt.getLogSysNo() != null;
    }
}
