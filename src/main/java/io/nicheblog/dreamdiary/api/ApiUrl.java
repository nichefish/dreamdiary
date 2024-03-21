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

    String PREFIX_API = "/api";

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

    /**
     * TODO: (API) NOTION : 노션 데이터 정보 조회
     */
    String PREFIX_NOTION = "/notion";
    String PREFIX_API_NOTION = PREFIX_API + PREFIX_NOTION;
    String API_NOTION_GET = PREFIX_API_NOTION + "/getNotionInfo.do";
    String API_NOTION_SPLITBEE_GET = PREFIX_API_NOTION + "/getNotionSplitbeeInfo.do";
}