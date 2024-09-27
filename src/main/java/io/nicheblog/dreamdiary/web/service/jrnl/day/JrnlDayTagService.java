package io.nicheblog.dreamdiary.web.service.jrnl.day;

import io.nicheblog.dreamdiary.global.intrfc.service.BaseReadonlyService;
import io.nicheblog.dreamdiary.web.entity.jrnl.day.JrnlDayTagEntity;
import io.nicheblog.dreamdiary.web.mapstruct.jrnl.day.JrnlDayTagMapstruct;
import io.nicheblog.dreamdiary.web.model.cmm.tag.TagDto;
import io.nicheblog.dreamdiary.web.repository.jrnl.day.jpa.JrnlDayTagRepository;
import io.nicheblog.dreamdiary.web.spec.jrnl.day.JrnlDayTagSpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * JrnlDayTagService
 * <pre>
 *  저널 일기 태그 서비스 모듈
 * </pre>
 *
 * @author nichefish
 * @implements BaseCrudService:: 세부내용 변경시 해당 default 메소드 재정의(@Override)
 */
@Service("jrnlDayTagService")
@RequiredArgsConstructor
@Log4j2
public class JrnlDayTagService
        implements BaseReadonlyService<TagDto, TagDto, Integer, JrnlDayTagEntity, JrnlDayTagRepository, JrnlDayTagSpec, JrnlDayTagMapstruct> {

    private final JrnlDayTagRepository jrnlDayTagRepository;
    private final JrnlDayTagSpec jrnlDayTagSpec;
    private final JrnlDayTagMapstruct tagMapstruct = JrnlDayTagMapstruct.INSTANCE;

    @Autowired
    private ApplicationContext context;

    private JrnlDayTagService getSelf() {
        return context.getBean(JrnlDayTagService.class);
    }

    @Override
    public JrnlDayTagRepository getRepository() {
        return this.jrnlDayTagRepository;
    }
    @Override
    public JrnlDayTagSpec getSpec() {
        return this.jrnlDayTagSpec;
    }
    @Override
    public JrnlDayTagMapstruct getMapstruct() {
        return this.tagMapstruct;
    }

    @Cacheable(value="jrnlDayTagList", key="#yy + \"_\" + #mnth")
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
    @Cacheable(value="jrnlDaySizedTagList", key="#yy + \"_\" + #mnth")
    public List<TagDto> getDaySizedListDto(Integer yy, Integer mnth) throws Exception {
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
            Integer diarySize = this.getSelf().countDaySize(tag.getTagNo(), yy, mnth);
            tag.setContentSize(diarySize);
            maxFrequency = Math.max(maxFrequency, diarySize);
        }
        return maxFrequency;
    }

    /**
     * 일자 태그별 크기 조회
     */
    @Cacheable(value="countDaySize", key="#tagNo + \"_\" + #yy + \"_\" + #mnth")
    public Integer countDaySize(Integer tagNo, Integer yy, Integer mnth) {
        return jrnlDayTagRepository.countDiarySize(tagNo, yy, mnth);
    }

    public Map<String, List<TagDto>> getDaySizedGroupListDto(Integer yy, Integer mnth) throws Exception {

        List<TagDto> tagList = this.getDaySizedListDto(yy, mnth);

        // 태그를 카테고리별로 그룹화하여 맵으로 반환
        return tagList.stream()
                .collect(Collectors.groupingBy(TagDto::getCtgr));
    }
}
