package io.nicheblog.dreamdiary.api;

/**
 * ApiUrl
 * <pre>
 *  API:: 공통 상수:: API 호출 URL 정의
 * </pre>
 *
 * @author nichefish
 */
public interface ApiUrl {

    String JANDI_CONNECT_WH = "https://wh.jandi.com/connect-api/webhook";

    String PREFIX_API = "/api";

    /**
     *
     */
    String PREFIX_DREAM = "/dream";
    String PREFIX_API_DREAM = PREFIX_API + PREFIX_DREAM;
    String API_DREAM_DAY_LIST_AJAX = PREFIX_API_DREAM + "/dreamDayListAjax.do";
    String API_DREAM_DAY_REG_AJAX = PREFIX_API_DREAM + "/dreamDayRegAjax.do";
    String API_DREAM_DAY_DTL_AJAX = PREFIX_API_DREAM + "/dreamDayDtlAjax.do";
    String API_DREAM_DAY_MDF_AJAX = PREFIX_API_DREAM + "/dreamDayMdfAjax.do";

    /**
     * (API) 한국천문연구원 : 특일 정보 조회
     */
    String PREFIX_HLDY = "/hldy";
    String PREFIX_API_HLDY = PREFIX_API + PREFIX_HLDY;
    String API_HLDY_GET = PREFIX_API_HLDY + "/getHldyInfo.do";

    /**
     * (API) JANDI : 메세지 송수신
     */
    String PREFIX_JANDI = "/jandi";
    String PREFIX_API_JANDI = PREFIX_API + PREFIX_JANDI;
    String API_JANDI_SND_MSG = PREFIX_API_JANDI + "/sendMsg.do";
    String API_JANDI_RCV_MSG = PREFIX_API_JANDI + "/receiveMsg.do";
}