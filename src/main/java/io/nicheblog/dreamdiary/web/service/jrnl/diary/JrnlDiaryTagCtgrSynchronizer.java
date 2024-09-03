package io.nicheblog.dreamdiary.web.service.jrnl.diary;

import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JrnlDiaryTagCtgrSynchronizer
 * <pre>
 *  저널 일기 태그 카테고리 메타 파일-DB 동기화 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDiaryTagCtgrSynchronizer")
@Log4j2
public class JrnlDiaryTagCtgrSynchronizer {

    @Resource(name = "jrnlDiaryTagService")
    private JrnlDiaryTagService jrnlDreamTagService;

    /**
     * 태그 조회해서 파일 생성
     */
    @Transactional
    public void tagSync() throws Exception {

        List<JrnlDiaryTagEntity> tagList = jrnlDreamTagService.getListEntity(new HashMap<>());

        Map<String, List<String>> tagCtgryMap = tagList.stream()
                .collect(Collectors.groupingBy(
                        JrnlDiaryTagEntity::getTagNm,
                        Collectors.mapping(tag -> {
                            if (StringUtils.isBlank(tag.getCtgr())) return "";
                            return tag.getCtgr();
                        }, Collectors.toList())
                ));

        // 각 태그의 카테고리 리스트를 정렬
        tagCtgryMap.forEach((key, value) -> value.sort(String::compareTo));

        // 파일 생성 (메소드 분리)
        this.writeToFile(tagCtgryMap);
    }

    /**
     * 파일 생성 (메소드 분리)
     */
    private void writeToFile(Map<String, List<String>> tagCtgryMap) throws Exception {
        String FILE_PATH = "templates/view/jrnl/diary/tag/_jrnl_diary_tag_ctgr_map.ftlh";
        String MAP_NM = "jrnlDiary";

        try (FileWriter fileWriter = new FileWriter(FILE_PATH)) {
            fileWriter.write("<script>\n");
            fileWriter.write("\tif (typeof TagCtgrMap === 'undefined') { var TagCtgrMap = {}; }\n");
            fileWriter.write("\tTagCtgrMap." + MAP_NM + " = (function() {\n");
            fileWriter.write("\t\treturn {\n");

            if (tagCtgryMap.isEmpty()) {
                fileWriter.write("\t\t // tag list is empty. \n");
            } else {
                for (Map.Entry<String, List<String>> entry : tagCtgryMap.entrySet()) {
                    String tagName = entry.getKey();
                    List<String> ctgrList = entry.getValue();
                    String formattedCategories = ctgrList.stream()
                            .map(category -> "\"" + category + "\"")
                            .collect(Collectors.joining(", "));
                    fileWriter.write(String.format("\t\t\t\"%s\": [%s],\n", tagName, formattedCategories));
                }
            }

            fileWriter.write("\t\t}\n");
            fileWriter.write("\t})();\n");
            fileWriter.write("</script>\n");
        } catch (Exception e) {
            log.error("Failed to write tag category map to file", e);
        }
    }
}
