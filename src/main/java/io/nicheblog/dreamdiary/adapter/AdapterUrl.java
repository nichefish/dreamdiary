package io.nicheblog.dreamdiary.adapter;

/**
 * AdapterUrl
 * <pre>
 *  API:: 공통 상수:: 외부 연동에 사용하는 URL 정의.
 * </pre>
 *
 * @author nichefish
 */
public interface AdapterUrl {

    String JANDI_CONNECT_WH = "https://wh.jandi.com/connect-api/webhook";

    /**
     * (API) 저널 일자
     */
    String API_JRNL_DAY_LIST_AJAX = Prefix.API_JRNL_DAY + "/jrnlDayListAjax.do";
    String API_JRNL_DAY_DTL_AJAX = Prefix.API_JRNL_DAY + "/jrnlDayDtlAjax.do";

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
     * (API) SNMP : 메세지 발신
     */
    String URL_API_SNMP_SEND_AJAX = "/snmpSendAjax.do";

    /**
     * PREFIX 정보
     */
    interface Prefix {
        String API = "/api";
        // API:: 저널
        String API_JRNL = API + "/jrnl";
        String API_JRNL_DAY = API_JRNL + "/day";
        // API:: 잔디
        String API_JANDI = API + "/jandi";
        // API:: 특일정보 (공공데이터)
        String API_HLDY = API + "/hldy";
        // API:: 노션
        String API_NOTION = API + "/notion";
    }
}