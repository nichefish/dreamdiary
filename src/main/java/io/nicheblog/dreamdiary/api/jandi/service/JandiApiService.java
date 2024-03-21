package io.nicheblog.dreamdiary.api.jandi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nicheblog.dreamdiary.api.ApiUrl;
import io.nicheblog.dreamdiary.api.jandi.JandiTopic;
import io.nicheblog.dreamdiary.api.jandi.model.*;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.config.HttpClientConfig;
import io.nicheblog.dreamdiary.global.util.JsonRestTemplate;
import io.nicheblog.dreamdiary.global.util.MessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * JandiApiService
 * <pre>
 *  API:: JANDI:: (incoming/outgoing) webhook 서비스 모듈
 * </pre>
 *
 * @author nichefish
 */
@Service("jandiApiService")
@Log4j2
public class JandiApiService {

    public static String TEAM_JANDI = "/25882573";


    /**
     * JANDI incoming webhook:: 공지사항 URL
     */
    public final String URL_JANDI_NOTICE = ApiUrl.JANDI_CONNECT_WH + TEAM_JANDI + "/d0030203b8b6598c9fad707a601b703c";
    /**
     * JANDI incoming webhook:: 휴가신청 URL
     */
    public final String URL_JANDI_VCATN = ApiUrl.JANDI_CONNECT_WH + TEAM_JANDI + "/646423922d134c103e2ef51658ef1abe";
    /**
     * JANDI incoming webhook:: 일정관리 URL
     */
    public final String URL_JANDI_SCHDUL = ApiUrl.JANDI_CONNECT_WH + TEAM_JANDI + "/02dd12a3fa93b00b3bfe4b5c9fff5412";
    /**
     * JANDI incoming webhook:: 물품구매/경조사비 신청 URL
     */
    public final String URL_JANDI_EXPTR_REQST = ApiUrl.JANDI_CONNECT_WH + TEAM_JANDI + "/cd096310c400783aeef27771ed4f95b3";
    /**
     * JANDI incoming webhook:: 테스트
     */
    public final String URL_JANDI_TEST = ApiUrl.JANDI_CONNECT_WH + TEAM_JANDI + "/a7119b915b0b1e8dd07911ef365e9722";


    /* JANDI outgoing webhook (jandi -> intranet) Tokens (현재 미사용중) */
    /**
     * 공지사항 TOKEN
     */
    public final String TOKEN_NOTICE = "5ecca066b82b213d750db40103e19a91";
    /**
     * 휴가신청 TOKEN
     */
    public final String TOKEN_VCATN = "2f5917dedc33d6a827475fa9b376a734";

    /**
     * API:: JANDI:: 잔디로 incoming webhook 전송
     *
     * @param jandiParam
     */
    public Boolean sendMsg(final JandiParam jandiParam) throws Exception {
        JandiTopic topic = jandiParam.getTrgetTopic();
        return this.sendMsg(topic, jandiParam.getMsg());
    }

    /**
     * API:: JANDI:: 잔디로 incoming webhook 전송
     *
     * @param trgetTopic: 메세지 발송 대상 토픽
     * @param msg: 메세지 (메세지로 기본 JandiMsg 객체 생성하여 주입)
     */
    public Boolean sendMsg(final JandiTopic trgetTopic, final String msg) throws Exception {
        JandiApiSndMsgDto jandiMsg = new JandiApiSndMsgDto(msg);
        return this.sendMsg(trgetTopic, jandiMsg);
    }

    /**
     * API:: JANDI:: 잔디로 incoming webhook 전송
     *
     * @param trgetTopic: 메세지 발송 대상 토픽
     * @param jandiMsg: 외부에서 JandiMsg 객체를 직접 주입
     */
    public Boolean sendMsg(
            final JandiTopic trgetTopic,
            final JandiApiSndMsgDto jandiMsg
    ) throws Exception {

        // https로 연결시 SSL/TLS 인증서 필요 :: SSL인증서 무시하도록 생성
        // POST 전송시 URI_ENCODING 끄도록 생성자에서 설정
        JsonRestTemplate restTemplate = new JsonRestTemplate(HttpClientConfig.trustRequestFactory(), Constant.URL_ENC_FALSE);

        // parameter로 요청 URL 세팅 (메소드 분리)
        String postUrl = this.setRequestUrlParam(trgetTopic);
        log.debug("postUrl: {}", postUrl);
        if (postUrl == null) return false;
        // 헤더 세팅 :: 메소드 분리
        HttpEntity<JandiApiSndMsgDto> httpEntity = this.setRequestHeader(jandiMsg);
        log.debug("httpEntity: {}", httpEntity);

        ResponseEntity<JandiApiRespnsDto> response = restTemplate.exchange(postUrl, HttpMethod.POST, httpEntity, JandiApiRespnsDto.class);
        log.debug(response);

        return true;
    }

    /**
     * API:: JANDI:: 잔디로 incoming webhook 전송
     *
     * @param trgetTopic: 메세지 발송 대상 토픽
     * @param msg: 메세지
     * @param title: 글제목 (connectorInfo title)
     * @param url: url (connectorInfo contents)
     */
    public Boolean sendMsg(
            final JandiTopic trgetTopic,
            final String msg,
            final String title,
            final String url
    ) throws Exception {
        JandiApiSndMsgDto jandiMsg = new JandiApiSndMsgDto(msg);
        jandiMsg.addConnMsg(new JandiApiSndMsgConnectInfoDto(title, url, null));
        return this.sendMsg(trgetTopic, jandiMsg);
    }

    /**
     * API:: JANDI:: 대상 토픽에 따라 incoming webhook 요청 URL 세팅 (메소드 분리)
     *
     * @param trgetTopic: 메세지 발송 대상 토픽
     */
    public String setRequestUrlParam(final JandiTopic trgetTopic) {
        switch (trgetTopic) {
            case NOTICE:
                return URL_JANDI_NOTICE;
            case SCHDUL:
                return URL_JANDI_SCHDUL;
            case TEST:
                return URL_JANDI_TEST;
            default:
                return null;
        }
    }

    /**
     * API:: JANDI (incoming/outgoing)webhook :: 요청 헤더 세팅 (메소드 분리)
     *
     * @param jandiMsg: jandiMsg 객체
     */
    public HttpEntity<JandiApiSndMsgDto> setRequestHeader(final JandiApiSndMsgDto jandiMsg) throws Exception {
        log.debug("jandiMsg: {}", jandiMsg);
        String jsonStr = new ObjectMapper().writeValueAsString(jandiMsg);
        log.debug("jsonStr: {}", jsonStr);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.tosslab.jandi-v2+json");
        headers.set("Content-Type", "application/json; charset=utf-8");

        return new HttpEntity<JandiApiSndMsgDto>(jandiMsg, headers);
    }

    /**
     * API:: JANDI outgoing webhook :: 요청 헤더 세팅 (메소드 분리)
     * // TODO: 성공 메세지 발송
     *
     * @param rcvMsg: 잔디에서 수신받은 메세지
     */
    public Boolean receiveMsg(final JandiApiRcvMsgDto rcvMsg) {

        Boolean isSuccess = this.insertIntranet(rcvMsg);
        if (isSuccess) {
            // TODO: 성공 메세지 발송
            try {
                this.sendMsg(JandiTopic.TEST, "성공적으로 등록되었습니다.");
            } catch (Exception e) {
                MessageUtil.getExceptionMsg(e);
            }
        }

        return isSuccess;
    }

    /**
     * API:: JANDI outgoing webhook :: 요청 헤더 세팅 (메소드 분리)
     *
     * @param rcvMsg: 잔디에서 수신받은 메세지 (현재 미사용 중)
     */
    public Boolean insertIntranet(final JandiApiRcvMsgDto rcvMsg) {

        String rcvToken = rcvMsg.getToken();
        switch (rcvToken) {
            case TOKEN_VCATN:
                log.debug("token:: vac");
                return true;
            case TOKEN_NOTICE:
                log.debug("token:: notice");
                return true;
            default:
                return false;
        }
    }
}