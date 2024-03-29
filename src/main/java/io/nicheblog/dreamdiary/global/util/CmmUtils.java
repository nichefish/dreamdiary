package io.nicheblog.dreamdiary.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.validator.CmmRegex;
import io.nicheblog.dreamdiary.global.intrfc.model.param.BaseSearchParam;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CmmUtils
 * <pre>
 *  공통, 기본 기능 처리 유틸리티 모듈
 * </pre>
 * TODO:: 필요별로 유틸 분리하고 필요하면 새로 만들기
 *
 * @author nichefish
 */
@UtilityClass
@Log4j2
public class CmmUtils {

    /**
     * 상세/수정 화면에서 목록 화면 복귀시 세션에 목록 검색 인자 저장해둔 거 있는지 체크
     */
    public Map<String, Object> checkPrevSearchMap(
            final Map<String, Object> searchParamMap,
            final String listUrl,
            final BaseSearchParam searchParam
    ) {
        // 세션?에서 목록 검색 인자 저장해둔 거 있는지 체크 :: 메소드 분리
        if (!searchParam.isBackToList()) return searchParamMap;

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = servletRequestAttribute.getRequest().getSession(true);

        Map<String, Object> prevSearchMap = (Map<String, Object>) session.getAttribute("prevSearchMap");
        String prevListUrl = (String) session.getAttribute("prevListUrl");
        boolean hasPrevSearchMap = listUrl.equals(prevListUrl) && prevSearchMap != null;
        if (!hasPrevSearchMap) return searchParamMap;
        return (Map<String, Object>) copyMap(prevSearchMap, "", null);
    }

    /**
     * 공통 > 처리를 마친 parameterMap 값을 공백 제거하여 entrySet으로 화면에 추가
     */
    public void setModelAttrMap(
            final Map<String, Object> searchParamMap,
            final BaseSearchParam param,
            final String listUrl,
            final ModelMap model
    ) {
        // 빈 값 제거
        searchParamMap.keySet()
                .removeIf(key -> StringUtils.isEmpty((String) searchParamMap.get(key)));
        // mustache에서 처리할 수 있도록 entrySet으로 모델에 추가 :: Freemarker의 경우 바로 HashMap으로 추가해야함
        //        Set<Map.Entry<String,Object>> entrySet = searchParamMap.entrySet();
        //        log.debug("entrySet: {}", entrySet);
        model.addAttribute("searchParamMap", searchParamMap);

        ServletRequestAttributes servletRequestAttribute = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = servletRequestAttribute.getRequest().getSession(true);

        // 내 글 보기 체크시 목록 돌아가기 버튼 보여지기 위해 값 저장
        boolean isMyPapr = !param.isBackToList() && param.isAction(Constant.ACTION_TY_MY_PAPR);
        boolean isBackToMyPapr = param.isBackToList() && (Constant.ACTION_TY_MY_PAPR.equals(searchParamMap.get("actionTyCd")));
        if (isMyPapr || isBackToMyPapr) model.addAttribute(Constant.ACTION_TY_MY_PAPR, true);

        // 목록 URL 모델에 추가 (검색 공통사용 용도)
        model.addAttribute(Constant.LIST_URL, listUrl);

        // 세션?에 목록 검색 인자 저장
        session.setAttribute("prevSearchMap", searchParamMap);
        session.setAttribute("prevListUrl", listUrl);
    }



    /**
     * 공통 > Object -> Map으로 변환
     */
    public static Map<String, Object> convertParamToMap(final Object searchParam) throws Exception {
        if (searchParam == null) return new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(searchParam, HashMap.class);
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
                    searchParamMap.put("searchEndDt", DateParser.eDateParseStr(searchEndDt));
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
                    filteredSearchKey.put(key, DateParser.eDateParse(value));
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

    /**
     * 공통 > pageSize, pageNo로 페이징 요청 정보 생성
     */
    public static PageRequest getPageRequest(
            final Map<String, Object> searchParamMap,
            final String sortParam,
            final ModelMap model
    ) throws Exception {
        Sort sort = Sort.by(Sort.Direction.DESC, sortParam);
        return getPageRequest(searchParamMap, sort, model);
    }

    public static PageRequest getPageRequest(
            final Map<String, Object> searchParamMap,
            final Sort sort,
            final ModelMap model
    ) throws Exception {
        String pageSizeStr = (String) searchParamMap.get("pageSize");
        String pageNoStr = (String) searchParamMap.get("pageNo");
        int pageSize = StringUtils.isNotEmpty(pageSizeStr) ? Integer.parseInt(pageSizeStr) : 10;
        int pageNo = StringUtils.isNotEmpty(pageNoStr) ? Integer.parseInt(pageNoStr) : 1;
        if (model != null) {
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("pageNo", pageNo);
        }
        int pageIdx = pageNo - 1;
        return PageRequest.of(pageIdx, pageSize, sort);
    }

    /**
     * 공통 > Page 및 listIndex 정보 받아서 *역순* rownum 반환
     */
    public static Long getPageRnum(
            Page<?> pageList,
            int i
    ) {
        Pageable pageable = pageList.getPageable();
        long totalCnt = pageList.getTotalElements();
        long offset = pageable.isPaged() ? pageable.getOffset() : 0L;
        return (totalCnt - offset - i);
    }

    /**
     * 공통 > map의 key값에 prefix/suffix를 붙여서 복사한다. (objectMapper 사전작업)
     */
    public Map<?, ?> copyMap(
            final Map<?, ?> source,
            String keyModifier,
            final String mode
    ) {
        if (source == null) return null;
        Map<String, Object> result = new HashMap<>();
        for (Object stringObjectEntry : source.entrySet()) {
            Object key = ((Map.Entry<?, ?>) stringObjectEntry).getKey();
            Object value = ((Map.Entry<?, ?>) stringObjectEntry).getValue();
            if (mode == null) {
                result.put(key.toString(), value);
            } else if (Constant.PREFIX.equals(mode)) {
                result.put(keyModifier + key.toString(), value);
            } else if (Constant.SUFFIX.equals(mode)) {
                result.put(key.toString() + keyModifier, value);
            }
        }
        return result;
    }

    /**
     * 공통 > dto의 property 값으로 paramString 생성
     * 기본 :: 프로퍼티 그대로 변환
     */
    @SuppressWarnings("deprecation")
    public String createQueryStringFromObject(final Object object) throws Exception {
        // object -> hashMap
        return createQueryStringFromObject(object, null);
    }

    /**
     * 공통 > dto의 property 값으로 paramString 생성
     * (SNAKE CASE 별도 처리 가능)
     */
    @SuppressWarnings("deprecation")
    public String createQueryStringFromObject(
            final Object object,
            final String strategy
    ) throws Exception {
        // object -> hashMap
        ObjectMapper mapper = new ObjectMapper();
        if ("SNAKE".equals(strategy)) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        }
        Map<String, Object> paramMap = mapper.convertValue(object, HashMap.class);
        // map에서 value가 비어있는 key들을 걸러냄
        Map<String, Object> filteredParamMap = CmmUtils.filterParamMap(paramMap);
        // queryString으로 변환;
        return createParamStringFromMap(filteredParamMap);
    }


    /**
     * 공통 > map의 key-value값으로 paramString 생성
     */
    public String createParamStringFromMap(final Map<String, Object> paramMap) throws Exception {
        StringBuilder paramData = new StringBuilder();
        for (Map.Entry<String, Object> param : paramMap.entrySet()) {
            if (paramData.length() != 0) paramData.append("&");
            paramData.append(URLEncoder.encode(param.getKey(), StandardCharsets.UTF_8));
            paramData.append("=");
            paramData.append(URLEncoder.encode(String.valueOf(param.getValue()), StandardCharsets.UTF_8));
        }
        log.info("creating paramString... paramData: {}, paramString: {}", paramData, paramData.toString());
        return paramData.toString();
    }

    /**
     * 공통 > html 태그 제거 (정규식)
     */
    public String removeHtmlTag(String html) {
        return html.replaceAll(CmmRegex.HTML_TAG_REGEX, "");
    }

    /**
     * 공통 > 년도, 월 담긴 Map 반환
     */
    public Map<String, Object> getYyMhtnMap(
            final String yyStr,
            final String mnthStr
    ) throws Exception {
        Integer[] prevMnth = DateUtils.getPrevYyMnth();
        int yy = (StringUtils.isNotEmpty(yyStr)) ? Integer.parseInt(yyStr) : prevMnth[0];
        int mnth = (StringUtils.isNotEmpty(mnthStr)) ? Integer.parseInt(mnthStr) : prevMnth[1];
        return new HashMap<>() {{
            put("yy", yy);
            put("mnth", mnth);
        }};
    }


    /**
     * 쿼리스트링을 Map에 담아서 반환
     */
    public Map<String, String> queryStringToMap(final String queryString) {
        log.info("queryString: {}", queryString);
        if (StringUtils.isEmpty(queryString)) return null;
        Map<String, String> resultMap = new HashMap<>();
        for (String param : queryString.split("&")) {
            String[] pair = param.split("=");
            resultMap.put(pair[0], (pair.length > 1) ? pair[1] : "");
        }
        return resultMap;
    }

    /**
     * 공통 > 숫자 텍스트에 콤마 추가
     */
    public String addComma(final String value) {
        try {
            return new java.text.DecimalFormat("#,###").format(Integer.parseInt(value));
        } catch (Exception e) {
            MessageUtils.getExceptionMsg(e);
            return "";
        }
    }

    /**
     * 공통 > 숫자 텍스트에서 콤마 제거
     */
    public String removeComma(final String value) {
        return value.replaceAll("\\,", "");
    }
}