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

    /**
     * (API) 꿈 관리
     */
    String API_DREAM_DAY_LIST_AJAX = Prefix.API_DREAM_DAY + "/dreamDayListAjax.do";
    String API_DREAM_DAY_DTL_AJAX = Prefix.API_DREAM_DAY + "/dreamDayDtlAjax.do";

    /**
     * (API) 한국천문연구원 : 특일 정보 조회
     */
    String API_HLDY_GET = Prefix.API_HLDY + "/getHldyInfo.do";

    /**
     * (API) JANDI : 메세지 송수신
     */
    String API_JANDI_SND_MSG = Prefix.API_JANDI + "/sendMsg.do";
    String API_JANDI_RCV_MSG = Prefix.API_JANDI + "/receiveMsg.do";


    String API_NOTION_GET = Prefix.API_NOTION + "/notion.do";

    /**
     * PREFIX 정보
     */
    interface Prefix {
        String API = "/api";
        String API_JANDI = API + "/jandi";
        String API_HLDY = API + "/hldy";
        String API_DREAM = API + "/dream";
        String API_DREAM_DAY = API_DREAM + "/day";
        String API_NOTION = API + "/notion";
    }
}