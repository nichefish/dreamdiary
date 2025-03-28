package io.nicheblog.dreamdiary.global.util.cmm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import io.nicheblog.dreamdiary.global.Constant;
import io.nicheblog.dreamdiary.global.util.MessageUtils;
import io.nicheblog.dreamdiary.global.util.date.DateUtils;
import io.nicheblog.dreamdiary.global.validator.Regex;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        final ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(searchParam, HashMap.class);
    }

    /**
     * 공통 > Page 및 listIndex 정보 받아서 *역순* rownum 반환
     */
    public static Long getPageRnum(final Page<?> pageList, int i) {
        final Pageable pageable = pageList.getPageable();
        long totalCnt = pageList.getTotalElements();
        long offset = pageable.isPaged() ? pageable.getOffset() : 0L;
        return (totalCnt - offset - i);
    }

    /**
     * 공통 > map의 key값에 prefix/suffix를 붙여서 복사한다. (objectMapper 사전작업)
     */
    public static Map<?, ?> copyMap(final Map<?, ?> source, final String keyModifier, final String mode) {
        if (source == null) return null;
        final Map<String, Object> result = new HashMap<>();
        for (final Map.Entry<?, ?> stringObjectEntry : source.entrySet()) {
            final Object key = stringObjectEntry.getKey();
            final Object value = stringObjectEntry.getValue();
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
        final ObjectMapper mapper = new ObjectMapper();
        if ("SNAKE".equals(strategy)) {
            mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        }
        final Map<String, Object> paramMap = mapper.convertValue(object, HashMap.class);
        // map에서 value가 비어있는 key들을 걸러냄
        final Map<String, Object> filteredParamMap = CmmUtils.Param.filterParamMap(paramMap);
        // queryString으로 변환;
        return createParamStringFromMap(filteredParamMap);
    }


    /**
     * 공통 > map의 key-value값으로 paramString 생성
     */
    public String createParamStringFromMap(final Map<String, Object> paramMap) throws Exception {
        final StringBuilder paramData = new StringBuilder();
        for (Map.Entry<String, Object> param : paramMap.entrySet()) {
            if (!paramData.isEmpty()) paramData.append("&");
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
    public String removeHtmlTag(final String html) {
        return html.replaceAll(Regex.HTML_TAG_REGEX, "");
    }

    /**
     * 공통 > 년도, 월 담긴 Map 반환
     */
    public static Map<String, Object> getYyMhtnMap(final String yyStr, final String mnthStr) throws Exception {
        final Integer[] prevMnth = DateUtils.getPrevYyMnth();
        final int yy = (StringUtils.isNotEmpty(yyStr)) ? Integer.parseInt(yyStr) : prevMnth[0];
        final int mnth = (StringUtils.isNotEmpty(mnthStr)) ? Integer.parseInt(mnthStr) : prevMnth[1];
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

        final Map<String, String> resultMap = new HashMap<>();
        for (String param : queryString.split("&")) {
            final String[] pair = param.split("=");
            resultMap.put(pair[0], (pair.length > 1) ? pair[1] : "");
        }
        return resultMap;
    }

    /**
     * 공통 > 숫자 텍스트에 콤마 추가
     */
    public String thousandSeparator(final String value) {
        try {
            return new java.text.DecimalFormat("#,###").format(Integer.parseInt(value));
        } catch (final Exception e) {
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
        final JSONArray jArray = new JSONArray(tafigyStr);
        final List<String> strList = new ArrayList<>();
        for (int i = 0; i < jArray.length(); i++) {
            strList.add(jArray.getJSONObject(i).getString("value"));
        }
        return strList;
    }

    /**
     * 공통 > 마크다운 처리
     */
    public static String markdown(final String htmlContent) {
        final Document document = Jsoup.parseBodyFragment(htmlContent);
        final Elements paragraphs = document.select("p");
        procNodes(paragraphs);
        final Elements lis = document.select("li");
        procNodes(lis);

        // 일반 큰따옴표로 묶인 부분을 하이라이트 색상으로 표시
        return document.body().html(); // 변경된 HTML 반환
    }

    public static void procNodes(final Elements elements) {
        for (final Element elmt : elements) {
            // <pre> 태그는 처리하지 않음
            if (elmt.tagName().equalsIgnoreCase("pre")) continue;

            // <li>&nbsp;- 형태의 태그에 상하 간격 부여
            if (elmt.tagName().equalsIgnoreCase("li")) {
                String html = elmt.html();
                if (html.trim().startsWith("&nbsp;-")) elmt.addClass("my-2");
            }

            // 해당 요소의 모든 자식 노드를 순회
            for (final Node child : elmt.childNodes()) {
                if (child instanceof TextNode textNode) {
                    // 텍스트 노드의 경우, 마크다운 변환 로직을 적용
                    final String text = textNode.getWholeText();
                    final String processedText = procText(text); // 변환된 텍스트 처리 :: 메소드 분리
                    if (text.equals(processedText)) continue;
                    // 텍스트 노드에 HTML 코드를 직접 삽입
                    textNode.before(processedText);
                    textNode.remove();
                }
            }
        }
    }

    /**
     * 텍스트 마크다운 처리 :: 메소드 분리
     **/
    public static String procText(final String text) {
        final int MAX_GROUP_LENGTH = 3000;

        // 텍스트를 <pre> 태그 기준으로 분할, <pre> </pre> 사이는 처리하지 않음
        final String[] parts = text.split("(?i)(</?pre>)");
        final StringBuilder result = new StringBuilder();
        boolean insidePreTag = false;
        for (String part : parts) {
            if (part.equalsIgnoreCase("<pre>") || part.equalsIgnoreCase("</pre>")) {
                result.append(part);
                insidePreTag = !insidePreTag;
            } else if (insidePreTag) {
                result.append(part);
            } else {
                // " " 로 묶인 부분을 색상 처리
                final Pattern highlightPattern = Pattern.compile("\"(.*?)\"");
                final Matcher highlightMatcher = highlightPattern.matcher(part);
                while (highlightMatcher.find()) {
                    final String group = highlightMatcher.group(1);
                    if (group == null || group.length() > MAX_GROUP_LENGTH) continue;
                    part = part.replace("\"" + group + "\"", "<span class=\"text-dialog\">\"" + group + "\"</span>");
                }

                // -- -- 로 묶인 부분을 회색으로 표시하되, - - 처리
                final Pattern grayPattern = Pattern.compile("--(.*?)(--)");
                final Matcher grayMatcher = grayPattern.matcher(part);
                while (grayMatcher.find()) {
                    final String group = grayMatcher.group(1);
                    if (group == null || group.length() > MAX_GROUP_LENGTH) continue;
                    part = part.replace("--" + group + "--", "<span class='text-muted'>-" + group + "-</span>");
                }

                // !! !! 로 묶인 부분을 빨간색으로 표시하되, !! !! 제거
                final Pattern redPattern = Pattern.compile("!!(.*?)!!");
                final Matcher redMatcher = redPattern.matcher(part);
                while (redMatcher.find()) {
                    final String group = redMatcher.group(1);
                    if (group == null || group.length() > MAX_GROUP_LENGTH) continue;
                    part = part.replace("!!" + group + "!!", "<span class='text-danger'>" + group + "</span>");
                }

                // __ __ 로 묶인 부분을 밑줄 처리하되, __ __ 제거
                final Pattern underlinePattern = Pattern.compile("__(.*?)__");
                final Matcher underlineMatcher = underlinePattern.matcher(part);
                while (underlineMatcher.find()) {
                    final String group = underlineMatcher.group(1);
                    if (group == null || group.length() > MAX_GROUP_LENGTH) continue;
                    part = part.replace("__" + group + "__", "<span style='text-decoration: underline;'>" + group + "</span>");
                }

                // || || 로 묶인 부분을 강조 처리하되, || || 제거 및 우측 구분바 추가
                final Pattern pipelinePattern = Pattern.compile("\\|\\|(.*?)\\|\\|");
                final Matcher pipelineMatcher = pipelinePattern.matcher(part);
                while (pipelineMatcher.find()) {
                    String group = pipelineMatcher.group(1);
                    if (group == null || group.length() > MAX_GROUP_LENGTH) continue;
                    part = part.replace("||" + group + "||", "<span class='text-muted fw-bold border-end border-2 border-gray-400 pe-5 me-3'>" + group + "</span>");
                }

                // <@> .로 묶인 부분을 강조 처리
                final Pattern atPattern = Pattern.compile("<@>(.*?\\.)");
                final Matcher atMatcher = atPattern.matcher(part);
                final StringBuilder buffer = new StringBuilder();
                while (atMatcher.find()) {
                    final String group = atMatcher.group(1);
                    if (group == null || group.length() > MAX_GROUP_LENGTH) continue;
                    atMatcher.appendReplacement(buffer, "<span class=\"text-muted\">@" + group + "</span>");
                }
                atMatcher.appendTail(buffer); // 변환되지 않은 나머지 텍스트를 추가
                part = buffer.toString(); // 변환된 부분을 전체 텍스트에 반영

                result.append(part);
            }
        }

        return result.toString();
    }

    /**
     * 문자열을 Set<String>으로 변환하는 유틸 함수
     *
     * @param valueStr String
     * @param delimiter String
     * @return Set<String>
     */
    public static Set<String> parseToSet(final String valueStr, final String delimiter) {
        if (StringUtils.isEmpty(delimiter)) return parseToSet(valueStr, ",");

        return Arrays.stream(valueStr.split(delimiter))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}


