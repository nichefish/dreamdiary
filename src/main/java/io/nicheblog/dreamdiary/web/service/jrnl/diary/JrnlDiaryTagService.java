package io.nicheblog.dreamdiary.web.service.jrnl.diary;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import io.nicheblog.dreamdiary.web.entity.jrnl.diary.JrnlDiaryTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.diary.JrnlDiaryTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.diary.jpa.JrnlDiaryTagRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.diary.JrnlDiaryTagSpec;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JrnlDiaryTagService
 * <pre>
 *  저널 일기 태그 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDiaryTagService")
@Log4j2
public class JrnlDiaryTagService
        implements BaseReadonlyService<TagDto, TagDto, Integer, JrnlDiaryTagEntity, JrnlDiaryTagRepository, JrnlDiaryTagSpec, JrnlDiaryTagMapstruct> {

    private final JrnlDiaryTagMapstruct tagMapstruct = JrnlDiaryTagMapstruct.INSTANCE;

    @Resource(name = "jrnlDiaryTagRepository")
    private JrnlDiaryTagRepository jrnlDiaryTagRepository;
    @Resource(name = "jrnlDiaryTagSpec")
    private JrnlDiaryTagSpec jrnlDiaryTagSpec;

    @Autowired
    private ApplicationContext context;

    private JrnlDiaryTagService getSelf() {
        return context.getBean(JrnlDiaryTagService.class);
    }

    @Override
    public JrnlDiaryTagRepository getRepository() {
        return this.jrnlDiaryTagRepository;
    }
    @Override
    public JrnlDiaryTagSpec getSpec() {
        return this.jrnlDiaryTagSpec;
    }
    @Override
    public JrnlDiaryTagMapstruct getMapstruct() {
        return this.tagMapstruct;
    }

    @Cacheable(value="jrnlDiaryTagList", key="#yy + \"_\" + #mnth")
    public List<TagDto> getListDtoWithCache(Integer yy, Integer mnth) throws Exception {
        Map<String, Object> searchParamMap = new HashMap<>() {{
            put("yy", yy);
            put("mnth", mnth);
        }};
        return this.getSelf().getListDto(searchParamMap);
    }

    /**
     * css 사이즈 계산한 태그 목록 조회
     * 태그 1개 = 1. 그 외엔 2~9
     */
    @Cacheable(value="jrnlDiarySizedTagList", key="#yy + \"_\" + #mnth")
    public List<TagDto> getDiarySizedListDto(Integer yy, Integer mnth) throws Exception {
        // 저널 꿈 태그 DTO 목록 조회
        List<TagDto> tagList = this.getSelf().getListDtoWithCache(yy, mnth);

        int maxSize = this.calcMaxSize(tagList, yy, mnth);
        final int MIN_SIZE = 2; // 최소 크기
        final int MAX_SIZE = 9; // 최대 크기
        return tagList.stream()
                .peek(dto -> {
                    int size = dto.getContentSize();
                    if (size == 1) {
                        dto.setTagClass("ts-1");
                    } else {
                        double ratio = (double) size / maxSize; // 사용 빈도의 비율 계산
                        int tagSize = (int) (MIN_SIZE + (MAX_SIZE - MIN_SIZE) * ratio);
                        dto.setTagClass("ts-"+tagSize);
                    }
                })
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 최대 사용빈도 계산한 꿈 태그 목록 조회
     */
    public Integer calcMaxSize(List<TagDto> tagList, Integer yy, Integer mnth) {
        int maxFrequency = 0;
        for (TagDto tag : tagList) {
            // 캐싱 처리 위해 셀프 프록시
            Integer diarySize = this.getSelf().countDiarySize(tag.getTagNo(), yy, mnth);
            tag.setContentSize(diarySize);
            maxFrequency = Math.max(maxFrequency, diarySize);
        }
        return maxFrequency;
    }

    /**
     * 꿈 태그별 크기 조회
     */
    @Cacheable(value="countDiarySize", key="#tagNo + \"_\" + #yy + \"_\" + #mnth")
    public Integer countDiarySize(Integer tagNo, Integer yy, Integer mnth) {
        return jrnlDiaryTagRepository.countDiarySize(tagNo, yy, mnth);
    }

    public Map<String, List<TagDto>> getDiarySizedGroupListDto(Integer yy, Integer mnth) throws Exception {

        List<TagDto> tagList = this.getDiarySizedListDto(yy, mnth);

        // 태그를 카테고리별로 그룹화하여 맵으로 반환
        return tagList.stream()
                .collect(Collectors.groupingBy(TagDto::getCtgr));
    }
}
