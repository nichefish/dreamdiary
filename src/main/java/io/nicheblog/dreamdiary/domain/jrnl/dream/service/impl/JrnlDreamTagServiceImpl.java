package io.nicheblog.dreamdiary.domain.jrnl.dream.service.impl;

import io.nicheblog.dreamdiary.auth.security.util.AuthUtils;
import io.nicheblog.dreamdiary.domain.jrnl.dream.entity.JrnlDreamTagEntity;
import io.nicheblog.dreamdiary.domain.jrnl.dream.mapstruct.JrnlDreamTagMapstruct;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamContentTagParam;
import io.nicheblog.dreamdiary.domain.jrnl.dream.model.JrnlDreamSearchParam;
import io.nicheblog.dreamdiary.domain.jrnl.dream.repository.jpa.JrnlDreamTagRepository;
import io.nicheblog.dreamdiary.domain.jrnl.dream.service.JrnlDreamTagService;
import io.nicheblog.dreamdiary.domain.jrnl.dream.spec.JrnlDreamTagSpec;
import io.nicheblog.dreamdiary.extension.clsf.tag.model.ContentTagCntDto;
import io.nicheblog.dreamdiary.extension.clsf.tag.model.TagDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * JrnlDreamTagService
 * <pre>
 *  저널 꿈 태그 서비스 모듈.
 * </pre>
 *
 * @author nichefish
 */
@Service("jrnlDreamTagService")
@RequiredArgsConstructor
@Log4j2
public class JrnlDreamTagServiceImpl
        implements JrnlDreamTagService {

    @Getter
    private final JrnlDreamTagRepository repository;
    @Getter
    private final JrnlDreamTagSpec spec;
    @Getter
    private final JrnlDreamTagMapstruct mapstruct = JrnlDreamTagMapstruct.INSTANCE;

    private final ApplicationContext context;
    private JrnlDreamTagService getSelf() {
        return context.getBean(this.getClass());
    }

    /**
     * 지정된 연도와 월을 기준으로 태그 목록을 캐시 처리하여 반환합니다.
     *
     * @param yy 조회할 연도
     * @param mnth 조회할 월
     * @return {@link List} -- 태그 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="myJrnlDreamTagList", key="T(io.nicheblog.dreamdiary.auth.security.util.AuthUtils).getLgnUserId() + \"_\" + #yy + \"_\" + #mnth")
    public List<TagDto> getListDtoWithCache(final Integer yy, final Integer mnth) throws Exception {
        final JrnlDreamSearchParam searchParam = JrnlDreamSearchParam.builder().yy(yy).mnth(mnth).build();

        return this.getSelf().getListDto(searchParam);
    }

    /**
     * css 사이즈 계산한 태그 목록 조회
     * 태그 1개 = 1. 그 외엔 2~9
     *
     * @param yy 조회할 연도
     * @param mnth 조회할 월
     * @return {@link List} -- CSS 사이즈가 적용된 태그 목록
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="myJrnlDreamSizedTagList", key="T(io.nicheblog.dreamdiary.auth.security.util.AuthUtils).getLgnUserId() + \"_\" + #yy + \"_\" + #mnth")
    public List<TagDto> getDreamSizedListDto(final Integer yy, final Integer mnth) throws Exception {
        // 저널 꿈 태그 Dto 목록 조회
        final List<TagDto> tagList = this.getSelf().getListDtoWithCache(yy, mnth);

        final int maxSize = this.calcMaxSize(tagList, yy, mnth);
        final int MIN_SIZE = 2; // 최소 크기
        final int MAX_SIZE = 9; // 최대 크기
        return tagList.stream()
                .peek(dto -> {
                    int size = dto.getContentSize();
                    if (size == 1) {
                        dto.setTagClass("ts-1");
                    } else {
                        final double ratio = (double) size / maxSize; // 사용 빈도의 비율 계산
                        final int tagSize = (int) (MIN_SIZE + (MAX_SIZE - MIN_SIZE) * ratio);
                        dto.setTagClass("ts-"+tagSize);
                    }
                })
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 최대 사용빈도 계산한 꿈 태그 목록 조회
     *
     * @param tagList 태그 목록 (List<TagDto>)
     * @param yy 조회할 년도
     * @param mnth 조회할 월
     * @return {@link Integer} -- 태그 목록에서 계산된 최대 사용 빈도 (Integer)
     */
    @Override
    public Integer calcMaxSize(final List<TagDto> tagList, Integer yy, Integer mnth) {
        int maxFrequency = 0;

        final JrnlDreamContentTagParam param = JrnlDreamContentTagParam.builder()
                .yy(yy)
                .mnth(mnth)
                .regstrId(AuthUtils.getLgnUserId())
                .build();
        final Map<Integer, Integer> tagCntMap = this.getSelf().countDreamSizeMap(param);

        for (final TagDto tag : tagList) {
            final Integer dreamSize = tagCntMap.getOrDefault(tag.getTagNo(), 0);
            tag.setContentSize(dreamSize);
            maxFrequency = Math.max(maxFrequency, dreamSize);
        }

        return maxFrequency;
    }

    /**
     * 꿈 태그별 크기 맵 조회
     *
     * @return {@link Map} -- 카테고리별 태그 목록을 담은 Map
     */
    @Override
    @Cacheable(value="myCountDreamSizeMap", key="T(io.nicheblog.dreamdiary.auth.security.util.AuthUtils).getLgnUserId() + \"_\" + #param.yy + \"_\" + #param.mnth")
    public Map<Integer, Integer> countDreamSizeMap(final JrnlDreamContentTagParam param) {
        final List<ContentTagCntDto> tagCountList = repository.countDreamSizeMap(param);

        // List를 태그 번호를 키로 하고, 태그 개수를 값으로 하는 Map으로 변환
        final ConcurrentMap<Integer, Integer> concurrentMap = tagCountList.stream()
                .collect(Collectors.toConcurrentMap(
                        ContentTagCntDto::getTagNo,
                        dto -> dto.getCount().intValue()   // Long을 int로 변환
                ));
        return new ConcurrentHashMap<>(concurrentMap);
    }

    /**
     * 지정된 연도와 월을 기준으로 태그 목록을 카테고리별로 그룹화하여 반환합니다.
     *
     * @param yy 조회할 연도
     * @param mnth 조회할 월
     * @return {@link Map} -- 카테고리별로 그룹화된 태그 목록을 담은 Map
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    public Map<String, List<TagDto>> getDreamSizedGroupListDto(final Integer yy, final Integer mnth) throws Exception {
        final List<TagDto> tagList = this.getSelf().getDreamSizedListDto(yy, mnth);

        // 태그를 카테고리별로 그룹화하여 맵으로 반환
        return tagList.stream()
                .collect(Collectors.groupingBy(TagDto::getCtgr));
    }

    /**
     * 태그 카테고리 맵을 반환합니다.
     *
     * @param userId 사용자 아이디
     * @return {@link Map} -- 태그 이름을 키로 하고, 카테고리 목록을 값으로 가지는 맵
     * @throws Exception 조회 중 발생할 수 있는 예외
     */
    @Override
    @Cacheable(value="myJrnlDreamTagCtgrMap", key="#userId")
    public Map<String, List<String>> getTagCtgrMap(final String userId) throws Exception {
        final HashMap<String, Object> paramMap = new HashMap<>() {{
            put("regstrId", userId);
        }};

        final List<JrnlDreamTagEntity> tagList = this.getSelf().getListEntity(paramMap);
        return tagList.stream()
                .collect(Collectors.groupingBy(
                        JrnlDreamTagEntity::getTagNm,
                        Collectors.mapping(tag -> {
                            if (StringUtils.isBlank(tag.getCtgr())) return "";
                            return tag.getCtgr();
                        }, Collectors.toList())
                ));
    }
}
