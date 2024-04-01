package io.nicheblog.dreamdiary.web.service.cmm;

import io.nicheblog.dreamdiary.api.jandi.JandiTopic;
import io.nicheblog.dreamdiary.api.jandi.service.JandiApiService;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.auth.util.AuthUtils;
import io.nicheblog.dreamdiary.global.cmm.cd.service.CdService;
import io.nicheblog.dreamdiary.global.cmm.log.ActvtyCtgr;
import io.nicheblog.dreamdiary.global.cmm.log.event.LogSysEvent;
import io.nicheblog.dreamdiary.global.cmm.log.model.LogSysParam;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DatePtn;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.web.SiteUrl;
import io.nicheblog.dreamdiary.web.model.board.BoardPostDto;
import io.nicheblog.dreamdiary.web.model.exptr.reqst.ExptrReqstDto;
import io.nicheblog.dreamdiary.web.model.notice.NoticeDto;
import io.nicheblog.dreamdiary.web.model.schdul.SchdulDto;
import io.nicheblog.dreamdiary.web.model.user.UserListDto;
import io.nicheblog.dreamdiary.web.model.vcatn.papr.VcatnPaprDto;
import io.nicheblog.dreamdiary.web.service.schdul.SchdulService;
import io.nicheblog.dreamdiary.web.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * NotifyService
 * <pre>
 *  알림 관리 서비스 모듈
 *  (중앙통제 Wrapper, 여기저기로 알림 분배)
 * </pre>
 *
 * @author nichefish
 */
@Service("notifyService")
@Log4j2
public class NotifyService {

    @Resource(name = "schdulService")
    private SchdulService schdulService;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "cdService")
    private CdService cdService;

    @Resource(name = "jandiApiService")
    private JandiApiService jandiApiService;

    @Resource
    protected ApplicationEventPublisher publisher;

    /**
     * 공지사항 등록 잔디 알림 메시지 발송
     */
    public String notifyNoticeReg(
            final JandiTopic trgetTopic,
            final NoticeDto result,
            final LogSysParam logParam
    ) throws Exception {
        String jandiResultMsg;
        try {
            // title
            String title = result.getFullTitle();
            // msg
            String msg = "새로운 공지사항이 등록되었습니다.";
            // url
            String url = SiteUrl.DOMAIN + SiteUrl.NOTICE_DTL;
            String param = "postNo=" + result.getPostNo() + "&boardCd=" + result.getContentType() + "&" + Constant.UTM_SOURCE + "=jandi";
            String fullUrl = url + "?" + param;
            // 메세지 발송
            jandiApiService.sendMsg(trgetTopic, msg, title, fullUrl);
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_SUCCESS);
        } catch (Exception e) {
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            logParam.setResult(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.JANDI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        return jandiResultMsg;
    }

    /**
     * 게시판 등록 잔디 알림 메시지 발송
     */
    public String notifyBoardPostReg(
            final JandiTopic trgetTopic,
            final BoardPostDto result,
            final LogSysParam logParam
    ) throws Exception {
        String jandiResultMsg;
        try {
            // title
            String title = result.getTitle();
            // msg
            String msg = "새로운 글이 등록되었습니다.";
            // url
            String url = SiteUrl.DOMAIN + SiteUrl.BOARD_POST_DTL;
            String param = "postNo=" + result.getPostNo() + "&boardCd=" + result.getBoardCd() + "&" + Constant.UTM_SOURCE + "=jandi";
            String fullUrl = url + "?" + param;
            // 메세지 발송
            jandiApiService.sendMsg(trgetTopic, msg, title, fullUrl);
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_SUCCESS);
        } catch (Exception e) {
            logParam.setExceptionInfo(MessageUtils.getExceptionNm(e), e.getMessage());
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        return jandiResultMsg;
    }

    /**
     * 일정 등록 잔디 알림 메시지 발송
     */
    public String notifySchdulReg(
            final JandiTopic trgetTopic,
            final SchdulDto result,
            final LogSysParam logParam
    ) {
        String jandiResultMsg;
        try {
            // title
            String schdulTyNm = cdService.getDtlCdNm(Constant.SCHDUL_CD, result.getSchdulCd());
            String title = "[" + schdulTyNm + "] " + result.getBgnDt() + " / " + result.getSchdulNm();
            String prtcpntStr = result.getPrtcpntListStr();
            if (StringUtils.isNotEmpty(prtcpntStr)) {
                title = "[" + schdulTyNm + "] " + result.getBgnDt() + " / " + prtcpntStr + " : " + result.getSchdulNm();
            }
            // msg
            String msg = "새로운 일정이 등록되었습니다.";
            // url
            String url = SiteUrl.DOMAIN + SiteUrl.SCHDUL_CAL;
            String param = Constant.UTM_SOURCE + "=jandi";
            String fullUrl = url + "?" + param;
            // 메세지 발송
            jandiApiService.sendMsg(trgetTopic, msg, title, fullUrl);
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_SUCCESS);
        } catch (Exception e) {
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            logParam.setResult(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.JANDI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        return jandiResultMsg;
    }

    /**
     * 휴가계획서 등록 잔디 알림 메시지 발송
     */
    public String notifyVcatnPaprReg(
            final JandiTopic trgetTopic,
            final VcatnPaprDto result,
            final LogSysParam logParam
    ) {
        String jandiResultMsg;
        try {
            // title
            String userNm = AuthUtils.getLgnUserNm();
            String title = userNm + " " + result.getTitle();
            // msg
            // String msg = isReg ? "새로운 휴가 계획서가 등록되었습니다." : "휴가 계획서가 수정되었습니다.";
            String msg = "새로운 휴가 계획서가 등록되었습니다.";
            // url
            String url = SiteUrl.DOMAIN + SiteUrl.VCATN_PAPR_DTL;
            String param = "postNo=" + result.getPostNo() + "&boardCd=" + result.getContentType() + "&" + Constant.UTM_SOURCE + "=jandi";
            String fullUrl = url + "?" + param;
            // 메세지 발송
            jandiApiService.sendMsg(trgetTopic, msg, title, fullUrl);
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_SUCCESS);
        } catch (Exception e) {
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            logParam.setResult(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.JANDI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        return jandiResultMsg;
    }

    /**
     * 물품구매/경조사비 신청 등록 잔디 알림 메시지 발송
     */
    public String notifyExptrReqstReg(
            final JandiTopic trgetTopic,
            final ExptrReqstDto result,
            final LogSysParam logParam
    ) {
        String jandiResultMsg;
        try {
            // title
            String title = result.getFullTitle();
            // msg
            String msg = "새로운 요청이 등록되었습니다.";
            // url
            String url = SiteUrl.DOMAIN + SiteUrl.EXPTR_REQST_DTL;
            String param = "postNo=" + result.getPostNo() + "&boardCd=" + result.getContentType() + "&" + Constant.UTM_SOURCE + "=jandi";
            String fullUrl = url + "?" + param;
            // 메세지 발송
            jandiApiService.sendMsg(trgetTopic, msg, title, fullUrl);
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_SUCCESS);
        } catch (Exception e) {
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            logParam.setResult(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.JANDI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        return jandiResultMsg;
    }

    /**
     * 일정 > 생일인 현재 직원에 대하여 알림 발송
     */
    public Boolean notifyCrdtUserBrthdy(
            final LogSysParam logParam
    ) throws Exception {
        // 생일인 직원 목록 조회
        List<UserListDto> brthdyUserList = userService.getBrthdyCrdtUser();
        if (CollectionUtils.isEmpty(brthdyUserList)) return true;
        String jandiResultMsg = "";
        boolean isSuccess = false;
        try {
            JandiTopic trgetTopic = JandiTopic.SCHDUL;
            for (UserListDto user : brthdyUserList) {
                // title
                String title = "[생일] " + user.getUserNm();
                // msg
                String msg = "오늘은 " + user.getUserNm() + "님의 생일입니다.";
                boolean isLunar = "Y".equals(user.getLunarYn());
                String brthdyStr = user.getBrthdy();
                if (isLunar) brthdyStr = DateUtils.ChineseCal.solToLunStr(brthdyStr, DateUtils.PTN_DATE);
                // url
                String url = DateUtils.asStr(brthdyStr, DatePtn.BRTHDY.pattern) + (isLunar ? "음력" : "");
                // 메세지 발송
                isSuccess = jandiApiService.sendMsg(trgetTopic, msg, title, url);
                jandiResultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_JANDI_SUCCESS : MessageUtils.RSLT_JANDI_FAILURE);
            }
            isSuccess = true;
        } catch (Exception e) {
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            logParam.setResult(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.JANDI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        log.info("{}", jandiResultMsg);
        return isSuccess;
    }

    /**
     * 일정 > 매월 첫번째 평일에 경비지출서 작성 알림 발송
     */
    public boolean remindExptrPrsnl(
            final LogSysParam logParam
    ) {
        String jandiResultMsg = "";
        boolean isSuccess = false;
        try {
            boolean isFirstBsnsInCurrMnth = schdulService.isFirstBsnsDayInCurrMnth();
            if (!isFirstBsnsInCurrMnth) return false;
            JandiTopic trgetTopic = JandiTopic.SCHDUL;
            // title
            Integer prevMnth = DateUtils.getPrevMnth();
            String title = "[" + prevMnth + "월 경비지출서 작성 요망]";
            // msg
            String msg = prevMnth + "월이 지났습니다. " + prevMnth + "월분 경비지출서를 작성 마무리해 주세요.";
            // url
            String url = SiteUrl.DOMAIN + SiteUrl.EXPTR_PRSNL_PAPR_LIST;
            String param = Constant.UTM_SOURCE + "=jandi";
            String fullUrl = url + "?" + param;
            // 메세지 발송
            isSuccess = jandiApiService.sendMsg(trgetTopic, msg, title, fullUrl);
            jandiResultMsg = MessageUtils.getMessage(isSuccess ? MessageUtils.RSLT_JANDI_SUCCESS : MessageUtils.RSLT_JANDI_FAILURE);
        } catch (Exception e) {
            jandiResultMsg = MessageUtils.getMessage(MessageUtils.RSLT_JANDI_FAILURE);
            logParam.setResult(false, MessageUtils.getExceptionMsg(e), ActvtyCtgr.JANDI);
            publisher.publishEvent(new LogSysEvent(this, logParam));
        }
        log.info("{}", jandiResultMsg);
        return isSuccess;
    }

}