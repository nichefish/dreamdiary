package io.nicheblog.dreamdiary.global.util.cmm;

import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ParamModule
 * <pre>
 *  공통, 기본 기능 처리 유틸리티 모듈
 *  (!package-private class)
 * </pre>
 * TODO:: 필요별로 유틸 분리하고 필요하면 새로 만들기
 *
 * @author nichefish
 */
class ParamModule {

    /**
     * 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
     */
    public static BaseSearchParam checkPrevSearchParam(
            final String listUrl,
            final BaseSearchParam searchParam
    ) {

        if (StringUtils.isEmpty(listUrl)) return searchParam;

        // 세션에서 이전 정보 조회, 이전 정보가 있을 경우 이전 정보 반환
        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = servletRequestAttribute.getRequest().getSession(true);
        // prevListUrl과 prevSearchParam은 한 세트. prevListUrl이 있으면 prevSearchParam 반환
        String prevListUrl = (String) session.getAttribute("prevListUrl");
        if (StringUtils.isEmpty(prevListUrl) || !listUrl.equals(prevListUrl)) return searchParam;

        return (BaseSearchParam) session.getAttribute("prevSearchParam");
    }

    /**
     * 공통 > pageSize, pageNo로 페이징 요청 정보 생성
     */
    public static PageRequest getPageRequest(
            final BaseSearchParam searchParam,
            final String sortParam,
            final ModelMap model
    ) throws Exception {

        Sort sort = Sort.by(Sort.Direction.DESC, sortParam);
        return getPageRequest(searchParam, sort, model);
    }

    /**
     * 공통 > pageSize, pageNo로 페이징 요청 정보 생성
     */
    public static PageRequest getPageRequest(
            final BaseSearchParam searchParam,
            final Sort sort,
            final ModelMap model
    ) throws Exception {

        Integer pageSize = searchParam.getPageSize();
        Integer pageNo = searchParam.getPageNo();
        if (model != null) {
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("pageNo", pageNo);
        }
        int pageIdx = pageNo - 1;
        return PageRequest.of(pageIdx, pageSize, sort);
    }

    /**
     * 공통 > 처리를 마친 parameterMap 값을 공백 제거하여 entrySet으로 화면에 추가
     */
    public static void setModelAttrMap(
            final BaseSearchParam searchParam,
            final String listUrl,
            final ModelMap model
    ) {

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = servletRequestAttribute.getRequest().getSession(true);

        // 내 글 보기 체크시 목록 돌아가기 버튼 보여지기 위해 값 저장
        boolean isMyPapr = !searchParam.isBackToList() && searchParam.isAction(Constant.ACTION_TY_MY_PAPR);
        boolean isBackToMyPapr = searchParam.isBackToList() && (Constant.ACTION_TY_MY_PAPR.equals(searchParam.getActionTyCd()));
        if (isMyPapr || isBackToMyPapr) model.addAttribute(Constant.ACTION_TY_MY_PAPR, true);

        // 목록 URL 모델에 추가 (검색 공통사용 용도)
        model.addAttribute(Constant.LIST_URL, listUrl);

        // 세션?에 목록 검색 인자 저장
        session.setAttribute("prevSearchParam", searchParam);
        session.setAttribute("prevListUrl", listUrl);
    }

    /**
     * 공통 > 목록 검색 parameterMap 빈 값 걸러내고 정돈
     */
    public static Map<String, Object> filterParamMap(final Map<String, Object> searchParamMap) throws Exception {
        Map<String, Object> filteredSearchKey = new HashMap<>();
        // 목록 검색에서 시작일, 종료일이 같은 날짜(문자열)로 넘어올 경우 searchEndDt를 23:59:59로 세팅
        Object searchStartDt = searchParamMap.get("searchStartDt");
        if (searchStartDt instanceof String) {
            String searchStartDtStr = (String) searchStartDt;
            if (StringUtils.isNotEmpty(searchStartDtStr)) {
                String searchEndDtStr = (String) searchParamMap.get("searchEndDt");
                if (searchStartDtStr.equals(searchEndDtStr)) {
                    Date searchEndDt = DateUtils.asDate(searchEndDtStr);
                    searchParamMap.put("searchEndDt", DateUtils.Parser.eDateParseStr(searchEndDt));
                }
            }
        }
        // Parameter 순차적으로 세팅
        for (String key : searchParamMap.keySet()) {
            // pageNo, pageSize는 검색인자가 아니므로 여기 들어갈 필요가 없다.
            if ("pageNo".equals(key)) continue;
            if ("pageSize".equals(key)) continue;
            Object value = searchParamMap.get(key);
            String valueStr = String.valueOf(searchParamMap.get(key));
            if (StringUtils.isNotEmpty(valueStr) && !"null".equals(valueStr)) {
                // 날짜 파라미터 세팅 ("Dt"로 끝나는 입력값은 Date로 변환하여 Dt에 담음)
                if (key.endsWith("Dt")) filteredSearchKey.put(key, DateUtils.asDate(value));
                // searchEndDt 문자열 :: 끝에 강제로 23:59:59 붙여줌 (yyyy-MM-dd까지만 받기때문)
                if (key.equals("searchEndDt")) {
                    filteredSearchKey.put(key, DateUtils.Parser.eDateParse(value));
                    continue;
                }
                // searchType + searchKeyword 매칭 (인덱스마다 자동 설정) (ex.searchType1 <- searchKeyword1)
                if (key.startsWith("searchType")) {
                    String idx = key.replace("searchType", "");
                    String searchKeyword = String.valueOf(searchParamMap.get("searchKeyword" + idx));
                    if (StringUtils.isNotEmpty(searchKeyword) && !"null".equals(searchKeyword)) {
                        filteredSearchKey.put(valueStr, searchKeyword);
                    }
                    continue;
                }
                if (key.startsWith("searchKeyword")) continue;
                if (key.endsWith("Dt")) continue;
                filteredSearchKey.put(key, value);
            }
        }
        return filteredSearchKey;
    }


    public void streSearchParam(
            final String listUrl,
            final BaseSearchParam searchParam
    ) {

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = servletRequestAttribute.getRequest().getSession(true);
        // 세션?에 목록 검색 인자 저장
        session.setAttribute("prevSearchMap", searchParam);
        session.setAttribute("prevListUrl", listUrl);
    }
}