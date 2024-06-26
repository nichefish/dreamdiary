package io.nicheblog.dreamdiary.global.util.cmm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.global.validator.CmmRegex;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CmmUtils
 * <pre>
 *  공통, 기본 기능 처리 유틸리티 모듈
 * </pre>
 * TODO:: 필요별로 유틸 분리하고 필요하면 새로 만들기
 *
 * @author nichefish
 */
@Component
@Log4j2
public class CmmUtils {

    /** 파라피터 관련 메소드 분리 및 합성 */
    public static class Param extends ParamModule {}

    /**
     * 공통 > Object -> Map으로 변환
     */
    public static Map<String, Object> convertToMap(final Object searchParam) throws Exception {
        if (searchParam == null) return new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(searchParam, HashMap.class);
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
    public static Map<?, ?> copyMap(
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
        Map<String, Object> filteredParamMap = CmmUtils.Param.filterParamMap(paramMap);
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
    public static Map<String, Object> getYyMhtnMap(
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

    /**
     * 공통 > tagify 문자열 파싱
     */
    public static List<String> parseTagify(final String tafigyStr) {
        if (StringUtils.isEmpty(tafigyStr)) return new ArrayList<>();
        JSONArray jArray = new JSONArray(tafigyStr);
        List<String> strList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            strList.add(jArray.getJSONObject(i).getString("value"));
        }
        return strList;
    }

    /**
     * 공통 > 마크다운 처리
     */
    public static String markdown(String htmlContent) {
        Document document = Jsoup.parseBodyFragment(htmlContent);
        Elements paragraphs = document.select("p");
        procNodes(paragraphs);
        Elements lis = document.select("li");
        procNodes(lis);

        // 일반 큰따옴표로 묶인 부분을 하이라이트 색상으로 표시
        return document.body().html(); // 변경된 HTML 반환
    }

    public static void procNodes(Elements elements) {
        for (Element elmt : elements) {
            // <p> 태그 내의 모든 자식 노드를 순회
            for (Node child : elmt.childNodes()) {
                if (child instanceof TextNode) {
                    // 텍스트 노드의 경우, 마크다운 변환 로직을 적용
                    TextNode textNode = (TextNode) child;
                    String text = textNode.getWholeText();
                    String processedText = procText(text); // 변환된 텍스트 처리
                    if (text.equals(processedText)) continue;
                    // 텍스트 노드에 HTML 코드를 직접 삽입
                    textNode.before(processedText);
                    textNode.remove();
                }
            }
        }
    }

    public static String procText(String text) {

        // " " 로 묶인 부분을 색상 처리
        Pattern highlightPattern = Pattern.compile("\"(.*?)\"");
        Matcher highlightMatcher = highlightPattern.matcher(text);
        while (highlightMatcher.find()) {
            String group = highlightMatcher.group(1);
            text = text.replace("\"" + group + "\"", "<span class=\"text-dialog\">\"" + group + "\"</span>");
        }

        // -- -- 로 묶인 부분을 회색으로 표시하되, - - 처리
        Pattern grayPattern = Pattern.compile("--(.*?)(--)");
        Matcher grayMatcher = grayPattern.matcher(text);
        while (grayMatcher.find()) {
            String group = grayMatcher.group(1);
            text = text.replace("--" + group + "--", "<span class='text-muted'>-" + group + "-</span>");
        }

        // !! !! 로 묶인 부분을 빨간색으로 표시하되, !! !! 제거
        Pattern redPattern = Pattern.compile("!!(.*?)!!");
        Matcher redMatcher = redPattern.matcher(text);
        while (redMatcher.find()) {
            String group = redMatcher.group(1);
            text = text.replace("!!" + group + "!!", "<span class='text-danger'>" + group + "</span>");
        }

        // __ __ 로 묶인 부분을 밑줄 처리하되, __ __ 제거
        Pattern underlinePattern = Pattern.compile("__(.*?)__");
        Matcher underlineMatcher = underlinePattern.matcher(text);
        while (underlineMatcher.find()) {
            String group = underlineMatcher.group(1);
            text = text.replace("__" + group + "__", "<span style='text-decoration: underline;'>" + group + "</span>");
        }

        return text;
    }
}


